package com.fireflyest.fiot.bean;

public class Characteristic {

    private String name;

    private String uuid;

    private String usage;

    private String proprty;

    public Characteristic(String name, String uuid, String usage, String proprty) {
        this.name = name;
        this.uuid = uuid;
        this.usage = usage;
        this.proprty = proprty;
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

    public String getProprty() {
        return proprty;
    }

    public void setProprty(String proprty) {
        this.proprty = proprty;
    }
}
