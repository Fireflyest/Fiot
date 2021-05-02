package com.fireflyest.fiot.bean;

public class Characteristic {

    private String name;

    private String uuid;

    private String service;

    private String usage;

    private String proprty;

    private boolean select;

    private boolean selectAble;

    public Characteristic(String name, String uuid, String service, String usage, String proprty) {
        this.name = name;
        this.uuid = uuid;
        this.service = service;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isSelectAble() {
        return selectAble;
    }

    public void setSelectAble(boolean selectAble) {
        this.selectAble = selectAble;
    }

    @Override
    public String toString() {
        return "Characteristic{" +
                "name='" + name + '\'' +
                '}';
    }
}
