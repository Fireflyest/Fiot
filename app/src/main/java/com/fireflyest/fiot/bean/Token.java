package com.fireflyest.fiot.bean;

public class Token {

    private long id;

    private String value;

    private long date;

    public Token() {
    }

    public Token(long id, String value, long date) {
        this.id = id;
        this.value = value;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
