package com.dse.service;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class OrderRequestService {

    Logger logger = Logger.getLogger(OrderRequestService.class.getName());
    OrderProcessingService orderProcessingService;
    OrdersDao orderDao;


    ExecutorService executorService = Executors.newFixedThreadPool(100);

    public OrderRequestService(OrderProcessingService orderProcessingService, OrdersDao ordersDao) {
        this.orderProcessingService = orderProcessingService;
        this.orderDao = ordersDao;
    }


    public void processOrder(Order order) {
        logger.info(String.format("ready for dispatch %s ", order));
        dispatchMessagesAsync(order);
    }

    public List<Order> getOrders(String security){
        return orderDao.getOrders("ABC.XM");
    }

    public List<Order> getOrders(String security , Side side){
        return orderDao.getOrders("ABC.XM" , side);
    }


    // Redo this logic , should buffer and stream the request in a different dispatcher
    void dispatchMessages(Order order) {
        logger.info("dispatched-> " + order);
        orderProcessingService.placeOrder(order.getSecurity(), order);
        logger.info("done-> " + order);
    }

    void dispatchMessagesAsync(Order order) {
        executorService.submit(() -> {
            logger.info("dispatched-> " + order);
            orderProcessingService.placeOrder(order.getSecurity(), order);
            logger.info("done-> " + order);
        });

    }


}
