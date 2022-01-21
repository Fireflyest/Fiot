package com.fireflyest.fiot.bean;

public class Line {

    private String name;

    private int type;

    public Line() {
    }

    public Line(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
