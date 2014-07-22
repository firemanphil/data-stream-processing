package com.datasift.datastreamprocessing;

import com.datasift.client.DataSiftClient;
import com.datasift.client.DataSiftConfig;
import com.datasift.client.core.Stream;
import com.datasift.client.stream.*;
import com.datasift.datastreamprocessing.buffer.InteractionBuffer;
import com.datasift.datastreamprocessing.downstream.HttpDownstreamHandler;

/**
 * @author philipince
 *         Date: 22/07/2014
 *         Time: 10:09
 */
public class App {

    public static void main(String args[]){
        if(args.length != 4){
            System.err.println("Requires 4 arguments: <datasift username> <datasift api key> <datasift stream hash> <url to post to>");
            System.exit(1);
        }
        DataSiftConfig config = new DataSiftConfig();
        config.auth(args[0], args[1]);
        final DataSiftClient datasift = new DataSiftClient(config);

        Stream stream = Stream.fromString(args[2]);

        datasift.liveStream().onError(new ErrorHandler());

        datasift.liveStream().onStreamEvent(new StreamEventListener() {
            @Override
            public void onDelete(DeletedInteraction di) {
                // ignore
            }
        });
        InteractionBuffer incomingBuffer = new InteractionBuffer();
        InteractionBuffer downstreamBuffer = new InteractionBuffer();

        incomingBuffer.addConsumer(new GoogleRequestor(downstreamBuffer));
        downstreamBuffer.addConsumer(new HttpDownstreamHandler(args[3]));

        new Thread(incomingBuffer).start();
        new Thread(downstreamBuffer).start();
        datasift.liveStream().subscribe(new Subscription(stream, incomingBuffer));
    }

    private static class ErrorHandler extends ErrorListener {

        @Override
        public void exceptionCaught(Throwable t) {
            t.printStackTrace();
        }
    }


    private static class Subscription extends StreamSubscription {

        private final InteractionBuffer incomingBuffer;
        public Subscription(Stream stream, InteractionBuffer incomingBuffer) {
            super(stream);
            this.incomingBuffer = incomingBuffer;
        }

        @Override
        public void onDataSiftLogMessage(DataSiftMessage dm) {
            System.out.println(dm.toString());
        }

        @Override
        public void onMessage(Interaction i) {
            incomingBuffer.consume(i);
        }
    }
}
