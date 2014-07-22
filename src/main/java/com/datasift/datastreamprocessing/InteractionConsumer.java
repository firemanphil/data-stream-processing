package com.datasift.datastreamprocessing;

import com.datasift.client.stream.Interaction;

/**
 *
 * Something that consumes DataSift interactions
 *
 * @author philipince
 *         Date: 22/07/2014
 *         Time: 11:47
 */
public interface InteractionConsumer {

    void consume(Interaction i);

}
