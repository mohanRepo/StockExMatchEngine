package com.dse.service;

import com.dse.dao.TradeDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.Trade;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TradeServiceTest {
    @Mock
    TradeDao tradeDao;

    TradeService tradeService;

    @Before
    public void setup()
    {
        tradeService = new TradeService();
        tradeDao = PowerMockito.mock(TradeDao.class);
        Whitebox.setInternalState(tradeService, "tradeDao" , tradeDao);
    }

    @Test
    public void createTradeTest()
    {
        Order order = new Order("ABC.XM", Side.BUY, 200, 20.15F, 909);
        tradeService.createTrade("ABC.XM" , order, 100 , 20.15F);
        verify(tradeDao , times(1)).saveTrade(Mockito.any(Trade.class));
    }

    @Test
    public void createTradeFromTest()
    {
        Order order = new Order("ABC.XM", Side.BUY, 200, 20.15F, 909);
        Trade trade = tradeService.createTradeFrom(order, 100 , 20.15F);
        Assert.assertEquals(trade.getOrderId() , order.getOrderId());
        Assert.assertEquals(trade.getTradeQuantity() , 100);
        long tradeID = order.getOrderId() * 10 + order.getVersion();
        long otherTradeID = trade.getTradeId();
        Assert.assertEquals(tradeID , otherTradeID);
        // TODO match all fiels
    }


}
