package com.dse.service;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.OrderBag;
import com.dse.model.Quote;
import com.dse.model.Subscriber;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class OrderProcessingService implements Subscriber<Quote> {

    private Logger logger = Logger.getLogger(OrderProcessingService.class.getName());
    private ExecutorService orderExecutorService = Executors.newFixedThreadPool(10); // Thread pool for execution task
    private QuoteService quoteService;
    private OrdersDao ordersDao;
    private TradeService tradeService = new TradeService();

    public OrderProcessingService(QuoteService quoteService, OrdersDao ordersDao) {
        this.quoteService = quoteService;
        this.ordersDao = ordersDao;
        this.quoteService.register(this);
    }

    public void placeOrder(String security, Order newOrder) {

        logger.info(String.format("security: %s -> order received for process -> " ,security , newOrder));
        OrderBag orderBag = ordersDao.getOrderBag(security);
        Side side = newOrder.getSide();

        logger.info(String.format("security:%s total %s orders: %s",security, side, orderBag.size(side)));
        synchronized (orderBag.getQueueLockObj(side)) { // buy lock or sell lock per security
            try {
                orderBag.waitForOrderExecution();  // Wait for Orders to be executed
                Optional<Order> currentTop = orderBag.peek(side);
                logger.info(String.format("top order: %s", currentTop));
                logger.info(String.format("new order: %s", newOrder));
                orderBag.add(newOrder);
                if (currentTop.isEmpty() || currentTop.get().getPrice() != newOrder.getPrice()) {
                    updateQuote(security, side, newOrder.getPrice());
                }
                logger.info(String.format("total %s orders: %s", side, orderBag.size(side)));
            } finally {
                orderBag.releaseForOrderExecution();
            }
        }
    }


    void executeOrder(String security) {
        OrderBag orderBag = ordersDao.getOrderBag(security);
        try {
            orderBag.waitForOrderProcess();  // When order to be traded don't allow to add new orders into order boo

            Optional<Order> buyOrderOpt = orderBag.peek(Side.BUY);
            Optional<Order> sellOrderOpt = orderBag.peek(Side.SELL);
            if (buyOrderOpt.isEmpty()|| sellOrderOpt.isEmpty()) {
                logger.info(String.format("**WARNING**  phantom scenario[either sell buy order null] ABORT EXECUTION")); return;
            }
            else if (buyOrderOpt.get().getPrice() < sellOrderOpt.get().getPrice()){
                logger.info(String.format("**WARNING**  phantom scenario[ buyPrice < sellPrice] ABORT EXECUTION")); return;
            }
            Order buyOrder = orderBag.poll(Side.BUY);
            Order sellOrder = orderBag.poll(Side.SELL);
            int buyQuantity = buyOrder.getQuantity();
            int sellQuantity = sellOrder.getQuantity();
            long tradeQuantity = Math.min(buyQuantity, sellQuantity);

            // create sell trade
            tradeService.createTrade(security, sellOrder, tradeQuantity);
            // create buy trade
            tradeService.createTrade(security, buyOrder, tradeQuantity);

            // create buy or sell order
            if (buyQuantity > sellQuantity) {
                orderBag.add(buyOrder.getNewVersion(buyQuantity - sellQuantity).get());  // create buy order
            } else if (buyQuantity < sellQuantity) {
                orderBag.add(sellOrder.getNewVersion(sellQuantity - buyQuantity).get());  // create sell order
            }

            logger.info(String.format("security: %s -  total [buy,sell] -> [%s,%s] orders",
                    security, orderBag.size(Side.BUY), orderBag.size(Side.SELL)));
        } catch (Exception ex) {
            logger.info("Error in execute..." + ex);
        } finally {
            orderBag.releaseForOrderProcess();
        }
        quoteService.updateQuote(security); // Run in the same Order executor thread and execute the orders
    }

    void updateQuote(String security, Side side, float price) {
        orderExecutorService.submit(() -> quoteService.updateQuote(security));
      // orderExecutorService.submit(() -> quoteService.updateQuote(security, side, price));
    }

    @Override
    public void onMessage(String topic, Quote message) {
        executeOrder(topic);
    }
}
