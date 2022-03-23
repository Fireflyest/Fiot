package com.fireflyest.fiot.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.fireflyest.fiot.data.DeviceType;

public class Device implements Parcelable {

    private long id;

    // 蓝牙名称，用于判断设备类型
    private String name;

    // 设备备注名
    private String nickname;

    // 归属
    private long owner;

    // 设备蓝牙地址，用于判断设备是否重复
    private String address;

    // 所在房间
    private String room;

    // 类型
    private int type;

    public Device(long id, String name, long owner, String address, String room, int type) {
        this.id = id;
        this.name = name;
        this.nickname = name;
        this.owner = owner;
        this.address = address;
        this.room = room;
        this.type = type;
    }

    public Device(BtDevice btDevice) {
        this.name = btDevice.getName();
        this.nickname = btDevice.getName();
        this.address = btDevice.getAddress();
        this.type = DeviceType.NON;
    }

    public Device() {
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
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
                ", owner=" + owner +
                ", address='" + address + '\'' +
                ", room='" + room + '\'' +
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
        dest.writeString(room);
        dest.writeInt(type);
    }

    protected Device(Parcel in){
        id = in.readLong();
        name = in.readString();
        nickname = in.readString();
        address = in.readString();
        room = in.readString();
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
