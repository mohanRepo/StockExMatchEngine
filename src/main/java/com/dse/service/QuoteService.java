package com.dse.service;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.Publisher;
import com.dse.model.Quote;
import com.dse.model.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteService implements Publisher<Quote> {

    // TODO it needs separated dao
    @Autowired
    private OrdersDao ordersDao;

    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

    private final List<Subscriber<Quote>> subscribers = new ArrayList<>();


    public void updateQuote(String security){
        Optional<Order> topSellOrder = ordersDao.peekTopOrder(security , Side.SELL);
        Optional<Order> topBuyOrder = ordersDao.peekTopOrder(security , Side.BUY);
        if(topSellOrder.isEmpty() && topBuyOrder.isEmpty())
            return; // dont update the quote , quote should have historic

        Quote quote = ordersDao.getQuote(security);
        synchronized (quote) {
            // TODO make setBit and setAsk immutable
            quote.setAsk(topSellOrder.isEmpty() ? 0 : topSellOrder.get().getPrice());
            quote.setBid(topBuyOrder.isEmpty() ? 0 : topBuyOrder.get().getPrice());

            float spread = quote.getSpread();
            logger.info("Notify price match --> update quote: {} , side:  {} , new spread: {} ", security, "BUY/SELL", spread);
            if ( topSellOrder.isPresent() && topBuyOrder.isPresent() &&  spread <= 0.00001) {
                sendMessage(security, quote);
            }
        }
    }

    public List<Quote> getAllQuotes()
    {
        return ordersDao.getAllQuotes();
    }

    @Deprecated
    public void updateQuote(String security, Side side, float price) {
        logger.info("update quote: {} , side:  {} , price: {}" , security , side , price);
        Quote quote = ordersDao.getQuote(security);

        synchronized (quote) {
            switch (side) {
                case BUY -> quote.setBid(price);
                case SELL -> quote.setAsk(price);
            }

            float spread = quote.getSpread();
            logger.info("Order Process --> update quote: {} , side:  {} , new spread: {} " , security , side , spread);
            if (! (Math.abs(quote.getAsk()) < 0.00001 || Math.abs(quote.getBid()) < 0.00001)  &&  spread <= 0.00001) {
                sendMessage(security , quote);
            }
        }
    }

    @Override
    public void register(Subscriber<Quote> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void sendMessage(String topic, Quote message) {
        for (Subscriber<Quote> subscriber : subscribers) {
            subscriber.onMessage(topic, message);
        }
    }
}
