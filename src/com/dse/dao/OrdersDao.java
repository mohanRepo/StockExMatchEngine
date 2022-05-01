package com.dse.dao;

import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.OrderBag;
import com.dse.model.Quote;
import com.dse.store.SimpleMemoryStore;

import java.util.PriorityQueue;
import java.util.Queue;

public class OrdersDao {


    public OrdersDao(SimpleMemoryStore store) {
        this.store = store;
    }

    SimpleMemoryStore store;

    public PriorityQueue<Order> getOrders(String security, Side side) {
        return switch (side) {
            case BUY -> getBuyOrders(security);
            case SELL -> getSellOrders(security);
        };
    }

    public OrderBag getOrderBag(String security) {
        return store.getSecurityOrders(security);
    }

    public PriorityQueue<Order> getBuyOrders(String security) {

        return store.getSecurityOrders(security).getBuyOrders();
    }

    public PriorityQueue<Order> getSellOrders(String security) {

        return store.getSecurityOrders(security).getSellOrders();
    }

    public Quote getQuote(String security) {
        return store.getQuote(security);
    }


}
