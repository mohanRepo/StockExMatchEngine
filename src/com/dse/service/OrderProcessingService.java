package com.dse.service;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.OrderBag;
import com.dse.model.Quote;
import com.dse.model.Subscriber;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Logger;

public class OrderProcessingService implements Subscriber<Quote> {

    Logger logger = Logger.getLogger(OrderProcessingService.class.getName());
    public OrderProcessingService(QuoteService quoteService, OrdersDao ordersDao) {
        this.quoteService = quoteService;
        this.ordersDao = ordersDao;
        this.quoteService.register(this);
    }

    QuoteService quoteService;
    OrdersDao ordersDao;
    TradeService tradeService = new TradeService();

    public void placeOrder(String security, Order newOrder) {

        logger.info("received for process -> " + newOrder.toString());
        OrderBag orderBag = ordersDao.getOrderBag(security);
        Side side = newOrder.getSide();
        PriorityQueue<Order> orders = ordersDao.getOrders(security, side);

        logger.info(String.format("total %s orders: %s" , side , orders.size()));
        synchronized (orders) { // buy lock or sell lock per security
            try {
                orderBag.waitForOrderExecution();
                Order currentTop = orders.peek();
                logger.info(String.format("top order: %s", currentTop));
                logger.info(String.format("new order: %s", newOrder));
                orders.add(newOrder);
                if (currentTop == null || currentTop.getPrice() != newOrder.getPrice()) {
                    updateQuote(security, side, newOrder.getPrice());
                }
                logger.info(String.format("total %s orders: %s", side, orders.size()));
            }finally {
                orderBag.releaseForOrderExecution();
            }
        }
    }


    void executeOrder(String security){
        Queue<Order> buyOrders = ordersDao.getBuyOrders(security);
        Queue<Order> sellOrders = ordersDao.getSellOrders(security);

        Order buyOrder = buyOrders.peek();
        Order sellOrder = sellOrders.peek();

        if(buyOrder == null || sellOrder == null)
            return;

        buyOrder = buyOrders.poll();
        sellOrder = sellOrders.poll();

        float tradePrice = buyOrder.getPrice();
        long quantityDiff = buyOrder.getQuantity() - sellOrder.getQuantity();
        long tradeQuantity = quantityDiff >= 0  ?  sellOrder.getQuantity() : buyOrder.getQuantity();

        // create sell trade
        tradeService.createTrade(security , sellOrder , tradeQuantity);

        // create buy trade
        tradeService.createTrade(security , buyOrder , tradeQuantity);

        // create buy or sell order
        if(quantityDiff > 0)
        {
            // create buy order
            buyOrder.setQuantity(Math.abs(quantityDiff));
            buyOrders.add(buyOrder);
            //this.placeOrder(security, buyOrder);
        }else if (quantityDiff < 0){
            // create sell order
            sellOrder.setQuantity(Math.abs(quantityDiff));
            sellOrders.add(sellOrder);
            // this.placeOrder(security , sellOrder);
        }
        quoteService.updateQuote(security);
    }

    void updateQuote(String security, Side side, float price) { // TODO make it Async
             quoteService.updateQuote(security , side , price);
    }

    @Override
    public void onMessage(String topic, Quote message) {
        executeOrder(topic);
    }
}
