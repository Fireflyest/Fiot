package com.fireflyest.fiot.bean;

public class Scaned {

    private String name;

    private String address;

    private int rssi;

    private int type;

    public Scaned(String name, String address, int rssi, int type) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
