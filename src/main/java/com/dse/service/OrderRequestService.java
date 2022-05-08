package com.dse.service;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.ItemBufferPool;
import com.dse.model.Order;
import com.dse.model.OrderBook;
import com.dse.model.Subscriber;
import com.dse.processor.OrderProcessingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Service
public class OrderRequestService  implements Subscriber<Order> {

    private static final Logger logger = LoggerFactory.getLogger(OrderRequestService.class);
    OrderProcessingEngine orderProcessingEngine;
    OrdersDao orderDao;

    @Autowired
    @Qualifier("orderProcessingExecutor")
    private ThreadPoolTaskExecutor orderProcessingExecutor;

    ItemBufferPool<Order> orderPool;

    @PostConstruct
    void init(){
        orderPool = new ItemBufferPool<>(orderProcessingExecutor.getMaxPoolSize());
        orderPool.register(this);
    }

    public OrderRequestService(OrderProcessingEngine orderProcessingEngine, OrdersDao ordersDao) {
        this.orderProcessingEngine = orderProcessingEngine;
        this.orderDao = ordersDao;
    }


   @Async("orderProcessingExecutor")
    public void processOrder(Order order) {
      //  logger.info("ready for dispatch {} ", order);
        orderPool.addItem(order);
       // dispatchMessages(order);
    }

    public List<Order> getOrders(String security){
        return orderDao.getOrders(security);
    }

    public List<Order> getOrders(String security , Side side){
        return orderDao.getOrders(security , side);
    }


    // Redo this logic , should buffer and stream the request in a different dispatcher
    void dispatchMessages(Order order) {
        logger.info("dispatched-> " + order);
        orderProcessingEngine.placeOrder(order.getSecurity(), order);
        logger.info("done-> " + order);
    }

    @Override
    public void onMessage(String topic, Order message) {

        orderProcessingExecutor.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            dispatchMessages(message);
            orderPool.releaseProcessing();
        });

    }
}
