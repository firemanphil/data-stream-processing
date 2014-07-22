package com.datasift.datastreamprocessing.downstream;

import com.datasift.client.stream.Interaction;
import com.datasift.datastreamprocessing.InteractionConsumer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author philipince
 *         Date: 22/07/2014
 *         Time: 11:21
 */
public class HttpDownstreamHandler implements InteractionConsumer {

    private String urlToSendTo;

    public HttpDownstreamHandler(String urlToSendTo) {
        this.urlToSendTo = urlToSendTo;
    }


    @Override
    public void consume(Interaction i) {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(urlToSendTo);

            StringRequestEntity ent = new StringRequestEntity(i.toString(),"application/json","UTF-8");
            method.setRequestEntity(ent);
            int statusCode = client.executeMethod(method);
            System.out.println("received "+statusCode+" from server");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
