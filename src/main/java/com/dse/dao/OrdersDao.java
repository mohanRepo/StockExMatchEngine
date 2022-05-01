package com.dse.dao;

import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.OrderBag;
import com.dse.model.Quote;
import com.dse.store.SimpleMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

@Component
public class OrdersDao {

    @Autowired
    private SimpleMemoryStore store;

/*    public OrdersDao(SimpleMemoryStore store) {
        this.store = store;
    }

    private SimpleMemoryStore store;*/

    public Optional<Order> peekTopOrder(String security, Side side){
        return getOrderBag(security).peek(side);
    }

    public List<Order> getOrders(String security) {
       return getOrderBag(security).getSellAndBuyOrders();
    }

    public List<Order> getOrders(String security, Side side) {
        return switch (side) {
            case BUY -> getBuyOrders(security);
            case SELL -> getSellOrders(security);
        };
    }

    public OrderBag getOrderBag(String security) {
        return store.getSecurityOrders(security);
    }

    public List<Order> getBuyOrders(String security) {
        return store.getSecurityOrders(security).getBuyOrders();
    }

    public List<Order> getSellOrders(String security) {

        return store.getSecurityOrders(security).getSellOrders();
    }

    public Quote getQuote(String security) {
        return store.getQuote(security);
    }


}
