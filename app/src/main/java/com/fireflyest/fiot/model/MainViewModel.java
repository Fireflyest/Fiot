package com.fireflyest.fiot.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.service.BleIntentService;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    // toolbar标题
    private final MutableLiveData<String> temperatureData = new MutableLiveData<>();
    private final MutableLiveData<String> humidityData = new MutableLiveData<>();
    private final MutableLiveData<String> homeData = new MutableLiveData<>();
    private final MutableLiveData<String> onlineData = new MutableLiveData<>();

    // 更新的设备
    private final MutableLiveData<Device> deviceData = new MutableLiveData<>();

    // 设备列表
    private final List<Device> devices = new ArrayList<>();

    // 蓝牙状态广播
    private final BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra(BleIntentService.EXTRA_ADDRESS);
            int index = getDeviceIndex(address);
            if(index == -1) return;
            Device device = devices.get(index);
            switch (intent.getAction()){
                case BleIntentService.ACTION_GATT_CONNECTED:
                    if (device.isConnect()) return;
                    device.setConnect(true);
                    deviceData.setValue(device);
                    break;
                case BleIntentService.ACTION_GATT_CONNECT_LOSE:
                    if (!device.isConnect()) return;
                    device.setConnect(false);
                    deviceData.setValue(device);
                    break;
                case BleIntentService.ACTION_DATA_AVAILABLE:

                    break;
                default:
            }
        }
    };

    public MainViewModel(){
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public MutableLiveData<String> getTemperatureData() {
        return temperatureData;
    }

    public MutableLiveData<String> getHumidityData() {
        return humidityData;
    }

    public MutableLiveData<String> getHomeData() {
        return homeData;
    }

    public MutableLiveData<String> getOnlineData() {
        return onlineData;
    }

    public MutableLiveData<Device> getDeviceData() {
        return deviceData;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public BroadcastReceiver getReceiver() {
        return receiver;
    }

    public int getDeviceIndex(String address){
        int i = -1;
        for (Device d : devices) {
            if (d.getAddress().equals(address)){
                i = devices.indexOf(d);
                break;
            }
        }
        return i;
    }

    public void initData(){
        temperatureData.setValue("X");
        humidityData.setValue("X");
        homeData.setValue("我的家");
        onlineData.setValue("1");
    }

}
