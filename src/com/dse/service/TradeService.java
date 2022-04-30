package com.dse.service;

import com.dse.model.Order;

public class TradeService {

    public void createTrade(String security , Order order, Long tradeQuantity){
        System.out.println( String.format("trade created for order : %s , security: %s , quantity: %s" , order , security , tradeQuantity ));
    }
}
