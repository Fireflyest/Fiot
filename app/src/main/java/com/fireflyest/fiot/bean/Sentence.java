package com.fireflyest.fiot.bean;

public class Sentence {

    private long id;

    private long home;

    private String name;

    private String target;

    private String data;

    private boolean select;

    public Sentence() {
    }

    public long getHome() {
        return home;
    }

    public void setHome(long home) {
        this.home = home;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
