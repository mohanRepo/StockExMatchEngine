package com.dse.controller;

import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.service.OrderRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orderApi/")
public class OrderRequestController {

    @Autowired
    private OrderRequestService orderRequestService;

    @GetMapping(value = {"whoami/{name}", "/whoami"})
    public String findUserById(@PathVariable(value = "name", required = false) String name) {
        return String.format("Welcome %s to dream stock exchange..", (name != null ? name : "Anonymous"));
    }

    @GetMapping(path = "getOrderBook/{security}/{limit}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getOrderBook(@PathVariable("security") String security, @PathVariable("limit") int limit) {
        List<Order> buyOrders = orderRequestService.getOrders(security, Side.BUY);
        List<Order> sellOrders = orderRequestService.getOrders(security, Side.SELL);
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<table  BORDER=2 CELLSPACING=0 CELLPADDING=5>");
        htmlBuilder.append("<tr><th valign='left' colspan=3>BUY</th><th valign='left' colspan=3>SELL</th></tr>");
        htmlBuilder.append("<tr><td>Qty</td><td>Price</td><td>Time</td><td>Qty</td><td>Price</td><td>Time</td></tr>");

        for (int i = 0; i <= limit; i++) {
            htmlBuilder.append("<tr>");
            if (i < buyOrders.size()) {
                htmlBuilder.append("<td>" + buyOrders.get(i).getQuantity() + "</td>");
                htmlBuilder.append("<td>" + buyOrders.get(i).getPrice() + "</td>");
                htmlBuilder.append("<td>" + buyOrders.get(i).getTime() + "</td>");
            }
            if (i < sellOrders.size()) {
                htmlBuilder.append("<td>" + sellOrders.get(i).getQuantity() + "</td>");
                htmlBuilder.append("<td>" + sellOrders.get(i).getPrice() + "</td>");
                htmlBuilder.append("<td>" + sellOrders.get(i).getTime() + "</td>");
            }
            htmlBuilder.append("</tr>");
        }

        htmlBuilder.append("<tr>");
        return ResponseEntity.ok(htmlBuilder.toString());
    }

    @GetMapping(path = "bookOrder/{security}/{side}/{quantity}/{price}")
    public ResponseEntity<Boolean> bookOrder(@PathVariable("security") String security,
                            @PathVariable("side") String side,
                            @PathVariable("quantity") int quantity,
                            @PathVariable("price") float price){

        Side sideEnum = Side.valueOf(side.toUpperCase());

        Order order = new Order(security, sideEnum, quantity, price);

        orderRequestService.processOrder(order);

        return ResponseEntity.ok(true);
    }

}
