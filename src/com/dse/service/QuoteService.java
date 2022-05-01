package com.dse.service;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.Publisher;
import com.dse.model.Quote;
import com.dse.model.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class QuoteService implements Publisher<Quote> {

    Logger logger = Logger.getLogger(QuoteService.class.getName());

    public QuoteService(OrdersDao ordersDao) {
        this.ordersDao = ordersDao;
    }

    private final List<Subscriber<Quote>> subscribers = new ArrayList<>();
    final OrdersDao ordersDao;

    public void updateQuote(String security){
        Optional<Order> topSellOrder = ordersDao.peekTopOrder(security , Side.SELL);
        Optional<Order> topBuyOrder = ordersDao.peekTopOrder(security , Side.BUY);
        Quote quote = ordersDao.getQuote(security);
        synchronized (quote) {
            // TODO make setBit and setAsk immutable
            quote.setAsk(topSellOrder.isEmpty() ? 0 : topSellOrder.get().getPrice());
            quote.setBid(topBuyOrder.isEmpty() ? 0 : topBuyOrder.get().getPrice());

            float spread = quote.getSpread();
            logger.info(String.format("Notify price match --> update quote: %s , side:  %s , new spread: %s ", security, "BUY/SELL", spread));
            if ( topSellOrder.isPresent() && topBuyOrder.isPresent() &&  spread <= 0.00001) {
                sendMessage(security, quote);
            }
        }
    }

    @Deprecated
    public void updateQuote(String security, Side side, float price) {
        logger.info(String.format("update quote: %s , side:  %s , price: %s" , security , side , price));
        Quote quote = ordersDao.getQuote(security);

        synchronized (quote) {
            switch (side) {
                case BUY -> quote.setBid(price);
                case SELL -> quote.setAsk(price);
            }

            float spread = quote.getSpread();
            logger.info(String.format("Order Process --> update quote: %s , side:  %s , new spread: %s " , security , side , spread));
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
