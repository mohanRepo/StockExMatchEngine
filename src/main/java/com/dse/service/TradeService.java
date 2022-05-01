package com.dse.service;

import com.dse.model.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    public void createTrade(String security , Order order, Long tradeQuantity){
        System.out.println( String.format("trade created for order : %s , security: %s , quantity: %s" , order , security , tradeQuantity ));
    }
}
