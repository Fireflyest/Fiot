package com.fireflyest.fiot.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fireflyest.fiot.bean.Account;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.util.RSAUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    // toolbar标题
    private final MutableLiveData<String> temperatureData = new MutableLiveData<>();
    private final MutableLiveData<String> humidityData = new MutableLiveData<>();
    private final MutableLiveData<String> homeData = new MutableLiveData<>();

    // 更新的设备
    private final MutableLiveData<Device> deviceData = new MutableLiveData<>();

    // 账户
    private final MutableLiveData<Account> dataAccount = new MutableLiveData<>();

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

    public MutableLiveData<Device> getDeviceData() {
        return deviceData;
    }

    public MutableLiveData<Account> getDataAccount() {
        return dataAccount;
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
        new Thread( new HttpRunnable(account, RSAUtils.publicEncrypt(token, RSAUtils.PUBLIC_KEY) , dataAccount)).start();
    }

    public void initData(){
        temperatureData.setValue("X");
        humidityData.setValue("X");
        homeData.setValue("我的家");
    }

    static class HttpRunnable implements Runnable {

        private final long account;
        private final String token;
        private final MutableLiveData<Account> data;

        public HttpRunnable(long account, String token, MutableLiveData<Account> data) {
            this.account = account;
            this.token = token;
            this.data = data;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/account")
            HttpUrl url = HttpUrl.get("http://192.168.2.115:8080/account")
                    .newBuilder()
                    .addQueryParameter("account", String.valueOf(account))
                    .addQueryParameter("token", token)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                ResponseBody body = response.body();
                if (body == null) return;
                Account account = new Gson().fromJson(body.string(), Account.class);
                if (account != null) data.postValue(account);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
