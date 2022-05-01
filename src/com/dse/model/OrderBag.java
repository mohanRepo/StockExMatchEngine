package com.dse.model;

import com.dse.enums.Side;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class OrderBag {

    private volatile OrderQueue buyOrders = new OrderQueue();
    private volatile OrderQueue sellOrders = new OrderQueue();

    // Lock to allow either (add buy and sell in parallel) or Order execution
    // permits are 2 bcoz we can allow add buy and sell in parallel
    // TODO find proper place to fit  for this
    private Semaphore orderBagLock = new Semaphore(2 );

    public void add(Order order) {
        switch (order.getSide()){
            case BUY ->  buyOrders.add(order);
            case SELL ->  sellOrders.add(order);
        }
    }

    public Optional<Order> peek(Side side){
        return switch (side){
            case BUY ->  Optional.ofNullable(buyOrders.peek());
            case SELL ->  Optional.ofNullable(sellOrders.peek());
        };
    }

    public Order poll(Side side){
        return switch (side){
            case BUY ->  buyOrders.poll();
            case SELL ->  sellOrders.poll();
        };
    }

    public Object getQueueLockObj(Side side){
        return switch (side){
            case BUY ->  buyOrders.getQueueLock();
            case SELL ->  sellOrders.getQueueLock();
        };
    }

    public OrderQueue getBuyOrders() {
        return buyOrders;
    }

    public List<Order> getBuyOrdersEx() {
        return buyOrders.stream().sorted().toList();
    }

    public List<Order> getSellOrdersEx() {
        return sellOrders.stream().sorted().toList();
    }

    public PriorityQueue<Order> getSellOrders() {
        return sellOrders;
    }

    public int size(Side side){
        return switch (side){
            case BUY ->  buyOrders.size();
            case SELL ->  sellOrders.size();
        };
    }

    public int size(){
        return buyOrders.size() + sellOrders.size();
    }

    public void waitForOrderExecution(){
        orderBagLock.acquireUninterruptibly();
    }
    public void releaseForOrderExecution()  {
        orderBagLock.release();
    }
    public void waitForOrderProcess(){
        orderBagLock.acquireUninterruptibly(2);
    }
    public void releaseForOrderProcess(){
        orderBagLock.release(2);
    }
}
