package com.dse.model;

import com.dse.enums.Side;

import javax.print.attribute.standard.MediaSize;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Order implements Comparable<Order> {

    private Long orderId;
    private String security;
    private Side side;
    private long quantity; // we don't trade fraction
    private float price;
    private int time;
    static DateTimeFormatter formatHHmm;
    // TODO order type Limit or Market

    static
    {
        formatHHmm = DateTimeFormatter.ofPattern("HHmmss");
    }


    public Order(Long orderId, String security, Side side, long quantity, float price) {
        this.orderId = orderId;
        this.security = security;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.time = Integer.parseInt(LocalTime.now().format(formatHHmm));
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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

    private static int compare(Order o1 , Order o2){

        if(Math.abs(o1.price - o2.price) < Float.MIN_VALUE){ // when price equal compare time
            return (o1.time - o2.time);  //  earlier time at head
        }
        else return (o1.price - o2.price) < 0 ? 1 : -1; // higher price at head

    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", security='" + security + '\'' +
                ", side=" + side +
                ", quantity=" + quantity +
                ", price=" + price +
                ", time=" + time +
                '}';
    }

    @Override
    public int compareTo(Order other) { // TODO test sort logic
        return this.getSide() == Side.BUY ? compare(this , other) : compare(other , this);
    }
}
