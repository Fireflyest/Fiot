package com.fireflyest.fiot.bean;

public class Setting {

    private String name;

    private int type;

    private String stringValue;

    private boolean value;

    private boolean enable;

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

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Setting() {
        this.type = 0;
    }

    public Setting(String name, int type, String stringValue, boolean value, boolean enable) {
        this.name = name;
        this.type = type;
        this.stringValue = stringValue;
        this.value = value;
        this.enable = enable;
    }

    public Setting(String name) {
        this.name = name;
        this.type = 1;
    }
}
