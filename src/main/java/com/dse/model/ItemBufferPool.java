package com.dse.model;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemBufferPool<T>  implements Publisher<T> {

    private AtomicInteger processingCount = new AtomicInteger(0);

    private Map<Long , T>  itemBuffer = new ConcurrentHashMap<>();

    private BlockingQueue<T> bufferQueue = new LinkedBlockingDeque<>();

    private final List<Subscriber<T>> subscribers = new ArrayList<>();

    private final int processingLimit = 2;

    public void addItem(T item){

        bufferQueue.add(item);

        notifyCollectionChange();


    }

    private Object lockObj = new Object();

    private void notifyCollectionChange(){
        int delta = processingLimit - processingCount.get();
        if(delta > 0) {
            synchronized (bufferQueue) {
               List<T> temp = new ArrayList<>();
               bufferQueue.drainTo(temp , delta);
               temp.forEach( t -> {
                   sendMessage("" , t);
               });
            }
        }

    }


    private int incrementProcessing(){
        return processingCount.incrementAndGet();
    }
    public void  releaseProcessing(){
        int i = processingCount.decrementAndGet();
        notifyCollectionChange();

    }

    void drainToProcessors()
    {

    }


    @Override
    public void register(Subscriber<T> subscriber) {

        subscribers.add(subscriber);
    }

    @Override
    public void sendMessage(String topic, T message) {
                subscribers.forEach( s ->{
                    incrementProcessing();
                    s.onMessage(topic , message);
                });

    }
}