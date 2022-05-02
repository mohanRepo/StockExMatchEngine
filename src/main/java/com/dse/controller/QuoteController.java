package com.dse.controller;

import com.dse.model.Quote;
import com.dse.model.Trade;
import com.dse.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Component
@RequestMapping("/quoteApi/")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    @GetMapping(path = "getAllQuotes", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getTrades() {

        List<Quote> allQuotes = quoteService.getAllQuotes();


        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<table  BORDER=2 CELLSPACING=0 CELLPADDING=5>");
        htmlBuilder.append("<tr><td>Security</td><td>Ask</td><td>Bid</td><td>Spread</td></tr>");

        allQuotes.forEach(quote ->
        {
            htmlBuilder.append("<tr>");

            htmlBuilder.append("<td>" + quote.getRic() + "</td>");
            htmlBuilder.append("<td>" + quote.getAsk() + "</td>");
            htmlBuilder.append("<td>" + quote.getBid() + "</td>");
            htmlBuilder.append("<td>" + quote.getSpread() + "</td>");

            htmlBuilder.append("</tr>");
        });


        htmlBuilder.append("<tr>");
        return ResponseEntity.ok(htmlBuilder.toString());
    }

}
