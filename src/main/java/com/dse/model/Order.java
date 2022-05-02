package com.dse.model;

import com.dse.enums.Side;

import javax.print.attribute.standard.MediaSize;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class Order implements Comparable<Order> , Cloneable {

    private static AtomicLong idTracker = new AtomicLong(0);
    private Long orderId;
    private String security;
    private Side side;
    private int quantity; // we don't trade fraction
    private float price;

    public int getTime() {
        return time;
    }

    private int time = Integer.parseInt(LocalTime.now().format(formatHHmm)); // TODO this needs to time
    private int version;

    // TODO order type Limit or Market

    static DateTimeFormatter formatHHmm;

    static
    {
        formatHHmm = DateTimeFormatter.ofPattern("HHmmss");
    }



    public Order(String security, Side side, int quantity, float price , int time)  {
        this.orderId = idTracker.incrementAndGet();
        this.security = security;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.version = 1;
        this.time = time;
    }


    public Order(String security, Side side, int quantity, float price)  {
        this.orderId = idTracker.incrementAndGet();
        this.security = security;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.version = 1;
        this.time = Integer.parseInt(LocalTime.now().format(formatHHmm));
    }



    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
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

    public int getVersion() {
        return version;
    }

    public Optional<Order> getNewVersion(int quantity)
    {
        try {
            Order newVersion = (Order)this.clone();
            newVersion.version +=1;
            newVersion.quantity = quantity;
            return Optional.of(newVersion);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return version == order.version && Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, version);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", version=" + version +
                ", security='" + security + '\'' +
                ", side=" + side +
                ", quantity=" + quantity +
                ", price=" + price +
                ", time=" + time +
                '}';
    }

    @Override
    public int compareTo(Order other) { // TODO test sort logic

        float diff = this.price - other.price;
        if(Math.abs(diff) < 0.00001){ // when price equal compare time
            return (this.time - other.time) ;  //  earlier time at head
        }
      //  else return (this.price - other.price) < 0 ? 1 : -1; // higher price at head
        else {
            diff = diff / 0.00001F;
            return  (int) (this.getSide() == Side.SELL ? diff : -diff);
        }
    }
}
