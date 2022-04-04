package com.fireflyest.fiot.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fireflyest.fiot.bean.Account;
import com.fireflyest.fiot.bean.BtDevice;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.net.AccountHttpRunnable;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.service.MqttIntentService;
import com.fireflyest.fiot.util.RSAUtils;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    // toolbar标题
    private final MutableLiveData<String> temperatureData = new MutableLiveData<>();
    private final MutableLiveData<String> humidityData = new MutableLiveData<>();
    private final MutableLiveData<Home> homeData = new MutableLiveData<>();
    private final MutableLiveData<List<Home>> homesData = new MutableLiveData<>();

    // 更新的设备
    private final MutableLiveData<BtDevice> btDeviceData = new MutableLiveData<>();
    private final MutableLiveData<Device> deviceData = new MutableLiveData<>();

    // 账户
    private final MutableLiveData<Account> accountData = new MutableLiveData<>();

    // 设备列表
    private final List<Device> devices = new ArrayList<>();

    // 蓝牙状态广播
    private final BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index;
            String address = null, data = null;
            Device device = null;
            Log.d(TAG, intent.getAction());


            switch (intent.getAction()){
                case BleIntentService.ACTION_GATT_CONNECTED:                // 蓝牙设备连接状态更新
                case BleIntentService.ACTION_GATT_CONNECT_LOSE:
                    address = intent.getStringExtra(BleIntentService.EXTRA_ADDRESS);
                    index = getDeviceIndex(address);
                    if(index == -1) return;
                    device = devices.get(index);
                    break;
                case MqttIntentService.ACTION_DEVICE_ONLINE:                   // 网络设备上线
                    address = intent.getStringExtra(MqttIntentService.EXTRA_TOPIC);
                    index = getDeviceIndex(address);
                    if(index == -1) return;
                    device = devices.get(index);
                    break;
                case MqttIntentService.ACTION_RECEIVER:                                // 网络数据接收
                    address = intent.getStringExtra(MqttIntentService.EXTRA_TOPIC);
                    index = getDeviceIndex(address);
                    if(index == -1) return;
                    device = devices.get(index);
                    // 获取数据
                    data = intent.getStringExtra(MqttIntentService.EXTRA_DATA);
                    if (data == null) return;
                    // 温湿度和状态更新
                    if(data.contains("=")){
                        String[] kv = data.split("=");
                        switch (kv[0]){
                            case "temp": // 温度
                                device.setState(String.format("温度%s℃", kv[1]));
                                temperatureData.setValue(kv[1]);
                                break;
                            case "humi": // 湿度
                                device.setState(device.getState() + String.format(" 湿度%s％", kv[1]));
                                humidityData.setValue(kv[1]);
                                break;
                            case "state":
                                device.setState(kv[1]);
                                break;
                            default:
                        }
                    }
                    break;
                case BleIntentService.ACTION_DATA_AVAILABLE:                        // 蓝牙数据接收
                    break;
                default:
            }

            if (device != null) {
                deviceData.setValue(device);
            }
        }
    };

    public MainViewModel(){
    }

    public static int getConnectState(String key){
        if(MqttIntentService.isConnected(key)) return 2;
        if(BleIntentService.isConnected(key)) return 1;
        return 0;
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

    public MutableLiveData<Home> getHomeData() {
        return homeData;
    }

    public MutableLiveData<List<Home>> getHomesData() {
        return homesData;
    }

    public MutableLiveData<BtDevice> getBtDeviceData() {
        return btDeviceData;
    }

    public MutableLiveData<Device> getDeviceData() {
        return deviceData;
    }

    public MutableLiveData<Account> getAccountData() {
        return accountData;
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

    // 由账户id获取账户
    public void updateAccount(long account, String token){
        new Thread( new AccountHttpRunnable(account, RSAUtils.publicEncrypt(token, RSAUtils.PUBLIC_KEY) , accountData)).start();
    }

    public void initData(){
        Home h = new Home();
        h.setName("我的家");
        homeData.setValue(h);

        Account a = new Account();
        a.setName("点击头像登录");
        accountData.setValue(a);
    }



}
