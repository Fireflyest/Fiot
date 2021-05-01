package com.fireflyest.fiot.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable {

    private long id;

    private String name;

    private String nickname;

    private String address;

    private String service;

    private String characteristic;

    private long create;

    private boolean display;

    private boolean connect;

    private int type;

    public Device(long id, String name, String address, boolean display, int type, long create) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.display = display;
        this.type = type;
        this.create = create;
    }

    public Device() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreate() {
        return create;
    }

    public void setCreate(long create) {
        this.create = create;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", address='" + address + '\'' +
                ", service='" + service + '\'' +
                ", characteristic='" + characteristic + '\'' +
                ", create=" + create +
                ", display=" + display +
                ", connect=" + connect +
                ", type=" + type +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(nickname);
        dest.writeString(address);
        dest.writeString(service);
        dest.writeString(characteristic);
        dest.writeLong(create);
        dest.writeInt(display ? 1 : 0);
        dest.writeInt(connect ? 1 : 0);
        dest.writeInt(type);
    }

    protected Device(Parcel in){
        id = in.readLong();
        name = in.readString();
        nickname = in.readString();
        address = in.readString();
        service = in.readString();
        characteristic = in.readString();
        create = in.readLong();
        display = in.readInt() == 1;
        connect = in.readInt() == 1;
        type = in.readInt();
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel source) {
            return new Device(source);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

}
