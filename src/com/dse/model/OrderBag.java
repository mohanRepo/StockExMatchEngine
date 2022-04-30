package com.dse.model;

import com.dse.enums.Side;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class OrderBag {


    void add(Order order) {
        if (order.getSide() == Side.BUY) {
            buyOrders.add(order);
        }
        else {
            sellOrders.add(order);
        }

        message(order.getSecurity() , order);
    }

    final PriorityQueue<Order> sellOrders = new PriorityQueue<>();

    public PriorityQueue<Order> getBuyOrders() {
        return buyOrders;
    }

    public Semaphore getOrderBagLock() {
        return orderBagLock;
    }

    public void waitForOrderExecution(){
        orderBagLock.acquireUninterruptibly();
    }
    public void releaseForOrderExecution()  {
        orderBagLock.release();
    }

    final Semaphore orderBagLock = new Semaphore(2 , true);

    final PriorityQueue<Order> buyOrders = new PriorityQueue<>();

    public PriorityQueue<Order> getSellOrders() {
        return sellOrders;
    }

    void message(String ricTopic , Order message) {

    }

}
