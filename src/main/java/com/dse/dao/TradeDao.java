package com.dse.dao;

import com.dse.model.Trade;
import com.dse.store.SimpleMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TradeDao {

    @Autowired
    private SimpleMemoryStore store;

    public void saveTrade(Trade trade){
        store.saveTrade(trade);
    }

    public void getTrade(Long id){
        store.getTrade(id);
    }

    public List<Trade> getAllTrades(){
        return store.getAllTrades();
    }
}
