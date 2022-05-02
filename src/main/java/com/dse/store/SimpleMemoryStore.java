package com.dse.store;

import com.dse.model.OrderBook;
import com.dse.model.Quote;
import com.dse.model.Trade;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class SimpleMemoryStore {

    Object lock = new Object();

    private Map<String, OrderBook> orderBags = new HashMap<>();  // Security -> Orders Map Store
    private Map<String, Quote> quotes = new HashMap<>();    // Security -> quote (bid , ask) Map Store
    private Map<Long, Trade> trades = new HashMap<>();     // Trade repo
    private Map<Long, String> orderAccountMapping = new HashMap<>();    // orderID -> Account Mapping

    {
        orderBags.put("TEST", new OrderBook());
    }

    public OrderBook getSecurityOrders(String security) {
        OrderBook orderBook = orderBags.get(security);
        if (orderBook == null) {
            synchronized (lock) {
                orderBook = orderBags.get(security);
                if (orderBook == null) {
                    orderBook = new OrderBook();
                    orderBags.put(security, orderBook);
                }
            }
        }

        return orderBook;
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

    public Trade getTrade(Long id){
        return trades.get(id);
    }

    public List<Trade> getAllTrades(){
        return trades.values().stream().toList();
    }

    public List<Quote> getAllQuote(){
        return quotes.values().stream().toList();
    }

    public void saveTrade(Trade trade){
        trades.put(trade.getTradeId() , trade);
    }


}
