package com.dse.controller;

import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.service.OrderRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/api/test")
@ConditionalOnProperty(value = "match.engine.run.test.order" , havingValue = "true")
public class TestRunnerRestClient {

    @Autowired
    OrderRequestService orderRequestService;

    @PostConstruct
    public void autoBookOrders() {
        autoBook();
    }

    private void autoBook() {

        orderRequestService.processOrder(new Order( "ABC.XM" , Side.SELL , 100 , 20.30F , 901));
        orderRequestService.processOrder(new Order("ABC.XM" , Side.SELL , 100 , 20.25F , 903));
        orderRequestService.processOrder(new Order("ABC.XM" , Side.SELL , 200 , 20.30F , 905));

        orderRequestService.processOrder(new Order( "ABC.XM" , Side.BUY , 100 , 20.15F , 906));
        orderRequestService.processOrder(new Order( "ABC.XM" , Side.BUY , 200 , 20.20F , 908));
        orderRequestService.processOrder(new Order( "ABC.XM" , Side.BUY , 200 , 20.15F , 909));

        orderRequestService.processOrder(new Order( "ABC.XM" , Side.BUY , 250 , 20.35F , 910));

        orderRequestService.processOrder(new Order( "XYZ.XM" , Side.SELL , 100 , 20.30F , 901));
        orderRequestService.processOrder(new Order("XYZ.XM" , Side.SELL , 100 , 20.25F , 903));
        orderRequestService.processOrder(new Order("XYZ.XM" , Side.SELL , 200 , 20.30F , 905));

        orderRequestService.processOrder(new Order( "XYZ.XM" , Side.BUY , 100 , 20.15F , 906));
        orderRequestService.processOrder(new Order( "XYZ.XM" , Side.BUY , 200 , 20.20F , 908));
        orderRequestService.processOrder(new Order( "XYZ.XM" , Side.BUY , 200 , 20.15F , 909));

        orderRequestService.processOrder(new Order( "XYZ.XM" , Side.BUY , 250 , 20.35F , 910));
    }

}
