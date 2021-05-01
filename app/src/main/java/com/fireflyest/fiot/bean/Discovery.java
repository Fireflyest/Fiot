package com.fireflyest.fiot.bean;

public class Discovery {

    private boolean enable;

    private int amount;

    public Discovery(boolean enable, int amount) {
        this.enable = enable;
        this.amount = amount;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void addAmount( ) {
        this.amount++;
    }
}
