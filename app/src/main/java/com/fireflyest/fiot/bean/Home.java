package com.fireflyest.fiot.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Fireflyest
 * 2022/1/19 21:37
 */

public class Home implements Parcelable {

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

    protected Home(Parcel in) {
        id = in.readLong();
        owner = in.readLong();
        name = in.readString();
        members = in.readString();
        background = in.readInt();
        wifi = in.readString();
        password = in.readString();
        rooms = in.readString();
    }

    public static final Creator<Home> CREATOR = new Creator<Home>() {
        @Override
        public Home createFromParcel(Parcel in) {
            return new Home(in);
        }

        @Override
        public Home[] newArray(int size) {
            return new Home[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(owner);
        dest.writeString(name);
        dest.writeString(members);
        dest.writeInt(background);
        dest.writeString(wifi);
        dest.writeString(password);
        dest.writeString(rooms);
    }




}
