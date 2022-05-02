package com.dse.service;

import com.dse.dao.TradeDao;
import com.dse.model.Order;
import com.dse.model.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeService {

    @Autowired
    private TradeDao tradeDao;

    private static final Logger logger = LoggerFactory.getLogger(OrderRequestService.class);

    public List<Trade> getAllTrades()
    {
        return tradeDao.getAllTrades();
    }

    public void createTrade(String security, Order order, int tradeQuantity , float tradePrice) {
        logger.info("trade created for order : {} , security: {} , quantity: {}", order, security, tradeQuantity);
        tradeDao.saveTrade(createTradeFrom(order, tradeQuantity , tradePrice));
    }

    public Trade createTradeFrom(Order order, int tradeQuantity , float tradePrice) {
        return new Trade(order.getOrderId() * 10 + order.getVersion(),
                order.getOrderId(), order.getSecurity(), order.getSide(), tradeQuantity, tradePrice);
    }
}
