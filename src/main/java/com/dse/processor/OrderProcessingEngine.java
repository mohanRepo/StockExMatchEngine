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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

@Service
public class OrderProcessingEngine implements Subscriber<Quote>, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingEngine.class);

/*
    @Autowired
    @Qualifier("orderMatchExecutor")
    Executor orderMatchExecutor;
*/

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private TradeService tradeService;

    ApplicationContext applicationContext;

    private Map<String, Executor> orderMatchExecutors = new HashMap<>();

    @PostConstruct
    public void init() {
        this.quoteService.register(this);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void placeOrder(String security, Order newOrder) {

        logger.info("security: {} -> order received for process -> {}", security, newOrder);
        OrderBook orderBook = ordersDao.getOrderBag(security);
        Side side = newOrder.getSide();

        logger.info("security:{} total {} orders: {}", security, side, orderBook.size(side));
        synchronized (orderBook.getQueueLockObj(side)) { // buy lock or sell lock per security
            try {
                orderBook.waitForOrderExecution();  // Wait for Orders to be executed
                logger.info("new order: {}", newOrder);
                orderBook.add(newOrder);
                Optional<Order> newTop = orderBook.peek(side);
                if (newOrder.getOrderId() == newTop.get().getOrderId())  //
                {
                    updateQuote(security, side, newOrder.getPrice());
                }
                logger.info("total {} orders: {}", side, orderBook.size(side));
            } finally {
                orderBook.releaseForOrderExecution();
            }
        }
    }

    void executeOrderQueue(String security) {
        OrderBook orderBook = ordersDao.getOrderBag(security);
        boolean executed = false;
        logger.info("try acquire execute order lock for secutiry: {}", security);
        if (orderBook.getExecutionLock().tryLock()) {
            try {
                logger.info("acquired execute order for secutiry: {}", security);
                executed = executeOrder(security, orderBook);
            } finally {
                orderBook.getExecutionLock().unlock();
            }

            if(executed)
                    quoteService.updateQuote(security); // Run in the same Order executor thread and execute the orders
        } else {
            logger.info("Order execution is in progress for security: {}", security);
        }
    }

    private boolean executeOrder(String security, OrderBook orderBook) {

        boolean executed = false;
        // OrderBook orderBook = ordersDao.getOrderBag(security);
        Order buyOrder = null;
        Order sellOrder = null;

        try {
            orderBook.waitForOrderProcess();  // When order to be traded don't allow other thread to add new orders into order book

            Optional<Order> buyOrderOpt = orderBook.peek(Side.BUY);
            Optional<Order> sellOrderOpt = orderBook.peek(Side.SELL);
            if (buyOrderOpt.isEmpty() || sellOrderOpt.isEmpty()) {
                logger.warn("**WARNING**  phantom scenario[either sell buy order null] ABORT EXECUTION , security:{}" , security);
                return false;
            } else if (buyOrderOpt.get().getPrice() < sellOrderOpt.get().getPrice()) {
                logger.warn("**WARNING**  phantom scenario[ buyPrice < sellPrice] ABORT EXECUTION , security:{}" , security);
                return false;
            }

            buyOrder = orderBook.poll(Side.BUY);
            sellOrder = orderBook.poll(Side.SELL);

        executed = true;
        int buyQuantity = buyOrder.getQuantity();
        int sellQuantity = sellOrder.getQuantity();
        int tradeQuantity = Math.min(buyQuantity, sellQuantity);
        float tradePrice = Math.max(buyOrder.getPrice(), sellOrder.getPrice());

        logger.info("executed order for sec:{} , order ids [{},{}] , tradeQuantity:{} , tradePrice: {}"
                , security, buyOrder.getOrderId(), sellOrder.getOrderId(), tradeQuantity, tradePrice);

        // create sell trade
        tradeService.createTrade(security, sellOrder, tradeQuantity, tradePrice);
        // create buy trade
        tradeService.createTrade(security, buyOrder, tradeQuantity, tradePrice);

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


        return executed;



    }


/*    @Async("orderMatchExecutor")
    void updateQuote(String security, Side side, float price) {
        logger.info("update begin.. ric:{}" , security);
        quoteService.updateQuote(security);
        logger.info("update end.. ric:{}" , security);
    }*/

    void updateQuote(String security, Side side, float price) {
        Executor executor = orderMatchExecutors.get(security);
        if (executor == null) {

            synchronized (orderMatchExecutors) {

                if (executor == null) {
                    executor = (Executor) applicationContext.getBean("orderMatchExecutor");
                    orderMatchExecutors.put(security, executor);
                }

            }
        }

        executor.execute(() -> quoteService.updateQuote(security));
    }

    @Override
    public void onMessage(String topic, Quote message) {
        executeOrderQueue(topic);
    }

/*    @Deprecated
    void updateQuoteEx(String security, Side side, float price) {
        //orderMatchExecutor.execute(() -> quoteService.updateQuote(security));
    }*/

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
            executeOrderQueue(security);
        } else {
            logger.info("No match found -> security: {} , buy order: {} , sell order : {}", security, buyOrder, sellOrder);
        }
    }


}
