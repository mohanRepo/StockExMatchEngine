package com.dse.model;

import com.dse.enums.Side;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class Trade {

    private long tradeId;
    private long orderId;
    private String security;
    private Side side;
    private int tradeQuantity;
    private float price;
    private LocalDateTime time;


    public Trade(Long tradeId, Long orderId, String security, Side side, int tradeQuantity, float price) {
        this.tradeId = tradeId;
        this.orderId = orderId;
        this.security = security;
        this.side = side;
        this.tradeQuantity = tradeQuantity;
        this.price = price;
        this.time = LocalDateTime.now();
    }


    public Long getTradeId() {
        return tradeId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getSecurity() {
        return security;
    }

    public Side getSide() {
        return side;
    }

    public int getTradeQuantity() {
        return tradeQuantity;
    }

    public float getPrice() {
        return price;
    }

    public LocalDateTime getTime() {
        return time;
    }




}
