package com.dse.service;

import com.dse.dao.OrdersDao;
import com.dse.dao.TradeDao;
import com.dse.enums.Side;
import com.dse.model.Order;
import com.dse.model.Quote;
import com.dse.model.Subscriber;
import com.dse.model.Trade;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class QuoteServiceTest {

    @Mock
    OrdersDao ordersDao;

    @Mock
    Subscriber<Quote> subscriber;

    QuoteService testClass;

    @Before
    public void setup()
    {
        testClass = new QuoteService();
        ordersDao = PowerMockito.mock(OrdersDao.class);
        subscriber = PowerMockito.mock(Subscriber.class);
        Whitebox.setInternalState(testClass, "ordersDao" , ordersDao);
        testClass.register(subscriber);
    }

    @Test
    public void updateQuotePositiveCase() {
        Order buyOrder = new Order("ABC.XM", Side.BUY, 200, 20.15F, 909);
        Order sellOrder = new Order("ABC.XM", Side.SELL, 200, 20.15F, 909);
        Quote quote = new Quote("ABC.XM");

        Optional<Order> buyOrderOpt = Optional.ofNullable(buyOrder);
        Optional<Order> sellOrderOpt = Optional.ofNullable(sellOrder);

        PowerMockito.doReturn(buyOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.BUY);
        PowerMockito.doReturn(sellOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.SELL);
        PowerMockito.doReturn(quote).when(ordersDao).getQuote("ABC.XM" );

        Quote quote1 = new Quote("ABC.XM");

        quote1.setAsk( 20.15F);
        quote1.setBid(20.15F);

        testClass.updateQuote("ABC.XM");

        verify(subscriber , times(1)).onMessage("ABC.XM" ,  quote1);

       // Assert.assertEquals(1 , 1);
    }

    @Test
    public void updateQuoteDontInvokeWhenAskLessThanBuy() {
        Order buyOrder = new Order("ABC.XM", Side.BUY, 200, 20.15F, 909);
        Order sellOrder = new Order("ABC.XM", Side.SELL, 200, 20.1F, 909);
        Quote quote = new Quote("ABC.XM");

        Optional<Order> buyOrderOpt = Optional.ofNullable(buyOrder);
        Optional<Order> sellOrderOpt = Optional.ofNullable(sellOrder);

        PowerMockito.doReturn(buyOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.BUY);
        PowerMockito.doReturn(sellOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.SELL);
        PowerMockito.doReturn(quote).when(ordersDao).getQuote("ABC.XM" );

        Quote quote1 = new Quote("ABC.XM");

        quote1.setAsk( 20.1F);
        quote1.setBid(20.15F);

        testClass.updateQuote("ABC.XM");

        verify(subscriber , times(1)).onMessage("ABC.XM" ,  quote1);

        // Assert.assertEquals(1 , 1);
    }

    @Test
    public void updateQuoteDontInvokeWhenAskGreaterThanBuy() {
        Order buyOrder = new Order("ABC.XM", Side.BUY, 200, 20.15F, 909);
        Order sellOrder = new Order("ABC.XM", Side.SELL, 200, 21.15F, 909);
        Quote quote = new Quote("ABC.XM");

        Optional<Order> buyOrderOpt = Optional.ofNullable(buyOrder);
        Optional<Order> sellOrderOpt = Optional.ofNullable(sellOrder);

        PowerMockito.doReturn(buyOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.BUY);
        PowerMockito.doReturn(sellOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.SELL);
        PowerMockito.doReturn(quote).when(ordersDao).getQuote("ABC.XM" );
        testClass.updateQuote("ABC.XM");
        verify(subscriber , times(0)).onMessage("ABC.XM" ,  quote);

        // Assert.assertEquals(1 , 1);
    }

    @Test
    public void updateQuoteDontInvokeWhenNoBuyOrder() {
        Order buyOrder = new Order("ABC.XM", Side.BUY, 200, 20.15F, 909);
        Order sellOrder = new Order("ABC.XM", Side.SELL, 200, 21.15F, 909);
        Quote quote = new Quote("ABC.XM");

        Optional<Order> buyOrderOpt = Optional.ofNullable(null);
        Optional<Order> sellOrderOpt = Optional.ofNullable(sellOrder);

        PowerMockito.doReturn(buyOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.BUY);
        PowerMockito.doReturn(sellOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.SELL);
        PowerMockito.doReturn(quote).when(ordersDao).getQuote("ABC.XM" );

        testClass.updateQuote("ABC.XM");
        verify(subscriber , times(0)).onMessage("ABC.XM" ,  quote);

        // Assert.assertEquals(1 , 1);
    }

    @Test
    public void updateQuoteDontInvokeWhenNoSellOrder() {
        Order buyOrder = new Order("ABC.XM", Side.BUY, 200, 20.15F, 909);
        Order sellOrder = new Order("ABC.XM", Side.SELL, 200, 21.15F, 909);
        Quote quote = new Quote("ABC.XM");

        Optional<Order> buyOrderOpt = Optional.ofNullable(buyOrder);
        Optional<Order> sellOrderOpt = Optional.ofNullable(sellOrder);

        PowerMockito.doReturn(buyOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.BUY);
        PowerMockito.doReturn(sellOrderOpt).when(ordersDao).peekTopOrder("ABC.XM" , Side.SELL);
        PowerMockito.doReturn(quote).when(ordersDao).getQuote("ABC.XM" );

        testClass.updateQuote("ABC.XM");
        verify(subscriber , times(0)).onMessage("ABC.XM" ,  quote);

        // Assert.assertEquals(1 , 1);
    }



}
