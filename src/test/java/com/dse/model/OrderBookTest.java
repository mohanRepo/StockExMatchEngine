package com.dse.model;

import com.dse.enums.Side;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OrderBookTest {


    OrderBook testClass;


    @Before
    public void setup() {
        testClass = new OrderBook();
    }

    @Test
    public void addInOrderForSellOrdersTest()
    {
        Order o1 = new Order( "ABC.XM" , Side.SELL , 100 , 20.30F , 901);
        Order o2 = new Order("ABC.XM" , Side.SELL , 100 , 20.25F , 903);
        Order o3 = new Order("ABC.XM" , Side.SELL , 200 , 20.30F , 905);


        testClass.add(o1);
        testClass.add(o2);
        testClass.add(o3);

        Assert.assertEquals(testClass.poll(Side.SELL).getOrderId(),o2.getOrderId());
        Assert.assertEquals(testClass.poll(Side.SELL).getOrderId(),o1.getOrderId());
        Assert.assertEquals(testClass.poll(Side.SELL).getOrderId(),o3.getOrderId());

    }

    @Test
    public void addInOrderForBuyOrdersTest()
    {
        Order o1 = new Order( "ABC.XM" , Side.BUY , 100 , 20.15F , 906);
        Order o2 = new Order( "ABC.XM" , Side.BUY , 200 , 20.20F , 908);
        Order o3 = new Order( "ABC.XM" , Side.BUY , 200 , 20.15F , 909);


        testClass.add(o1);
        testClass.add(o2);
        testClass.add(o3);

        Assert.assertEquals(testClass.poll(Side.BUY).getOrderId(),o2.getOrderId());
        Assert.assertEquals(testClass.poll(Side.BUY).getOrderId(),o1.getOrderId());
        Assert.assertEquals(testClass.poll(Side.BUY).getOrderId(),o3.getOrderId());

    }

}
