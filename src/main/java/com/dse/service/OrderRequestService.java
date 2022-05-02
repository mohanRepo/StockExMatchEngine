package com.dse.service;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.processor.OrderProcessingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderRequestService {

    private static final Logger logger = LoggerFactory.getLogger(OrderRequestService.class);
    OrderProcessingEngine orderProcessingEngine;
    OrdersDao orderDao;

    public OrderRequestService(OrderProcessingEngine orderProcessingEngine, OrdersDao ordersDao) {
        this.orderProcessingEngine = orderProcessingEngine;
        this.orderDao = ordersDao;
    }


   @Async("orderProcessingExecutor")
    public void processOrder(Order order) {
        logger.info("ready for dispatch {} ", order);
        dispatchMessages(order);
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

}
