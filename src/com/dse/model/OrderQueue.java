package com.dse.model;

import java.util.PriorityQueue;

public class OrderQueue extends PriorityQueue<Order> {


    private final Object queueLock = new Object();

    @Override
    public Order poll() {
        return super.poll();
    }

    public Object getQueueLock() {
        return queueLock;
    }

}
