package com.fireflyest.fiot.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fireflyest.fiot.bean.Scaned;

import java.util.ArrayList;
import java.util.List;

public class ScanViewModel extends ViewModel {

    private final MutableLiveData<Integer> amountData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> discoveryData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> btStateData = new MutableLiveData<>();
    private final MutableLiveData<Scaned> scanedData = new MutableLiveData<>();

    private static final List<String> addressList = new ArrayList<>();

    public static final String TAG = "ScanViewModel";

    /**
     * 接收蓝牙广播
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothDevice.ACTION_FOUND:
                    // 获取扫描结果
                    discoveryData.setValue(true);
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device == null || addressList.contains(device.getAddress()) || TextUtils.isEmpty(device.getName())) return;
                    Bundle bundle = intent.getExtras();
                    short rssi = bundle == null ? -100 : bundle.getShort(BluetoothDevice.EXTRA_RSSI);
                    // 记录
                    addressList.add(device.getAddress());
                    // 添加到列表
                    Scaned scaned = new Scaned(device.getName(), device.getAddress(), rssi+140, device.getBluetoothClass().getMajorDeviceClass());
                    scanedData.setValue(scaned);
                    // 刷新数量
                    amountData.setValue(amountData.getValue() == null ? 1 : amountData.getValue()+1);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    discoveryData.setValue(false);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState){
                        case BluetoothAdapter.STATE_OFF:
                            btStateData.setValue(false);
                            break;
                        case BluetoothAdapter.STATE_ON:
                            btStateData.setValue(true);
                            break;
                    }
                    break;
                default:
            }
        }
    };

    public ScanViewModel(){

    }

    public MutableLiveData<Integer> getAmountData() {
        return amountData;
    }

    public MutableLiveData<Boolean> getDiscoveryData() {
        return discoveryData;
    }

    public MutableLiveData<Boolean> getBtStateData() {
        return btStateData;
    }

    public MutableLiveData<Scaned> getScanedData() {
        return scanedData;
    }

    public BroadcastReceiver getReceiver() {
        return receiver;
    }

    public void clearAddressList(){
        addressList.clear();
    }

    public void initData(){
        amountData.setValue(0);
    }

}
