package com.dse.store;

import com.dse.model.OrderBag;
import com.dse.model.Quote;
import com.dse.model.Trade;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class SimpleMemoryStore {

    Object lock = new Object();

    private Map<String, OrderBag> orderBags = new HashMap<>();  // Security -> Orders Map Store
    private Map<String, Quote> quotes = new HashMap<>();    // Security -> quote (bid , ask) Map Store
    private Map<Long, Trade> trades = new HashMap<>();     // Trade repo
    private Map<Long, String> orderAccountMapping = new HashMap<>();    // orderID -> Account Mapping

    {
        orderBags.put("TEST", new OrderBag());
    }

    public OrderBag getSecurityOrders(String security) {
        OrderBag orderBag = orderBags.get(security);
        if (orderBag == null) {
            synchronized (lock) {
                orderBag = orderBags.get(security);
                if (orderBag == null) {
                    orderBag = new OrderBag();
                    orderBags.put(security, orderBag);
                }
            }
        }

        return orderBag;
    }
    public Quote getQuote(String security) {
        Quote quote = quotes.get(security);
        if (quote == null) {  // 1st time mutiple thread race condition
            synchronized (lock) {
                quote = quotes.get(security);
                if (quote == null) {
                    quote = new Quote(security);
                    quotes.put(security, quote);
                }
            }
        }

        return quote;
    }

    //


}
