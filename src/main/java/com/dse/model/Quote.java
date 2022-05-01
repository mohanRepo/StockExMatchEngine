package com.dse.model;

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
}
