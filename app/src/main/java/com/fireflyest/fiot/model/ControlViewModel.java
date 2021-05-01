package com.fireflyest.fiot.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.service.BluetoothIntentService;

public class ControlViewModel extends ViewModel {

    // 更新的设备
    private final MutableLiveData<Device> deviceData = new MutableLiveData<>();

    private final BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra(BluetoothIntentService.EXTRA_ADDRESS);

            switch (intent.getAction()){
                case BluetoothIntentService.ACTION_GATT_CONNECTED:

                    break;
                case BluetoothIntentService.ACTION_GATT_CONNECT_LOSE:

                    break;
                case BluetoothIntentService.ACTION_DATA_AVAILABLE:

                    break;
                default:
            }
        }
    };

    public ControlViewModel() {
    }

    public BroadcastReceiver getReceiver() {
        return receiver;
    }

    public MutableLiveData<Device> getDeviceData() {
        return deviceData;
    }
}
