package com.dse.dao;

import com.dse.model.Quote;
import com.dse.store.SimpleMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuoteDao {

    @Autowired
    private SimpleMemoryStore store;

    public Quote getQuote(String security) {
        return store.getQuote(security);
    }
}
