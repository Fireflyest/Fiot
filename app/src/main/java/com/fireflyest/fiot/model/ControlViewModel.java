package com.fireflyest.fiot.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fireflyest.fiot.bean.Command;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.data.CommandType;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.util.CalendarUtil;
import com.fireflyest.fiot.util.ConvertUtil;

import java.util.ArrayList;
import java.util.List;

public class ControlViewModel extends ViewModel {

    private static final String TAG = ControlViewModel.class.getSimpleName();
    // 更新的设备
    private final MutableLiveData<Device> deviceData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> hexData = new MutableLiveData<>();

    private final MutableLiveData<Command> commandData = new MutableLiveData<>();

    private final List<Command> commands = new ArrayList<>();

    private String text;

    private final BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra(BleIntentService.EXTRA_ADDRESS);
            Boolean hex = hexData.getValue();
            if (hex == null) hex = false;
            switch (intent.getAction()){
                case BleIntentService.ACTION_GATT_CONNECTED:

                    break;
                case BleIntentService.ACTION_GATT_CONNECT_LOSE:

                    break;
                case BleIntentService.ACTION_DATA_AVAILABLE:
                    {
                        byte[] bytesData = intent.getByteArrayExtra(BleIntentService.EXTRA_DATA);
                        String data = hex && bytesData != null  ? ConvertUtil.bytesToHexString(bytesData) :
                                new String(bytesData);
                        if(TextUtils.isEmpty(data)) break;
                        Log.d(TAG, "接收到数据 -> " + data);
                        commandData.setValue(new Command(0,
                                deviceData.getValue().getAddress(),
                                false,
                                true, CalendarUtil.getDate(),
                                data, CommandType.RECEIVE));
                    }
                    break;
                case BleIntentService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCEED:
                case BleIntentService.ACTION_GATT_CHARACTERISTIC_WRITE_FAIL:
                    {
                        boolean success = intent.getAction().equals(BleIntentService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCEED);
                        Log.d(TAG, "写入数据反馈广播 -> " + (success ? "写入成功" : "写入失败"));
                        byte[] bytesData = intent.getByteArrayExtra(BleIntentService.EXTRA_DATA);
                        String data = hex && bytesData != null  ? ConvertUtil.bytesToHexString(bytesData) :
                                new String(bytesData).substring(0, bytesData.length-1);
                        Log.d(TAG, "更新指令 -> " + data);
                        Command command = getCommand(data);
                        if (command == null) return;
                        command.setSending(false);
                        command.setSuccess(success);
                        commandData.setValue(command);
                        break;
                    }
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

    public MutableLiveData<Boolean> getHexData() {
        return hexData;
    }

    public MutableLiveData<Command> getCommandData() {
        return commandData;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Command getCommand(String data){
        for (Command command : commands) {
            if (!command.getText().equalsIgnoreCase(data) || !command.isSending()) continue;
            return command;
        }
        return null;
    }
}
