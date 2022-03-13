package com.fireflyest.fiot.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Device implements Parcelable, Cloneable {

    private long id;

    // 蓝牙名称，用于判断设备类型
    private String name;

    // 设备备注名
    private String nickname;

    // 设备蓝牙地址，用于判断设备是否重复
    private String address;

    // 所在房间
    private String room;

    // 展示简介
    private String desc;

    // 创建时间
    private long create;

    // 设备是否展示
    private boolean display;

    // 连接状态
    private boolean connect;

    // 是否已经配置
    private boolean config;

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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public boolean isConfig() {
        return config;
    }

    public void setConfig(boolean config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", address='" + address + '\'' +
                ", room='" + room + '\'' +
                ", desc='" + desc + '\'' +
                ", create=" + create +
                ", display=" + display +
                ", connect=" + connect +
                ", config=" + config +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        if (id != device.id) return false;
        if (create != device.create) return false;
        if (display != device.display) return false;
        if (connect != device.connect) return false;
        if (config != device.config) return false;
        if (type != device.type) return false;
        if (!name.equals(device.name)) return false;
        if (!nickname.equals(device.nickname)) return false;
        if (!address.equals(device.address)) return false;
        if (!room.equals(device.room)) return false;
        return desc.equals(device.desc);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + nickname.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + room.hashCode();
        result = 31 * result + desc.hashCode();
        result = 31 * result + (int) (create ^ (create >>> 32));
        result = 31 * result + (display ? 1 : 0);
        result = 31 * result + (connect ? 1 : 0);
        result = 31 * result + (config ? 1 : 0);
        result = 31 * result + type;
        return result;
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
        dest.writeString(room);
        dest.writeString(desc);
        dest.writeLong(create);
        dest.writeInt(display ? 1 : 0);
        dest.writeInt(connect ? 1 : 0);
        dest.writeInt(type);
        dest.writeInt(config ? 1 : 0);
    }

    protected Device(Parcel in){
        id = in.readLong();
        name = in.readString();
        nickname = in.readString();
        address = in.readString();
        room = in.readString();
        desc = in.readString();
        create = in.readLong();
        display = in.readInt() == 1;
        connect = in.readInt() == 1;
        type = in.readInt();
        config = in.readInt() == 1;
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
