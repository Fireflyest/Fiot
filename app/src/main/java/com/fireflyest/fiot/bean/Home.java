package com.fireflyest.fiot.bean;

/**
 * @author Fireflyest
 * 2022/1/19 21:37
 */

public class Home {

    private long id;

    private long owner;

    private String name;

    private String members;

    private int background;

    private String wifi;

    private String password;

    private String rooms;

    public Home() {
    }

    public Home(long id, long owner, String name, String members, int background, String wifi, String password, String rooms) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.members = members;
        this.background = background;
        this.wifi = wifi;
        this.password = password;
        this.rooms = rooms;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }
}
