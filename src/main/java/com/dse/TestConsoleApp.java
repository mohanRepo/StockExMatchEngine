package com.dse;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.service.OrderRequestService;
import com.dse.store.SimpleMemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class TestConsoleApp {

    static final Object lock = new Object();
    // static Object anotherLock = new Object();

    SimpleMemoryStore store = new SimpleMemoryStore();
   OrdersDao ordersDao;// = new OrdersDao(store);
    /*QuoteService quoteService = new QuoteService(ordersDao);
    OrderProcessingService orderProcessingService = new OrderProcessingService(quoteService, ordersDao);*/
    OrderRequestService orderRequestService;// = new OrderRequestService(orderProcessingService , ordersDao);

    public void placeOrder(String security,int start , int end) {

       // logger.info("received for process -> " + newOrder.toString());
        //Side side = newOrder.getSide();
      /*  PriorityQueue<Order> orders = ordersDao.getOrders(security, Side.SELL);
        synchronized (orders){
            for (int i = start; i < end; i++) {
                System.out.println(i);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }*/
    }

        public static void NOTmain(String[] args) throws InterruptedException, IOException {

        Logger logger = LoggerFactory.getLogger(OrderRequestService.class);
        logger.info("..test...");


            TestConsoleApp m = new TestConsoleApp();
/*         m.orderRequestService.processOrder(new Order("IBM.N" , Side.SELL , 10 , 100.0F));
            m.orderRequestService.processOrder(new Order( "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order( "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order( "IBM.N" , Side.SELL , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(L, "IBM.N" , Side.SELL , 10 , 100.0F));
            m.orderRequestService.processOrder(new Order(2L, "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(3L, "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(4L, "IBM.N" , Side.SELL , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(1L, "IBM.XN" , Side.SELL , 10 , 100.0F));
            m.orderRequestService.processOrder(new Order(2L, "IBM.XN" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(3L, "IBM.XN" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(4L, "IBM.XN" , Side.SELL , 20 , 101.0F));
            Thread.sleep(10);
            m.orderRequestService.processOrder(new Order(41L, "ABC.XM" , Side.SELL , 100 , 20.30F , 901));
            m.orderRequestService.processOrder(new Order(42L, "ABC.XM" , Side.SELL , 100 , 20.25F , 903));
            m.orderRequestService.processOrder(new Order(43L, "ABC.XM" , Side.SELL , 200 , 20.30F , 905));

            m.orderRequestService.processOrder(new Order(44L, "ABC.XM" , Side.BUY , 100 , 20.15F , 906));
            m.orderRequestService.processOrder(new Order(45L, "ABC.XM" , Side.BUY , 200 , 20.20F , 908));
            m.orderRequestService.processOrder(new Order(46L, "ABC.XM" , Side.BUY , 200 , 20.15F , 909));

            //  Thread.sleep(5000);
            m.orderRequestService.processOrder(new Order(47L, "ABC.XM" , Side.BUY , 250 , 20.35F , 910));

            m.orderRequestService.processOrder(new Order(1L, "IBM.N" , Side.SELL , 10 , 100.0F));
            m.orderRequestService.processOrder(new Order(2L, "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(3L, "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(4L, "IBM.N" , Side.SELL , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(1L, "IBM.N" , Side.SELL , 10 , 100.0F));
            m.orderRequestService.processOrder(new Order(2L, "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(3L, "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(4L, "IBM.N" , Side.SELL , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(1L, "IBM.N" , Side.SELL , 10 , 100.0F));
            m.orderRequestService.processOrder(new Order(2L, "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(3L, "IBM.N" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(4L, "IBM.N" , Side.SELL , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(1L, "IBM.XN" , Side.SELL , 10 , 100.0F));
            m.orderRequestService.processOrder(new Order(2L, "IBM.XN" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(3L, "IBM.XN" , Side.BUY , 20 , 101.0F));
            m.orderRequestService.processOrder(new Order(4L, "IBM.XN" , Side.SELL , 20 , 101.0F));*/

            Thread.sleep(2000);


            List<Order> orders = m.ordersDao.getOrders("ABC.XM");
            orders.forEach(order -> {
                System.out.println(order.toString());
            });



           PriorityQueue<Integer>  queue = new PriorityQueue<Integer>();
           queue.add(5);
           queue.add(3);
           queue.add(10);
           queue.add(4);


          Arrays.stream(queue.toArray()).sorted().toList().forEach(System.out::println);

          System.out.println(queue.poll());
          System.out.println(queue.poll());



        System.out.println("....hello dream stock exchange....");
    }
}
