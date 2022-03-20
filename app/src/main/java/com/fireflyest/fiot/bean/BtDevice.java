package com.fireflyest.fiot.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BtDevice implements Parcelable, Cloneable {

    private long id;

    // 蓝牙名称，用于判断设备类型
    private String name;

    // 设备蓝牙地址，用于判断设备是否重复
    private String address;

    public BtDevice(long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public BtDevice() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BtDevice btDevice = (BtDevice) o;

        if (id != btDevice.id) return false;
        if (!name.equals(btDevice.name)) return false;
        return address.equals(btDevice.address);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + address.hashCode();
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
        dest.writeString(address);
    }

    protected BtDevice(Parcel in){
        id = in.readLong();
        name = in.readString();
        address = in.readString();
    }

    public static final Creator<BtDevice> CREATOR = new Creator<BtDevice>() {
        @Override
        public BtDevice createFromParcel(Parcel source) {
            return new BtDevice(source);
        }

        @Override
        public BtDevice[] newArray(int size) {
            return new BtDevice[size];
        }
    };

}
