package com.dse.controller;


import com.dse.dao.TradeDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.Trade;
import com.dse.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tradeApi/")
public class TradeController {

    @Autowired
    TradeService tradeService;


    @GetMapping(path = "getTrades", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getTrades() {

        List<Trade> allTrades = tradeService.getAllTrades();


        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<table  BORDER=2 CELLSPACING=0 CELLPADDING=5>");
        htmlBuilder.append("<tr><td>tradeId</td><td>OrderId</td><td>security</td><td>Side</td><td>TradePrice</td><td>TradeQuantity</td><td>Time</td></tr>");

        allTrades.forEach(trade ->
        {
            htmlBuilder.append("<tr>");

            htmlBuilder.append("<td>" + trade.getTradeId() + "</td>");
            htmlBuilder.append("<td>" + trade.getOrderId() + "</td>");
            htmlBuilder.append("<td>" + trade.getSecurity() + "</td>");
            htmlBuilder.append("<td>" + trade.getSide() + "</td>");
            htmlBuilder.append("<td>" + trade.getPrice() + "</td>");
            htmlBuilder.append("<td>" + trade.getTradeQuantity() + "</td>");
            htmlBuilder.append("<td>" + trade.getTime().toString() + "</td>");

            htmlBuilder.append("</tr>");
        });


        htmlBuilder.append("<tr>");
        return ResponseEntity.ok(htmlBuilder.toString());
    }

}
