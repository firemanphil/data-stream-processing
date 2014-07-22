package com.datasift.datastreamprocedssing;

import com.datasift.datastreamprocessing.GoogleRequestor;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * @author philipince
 *         Date: 22/07/2014
 *         Time: 11:11
 */
public class TestGoogleRequestor {
    @Test
    public void shouldReturnTenResultsForGoogle(){
        GoogleRequestor requestor = new GoogleRequestor(null);
        ArrayNode restults = requestor.retrieveSearchResults("google", 10);
        assertTrue(restults.size()==10);
    }

}
