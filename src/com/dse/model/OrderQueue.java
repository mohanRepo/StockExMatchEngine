package com.dse.model;

import java.util.PriorityQueue;

public class OrderQueue extends PriorityQueue<Order> {

    @Override
    public Order poll() {
        Order poll = super.poll();




        return poll;
    }
}
