package com.fireflyest.fiot.bean;

public class Service {

    private String name;

    private String uuid;

    private String usage;

    private boolean select;

    public Service() {
    }

    public Service(String name) {
        this.name = name;
    }

    public Service(String name, String uuid, String usage) {
        this.name = name;
        this.uuid = uuid;
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                '}';
    }
}
