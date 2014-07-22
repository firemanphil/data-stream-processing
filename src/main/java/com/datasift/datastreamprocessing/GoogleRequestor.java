package com.datasift.datastreamprocessing;

import com.datasift.client.stream.Interaction;
import com.datasift.datastreamprocessing.buffer.InteractionBuffer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import static java.net.URLEncoder.encode;

/**
 *
 * Uses the Google search AJAX api to pull down results
 *
 * @author philipince
 *         Date: 22/07/2014
 *         Time: 10:39
 */
public class GoogleRequestor implements InteractionConsumer {

    private static final String GOOGLE_SEARCH_API_ADDRESS = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
    private static final String ENCODING_CHARSET = "UTF-8";
    private static final int RESULTS_RETURN_SIZE = 4;
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    InteractionBuffer downstreamBuffer;


    public GoogleRequestor(InteractionBuffer downstreamBuffer){
        this.downstreamBuffer  = downstreamBuffer;
    }

    public ArrayNode retrieveSearchResults(String topic, int noOfResults){
        ArrayNode fullResultSet = null;
        try {
            for (int i = 0; i < (noOfResults) ; i+= RESULTS_RETURN_SIZE) {
                JsonNode batchOfResults = retrieveBatch(topic, i);
                ArrayNode arrayBatch = (ArrayNode) batchOfResults.findValue("results");
                if(fullResultSet==null){
                    fullResultSet = arrayBatch;
                } else {
                    if(arrayBatch!=null) {
                        fullResultSet.addAll(arrayBatch);
                    }
                }
            }
            if(fullResultSet==null){
                return null;
            }
            while(fullResultSet.size() > noOfResults){
                fullResultSet.remove(fullResultSet.size()-1);
            }
            return fullResultSet;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private JsonNode retrieveBatch(String topic, int batchStart) throws IOException {
        URL url = new URL(GOOGLE_SEARCH_API_ADDRESS + urlEncode(topic) + "&start="+batchStart);
        JsonParser jp = factory.createJsonParser(url.openStream());
        return mapper.readTree(jp);
    }

    public static String urlEncode(String string) {
        if (string == null)
            return null;
        try {
            return encode(string, ENCODING_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Platform doesn't support " + ENCODING_CHARSET, e);
        }
    }

    @Override
    public void consume(Interaction i) {
        String topic = i.getData().findValue("title").asText();
        ArrayNode results = retrieveSearchResults(topic, 10);
        ((ObjectNode) i.getData()).put("google_results",results);
        if(downstreamBuffer!=null) {
            downstreamBuffer.consume(i);
        }
    }
}
