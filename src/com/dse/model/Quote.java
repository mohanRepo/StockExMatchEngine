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

    public void setRic(String ric) {
        this.ric = ric;
    }

    public float getBid() {
        return bid;
    }

    public void setBid(float bid) {
        this.bid = bid;
        adjustSpread();

    }

    public float getAsk() {
        return ask;

    }

    public void setAsk(float ask) {
        this.ask = ask;
        adjustSpread();

    }

    private void adjustSpread(){
        this.spread = this.ask - this.bid;
        if(this.spread <= 0.00000001)
        {
            message(this.ric , this);
        }
    }

    private void message(String topicRic ,Quote quote)
    {

    }

    private float spread;

    public float getSpread() {
        return spread;
    }
}
