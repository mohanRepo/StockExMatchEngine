package com.dse.service;

import com.dse.model.Order;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class OrderRequestService {


    Logger logger = Logger.getLogger(OrderRequestService.class.getName());
    OrderProcessingService orderProcessingService;

    BlockingQueue<Order> orders = new LinkedBlockingDeque<>();

    ExecutorService executorService = Executors.newFixedThreadPool(100);

    public OrderRequestService(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }


    public void processOrder(Order order) {
        orders.add(order);
        logger.info(String.format("ready for dispatch %s " , order));
        dispatchMessagesEx(order);
    }


    // Redo this logic , should buffer and stream the request in a different dispatcher

    void dispatchMessages(Order order) {

            logger.info("dispatched-> " + order);
            orderProcessingService.placeOrder(order.getSecurity(), order);


    }
    void dispatchMessagesEx(Order order) {
        executorService.submit(() -> {
            logger.info("dispatched-> " + order);
            orderProcessingService.placeOrder(order.getSecurity(), order);
            logger.info("done-> " + order);
        });

    }


}
