package com.dse.model;

import java.util.List;
import java.util.PriorityQueue;

public class ItemQueue<T>  {

    private PriorityQueue<T> queue = new PriorityQueue<>();

    private final Object queueLock = new Object();
    public void add(T order){
        queue.add(order);
    }
    public T peek(){
        return queue.peek();
    }

    public T poll(){
        return queue.poll();
    }

    public List<T> peekItems(){
        return queue.stream().sorted().toList();
    }

    public int size(){
        return queue.size();
    }

    public Object getQueueLock() {
        return queueLock;
    }

}
