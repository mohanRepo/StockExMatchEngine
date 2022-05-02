package com.dse.processor;

import com.dse.dao.OrdersDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.OrderBook;
import com.dse.service.QuoteService;
import com.dse.service.TradeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OrderProcessingEngineTest {


    @Mock
    Executor orderMatchExecutor;

    @Mock
    private QuoteService quoteService;

    @Mock
    private OrdersDao ordersDao;

    @Mock
    private TradeService tradeService;

    @Mock
    OrderBook orderBook;

    OrderProcessingEngine testClass;

    @Before
    public void setup()
    {
        testClass =  PowerMockito.spy( new OrderProcessingEngine());
        orderMatchExecutor = PowerMockito.mock(Executor.class);
        quoteService = PowerMockito.mock(QuoteService.class);
        ordersDao = PowerMockito.mock(OrdersDao.class);
        tradeService = PowerMockito.mock(TradeService.class);

        orderBook = PowerMockito.mock(OrderBook.class);

        Whitebox.setInternalState(testClass, "orderMatchExecutor" , orderMatchExecutor);
        Whitebox.setInternalState(testClass, "quoteService" , quoteService);
        Whitebox.setInternalState(testClass, "ordersDao" , ordersDao);
        Whitebox.setInternalState(testClass, "tradeService" , tradeService);
    }


    @Test
    public void testPlaceOrderBuyOrder()
    {
        Order buyOrder = new Order("ABC.XM", Side.BUY, 200, 20.15F, 909);
        OrderBook orderBookLocal = new OrderBook();

        PowerMockito.doReturn(orderBookLocal).when(ordersDao).getOrderBag("ABC.XM");

        testClass.placeOrder("ABC.XM" , buyOrder);

        Optional<Order> peek = orderBookLocal.peek(Side.BUY);

        Assert.assertTrue(peek.isPresent());

        verify(testClass , times(1)).updateQuote("ABC.XM" ,  Side.BUY , 20.15F);
    }

    @Test
    public void testPlaceOrderSellOrder()
    {
        Order buyOrder = new Order("ABC.XM", Side.SELL, 200, 20.15F, 909);
        OrderBook orderBookLocal = new OrderBook();

        PowerMockito.doReturn(orderBookLocal).when(ordersDao).getOrderBag("ABC.XM");

        testClass.placeOrder("ABC.XM" , buyOrder);

        Optional<Order> peek = orderBookLocal.peek(Side.SELL);
        Optional<Order> peekBuy = orderBookLocal.peek(Side.BUY);

        verify(testClass , times(1)).updateQuote("ABC.XM" ,  Side.SELL , 20.15F);

        Assert.assertTrue(peek.isPresent());
        Assert.assertTrue(peekBuy.isEmpty());
    }

    // TODO test matching trigger logic


}
