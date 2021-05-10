package com.fireflyest.fiot.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Device implements Parcelable, Cloneable {

    private long id;

    private String name;

    private String nickname;

    private String address;

    private String service;

    private String characteristic;

    private long create;

    private boolean display;

    private boolean connect;

    private boolean auto;

    private int type;

    public Device(long id, String name, String address, boolean display, int type, long create, boolean auto) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.display = display;
        this.type = type;
        this.create = create;
        this.auto = auto;
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

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
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
                ", auto=" + auto +
                ", type=" + type +
                '}';
    }

    /**
     * 只判断数据是否一样就行
     * @param o 要对比的对象
     * @return 是否数据相同
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        if (id != device.id) return false;
        if (create != device.create) return false;
        if (display != device.display) return false;
        if (type != device.type) return false;
        if (auto != device.auto) return false;
        if (!nickname.equals(device.nickname)) return false;
        if (!Objects.equals(name, device.name)) return false;
        if (!Objects.equals(address, device.address)) return false;
        if (!Objects.equals(service, device.service)) return false;
        return Objects.equals(characteristic, device.characteristic);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + nickname.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + service.hashCode();
        result = 31 * result + characteristic.hashCode();
        result = 31 * result + (int) (create ^ (create >>> 32));
        result = 31 * result + (display ? 1 : 0);
        result = 31 * result + (connect ? 1 : 0);
        result = 31 * result + (auto ? 1 : 0);
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
        dest.writeString(service);
        dest.writeString(characteristic);
        dest.writeLong(create);
        dest.writeInt(display ? 1 : 0);
        dest.writeInt(connect ? 1 : 0);
        dest.writeInt(auto ? 1 : 0);
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
        auto = in.readInt() == 1;
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
