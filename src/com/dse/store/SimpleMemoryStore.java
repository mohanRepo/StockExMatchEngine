package com.dse.store;

import com.dse.model.Order;
import com.dse.model.OrderBag;
import com.dse.model.Quote;
import com.dse.model.Trade;

import java.util.HashMap;
import java.util.Map;

public class SimpleMemoryStore {



    Map<String, OrderBag> orders = new HashMap<>();
    Map<String , Quote> quotes = new HashMap<>();
    Map<Long, Trade> trades = new HashMap<>();
    Map<Long, String>  orderAccountMapping = new HashMap<>();

    {
        orders.put("TEST" , new OrderBag());
    }


    public OrderBag getSecurityOrders(String security) {
        OrderBag orderBag = orders.get(security);
        if(orderBag == null){
            orderBag = new OrderBag();
            orders.put(security , orderBag);
        }
        return orderBag;
    }

    public Quote getQuote(String security){
        Quote quote = quotes.get(security);
        if(quote ==  null){
            quote = new Quote(security);
            quotes.put(security , quote);
        }

        return quote;
    }

    //


}
