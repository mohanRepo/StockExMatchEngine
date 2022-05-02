package com.dse.processor;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.OrderBook;
import com.dse.model.Quote;
import com.dse.model.Subscriber;
import com.dse.service.OrderRequestService;
import com.dse.service.QuoteService;
import com.dse.service.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.Executor;

@Service
public class OrderProcessingEngine implements Subscriber<Quote> {

    private static final Logger logger = LoggerFactory.getLogger(OrderRequestService.class);

    @Autowired
    @Qualifier("orderMatchExecutor")
    Executor orderMatchExecutor;

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private TradeService tradeService;

    @PostConstruct
    public void init() {
        this.quoteService.register(this);
    }


    public void placeOrder(String security, Order newOrder) {

        logger.info("security: {} -> order received for process -> {}", security, newOrder);
        OrderBook orderBook = ordersDao.getOrderBag(security);
        Side side = newOrder.getSide();

        logger.info("security:{} total {} orders: {}", security, side, orderBook.size(side));
        synchronized (orderBook.getQueueLockObj(side)) { // buy lock or sell lock per security
            try {
                orderBook.waitForOrderExecution();  // Wait for Orders to be executed
                Optional<Order> currentTop = orderBook.peek(side);
                logger.info("top order: {}", currentTop);
                logger.info("new order: {}", newOrder);
                orderBook.add(newOrder);
                if (currentTop.isEmpty() || currentTop.get().getPrice() != newOrder.getPrice()) {
                    // orderMatchService.submit(() -> checkForOrderMatch(security));
                    updateQuote(security, side, newOrder.getPrice());
                }
                logger.info("total {} orders: {}", side, orderBook.size(side));
            } finally {
                orderBook.releaseForOrderExecution();
            }
        }
    }

    void executeOrder(String security) {
        OrderBook orderBook = ordersDao.getOrderBag(security);
        try {
            orderBook.waitForOrderProcess();  // When order to be traded don't allow other thread to add new orders into order book

            Optional<Order> buyOrderOpt = orderBook.peek(Side.BUY);
            Optional<Order> sellOrderOpt = orderBook.peek(Side.SELL);
            if (buyOrderOpt.isEmpty() || sellOrderOpt.isEmpty()) {
                logger.warn(String.format("**WARNING**  phantom scenario[either sell buy order null] ABORT EXECUTION"));
                return;
            } else if (buyOrderOpt.get().getPrice() < sellOrderOpt.get().getPrice()) {
                logger.warn(String.format("**WARNING**  phantom scenario[ buyPrice < sellPrice] ABORT EXECUTION"));
                return;
            }


            Order buyOrder = orderBook.poll(Side.BUY);
            Order sellOrder = orderBook.poll(Side.SELL);
            int buyQuantity = buyOrder.getQuantity();
            int sellQuantity = sellOrder.getQuantity();
            int tradeQuantity = Math.min(buyQuantity, sellQuantity);
            float tradePrice = Math.max(buyOrder.getPrice(), sellOrder.getPrice());

            logger.info("executed order for sec:{} , order ids [{},{}] , tradeQuantity:{} , tradePrice: {}"
                    , security , buyOrder.getOrderId() , sellOrder.getOrderId() , tradeQuantity , tradePrice);

            // create sell trade
            tradeService.createTrade(security, sellOrder, tradeQuantity , tradePrice);
            // create buy trade
            tradeService.createTrade(security, buyOrder, tradeQuantity , tradePrice);

            // create buy or sell order
            if (buyQuantity > sellQuantity) {
                orderBook.add(buyOrder.getNewVersion(buyQuantity - sellQuantity).get());  // create buy order
            } else if (buyQuantity < sellQuantity) {
                orderBook.add(sellOrder.getNewVersion(sellQuantity - buyQuantity).get());  // create sell order
            }

            logger.info("security: {} -  total [buy,sell] -> [{},{}] orders",
                    security, orderBook.size(Side.BUY), orderBook.size(Side.SELL));
        } catch (Exception ex) {
            logger.info("Error in execute..." + ex);
        } finally {
            orderBook.releaseForOrderProcess();
        }
        quoteService.updateQuote(security); // Run in the same Order executor thread and execute the orders
    }


/*    @Async("orderMatchExecutor")
    void updateQuote(String security, Side side, float price) {
        logger.info("update begin.. ric:{}" , security);
        quoteService.updateQuote(security);
        logger.info("update end.. ric:{}" , security);
    }*/

    void updateQuote(String security, Side side, float price) {
        orderMatchExecutor.execute(() -> quoteService.updateQuote(security));
    }

    @Override
    public void onMessage(String topic, Quote message) {
        executeOrder(topic);
    }

    @Deprecated
    void checkForOrderMatch(String security) {

        OrderBook orderBook = ordersDao.getOrderBag(security);
        Optional<Order> buyOrderOpt = orderBook.peek(Side.BUY);
        Optional<Order> sellOrderOpt = orderBook.peek(Side.SELL);
        if (buyOrderOpt.isEmpty() || sellOrderOpt.isEmpty()) {
            logger.info("security: {} , No match --> [either no sell or buy order].. abort match", security);
            return;
        }

        Order buyOrder = buyOrderOpt.get();
        Order sellOrder = sellOrderOpt.get();
        float buyPrice = buyOrder.getPrice();
        float sellPrice = sellOrder.getPrice();

        if ((sellPrice - buyPrice) <= 0.00001) {
            logger.info("match found -> security: {} , buy order: {}, sell order : {}", security, buyOrder, sellOrder);
            executeOrder(security);
        } else {
            logger.info("No match found -> security: {} , buy order: {} , sell order : {}", security, buyOrder, sellOrder);
        }

    }



}
