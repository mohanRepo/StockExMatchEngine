package com.dse.model;

import java.util.Objects;

public class Quote {
    public Quote(String ric) {
        this.ric = ric;
    }

    private String ric;
    private float bid;
    private float ask;

    public String getRic() {
        return ric;
    }

    public float getBid() {
        return bid;
    }

    public float getAsk() {
        return ask;
    }

    public void setBid(float bid) {
        this.bid = bid;
        adjustSpread();
    }

    public void setAsk(float ask) {
        this.ask = ask;
        adjustSpread();
    }

    private void adjustSpread(){
        this.spread = this.ask - this.bid;
    }

    private float spread;

    public float getSpread() {
        return spread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return Float.compare(quote.bid, bid) == 0 && Float.compare(quote.ask, ask) == 0 && ric.equals(quote.ric);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ric, bid, ask);
    }
}
