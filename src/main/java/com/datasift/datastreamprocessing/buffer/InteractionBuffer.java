package com.datasift.datastreamprocessing.buffer;

import com.datasift.client.stream.Interaction;
import com.datasift.datastreamprocessing.InteractionConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author philipince
 *         Date: 22/07/2014
 *         Time: 11:44
 */
public class InteractionBuffer implements Runnable, InteractionConsumer {

    private BlockingQueue<Interaction> queue = new LinkedBlockingQueue<Interaction>(10000);

    private List<InteractionConsumer> consumerList = new ArrayList();

    public void addToBuffer(Interaction i){
        queue.add(i);
    }

    public void addConsumer(InteractionConsumer consumer){
        consumerList.add(consumer);
    }


    public void run(){
        if(consumerList.isEmpty()){
            throw new RuntimeException("can't run buffer wihtout consumers");
        }

        try {
            Interaction i;
            while(true){
                i = queue.take();
                for (InteractionConsumer consumer : consumerList){
                    consumer.consume(i);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void consume(Interaction i) {
        if(i!=null) {
            addToBuffer(i);
        }
    }

}
