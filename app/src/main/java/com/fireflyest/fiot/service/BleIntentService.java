package com.fireflyest.fiot.service;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用于蓝牙操作
 */
public class BleIntentService extends IntentService {

    private final BluetoothAdapter bluetoothAdapter;

    private static final List<String> disconnectList = new ArrayList<>();
    private static final Map<String, BluetoothGatt> gattMap = new HashMap<>();

    private static final UUID BLUETOOTH_NOTIFY_D = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final String TAG = BleIntentService.class.getSimpleName();

    public static final String ACTION_GATT_CONNECT =// 连接
            "com.fireflyest.fiot.service.action.GATT_CONNECT";
    public static final String ACTION_GATT_CONNECTED = // 连接成功
            "com.fireflyest.fiot.service.action.GATT_CONNECTED";
    public static final String ACTION_GATT_CONNECT_LOSE = // 连接丢失
            "com.fireflyest.fiot.service.action.GATT_CONNECT_LOSE";
    public static final String ACTION_GATT_DISCONNECTED = // 连接断开
            "com.fireflyest.fiot.service.action.GATT_DISCONNECTED";
    public static final String ACTION_GATT_CHARACTERISTIC_READ = // 读取
            "com.fireflyest.fiot.service.action.GATT_CHARACTERISTIC_READ";
    public static final String ACTION_GATT_CHARACTERISTIC_READ_FAIL = // 读取失败
            "com.fireflyest.fiot.service.action.GATT_CHARACTERISTIC_READ_FAIL";
    public static final String ACTION_GATT_CHARACTERISTIC_WRITE = // 写入
            "com.fireflyest.fiot.service.action.GATT_CHARACTERISTIC_WRITE";
    public static final String ACTION_GATT_CHARACTERISTIC_WRITE_SUCCEED = // 写入成功
            "com.fireflyest.fiot.service.action.GATT_CHARACTERISTIC_WRITE_SUCCEED";
    public static final String ACTION_GATT_CHARACTERISTIC_WRITE_FAIL = // 写入失败
            "com.fireflyest.fiot.service.action.GATT_CHARACTERISTIC_WRITE_FAIL";
    public static final String ACTION_DATA_AVAILABLE = // 收到数据
            "com.fireflyest.fiot.service.action.DATA_AVAILABLE";

    public static final String EXTRA_DATA = // 数据
            "com.fireflyest.fiot.service.extra.DATA";
    public static final String EXTRA_ADDRESS = // 蓝牙地址
            "com.fireflyest.fiot.service.extra.ADDRESS";
    public static final String EXTRA_NAME =
            "com.fireflyest.fiot.service.extra.NAME";
    public static final String EXTRA_SERVICE = // 蓝牙服务
            "com.fireflyest.fiot.service.extra.SERVICE";
    public static final String EXTRA_CHARACTERISTIC = // 特征
            "com.fireflyest.fiot.service.extra.CHARACTERISTIC";

    public BleIntentService() {
        super("BleIntentService");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String address = gatt.getDevice().getAddress();
            if (newState == BluetoothProfile.STATE_CONNECTED) { //连接成功
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {   //断开连接
                broadcastUpdate(ACTION_GATT_CONNECT_LOSE, address);
                // 非手动断开，自动尝试重新连接重新
                if (!disconnectList.contains(address)) {
                    disconnectList.add(address);
                    gatt.connect();
                }
                gattMap.remove(address);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            String address = gatt.getDevice().getAddress();
            String name = gatt.getDevice().getName();
            broadcastUpdate(ACTION_GATT_CONNECTED, address, name);
            gattMap.put(address, gatt);
            for(BluetoothGattService service : gatt.getServices()){
                for(BluetoothGattCharacteristic  characteristic: service.getCharacteristics()){
                    if((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) continue;
                    if (gatt.setCharacteristicNotification(characteristic, true)){
                        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(BLUETOOTH_NOTIFY_D);
                        if (clientConfig == null) continue;
                        clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(clientConfig);
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "读取数据: "+ Arrays.toString(characteristic.getValue()));
            broadcastDataAction(ACTION_DATA_AVAILABLE, gatt.getDevice().getAddress(), characteristic.getValue());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String address = gatt.getDevice().getAddress();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "数据发送成功 -> " + new String(characteristic.getValue()));
                broadcastDataAction(ACTION_GATT_CHARACTERISTIC_WRITE_SUCCEED, address, characteristic.getValue());
            } else {
                Log.d(TAG, "数据发送结果失败 -> " + new String(characteristic.getValue()));
                broadcastDataAction(ACTION_GATT_CHARACTERISTIC_WRITE_FAIL, address, characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "接收数据: "+ Arrays.toString(characteristic.getValue()));
            broadcastDataAction(ACTION_DATA_AVAILABLE, gatt.getDevice().getAddress(), characteristic.getValue());
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    /**
     * 蓝牙连接
     * @param context 上下文
     * @param address 地址
     */
    public static void connect(Context context, String address) {
        Intent intent = new Intent(context, BleIntentService.class);
        intent.setAction(ACTION_GATT_CONNECT);
        intent.putExtra(EXTRA_ADDRESS, address);
        context.startService(intent);
    }

    /**
     * 断开蓝牙连接
     * @param context 上下文
     * @param address 地址
     */
    public static void disconnect(Context context, String address) {
        Intent intent = new Intent(context, BleIntentService.class);
        intent.setAction(ACTION_GATT_DISCONNECTED);
        intent.putExtra(EXTRA_ADDRESS, address);
        context.startService(intent);
    }

    /**
     * 写入数据
     * @param context 上下文
     * @param address 地址
     * @param data 数据
     */
    public static void write(Context context, String address, String service, String characteristic, byte[] data){
        Intent intent = new Intent(context, BleIntentService.class);
        intent.setAction(ACTION_GATT_CHARACTERISTIC_WRITE);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_SERVICE, service);
        intent.putExtra(EXTRA_CHARACTERISTIC, characteristic);
        intent.putExtra(EXTRA_DATA, data);
        context.startService(intent);
    }

    /**
     * 读取某特征的数据
     * @param context 上下文
     * @param address 地址
     * @param service 服务uuid
     * @param characteristic 特征uuid
     */
    public static void readCharacteristic(Context context, String address, String service, String characteristic){
        Intent intent = new Intent(context, BleIntentService.class);
        intent.setAction(ACTION_GATT_CHARACTERISTIC_READ);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_SERVICE, service);
        intent.putExtra(EXTRA_CHARACTERISTIC, characteristic);
        context.startService(intent);
    }

    public  static boolean isConnected(String address){
        return gattMap.containsKey(address);
    }

    public static List<BluetoothGattService> getService(String address){
        BluetoothGatt gatt = gattMap.get(address);
        if (gatt == null) {
            return null;
        }
        return gatt.getServices();
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String address, service, characteristic;
            switch (intent.getAction()){
                case ACTION_GATT_CONNECT:
                    address = intent.getStringExtra(EXTRA_ADDRESS);
                    this.handleActionConnect(address);
                    break;
                case ACTION_GATT_DISCONNECTED:
                    address = intent.getStringExtra(EXTRA_ADDRESS);
                    this.handleActionDisconnect(address);
                    break;
                case ACTION_GATT_CHARACTERISTIC_WRITE:
                    address = intent.getStringExtra(EXTRA_ADDRESS);
                    byte[] data = intent.getByteArrayExtra(EXTRA_DATA);
                    service = intent.getStringExtra(EXTRA_SERVICE);
                    characteristic = intent.getStringExtra(EXTRA_CHARACTERISTIC);
                    this.handleActionWrite(address, service, characteristic, data);
                case ACTION_GATT_CHARACTERISTIC_READ:
                    address = intent.getStringExtra(EXTRA_ADDRESS);
                    service = intent.getStringExtra(EXTRA_SERVICE);
                    characteristic = intent.getStringExtra(EXTRA_CHARACTERISTIC);
                    this.handleActionReadCharacteristic(address, service, characteristic);
                default:
            }
        }
    }

    private void handleActionConnect(String address) {
        if(!bluetoothAdapter.isEnabled()){
            Log.e(TAG, "蓝牙未开启");
            return;
        }
        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(address);
        if (remoteDevice == null) {
            Log.e(TAG, "未找到该蓝牙设备 -> " + address);
            return;
        }
        // 非自动断连，断开后会尝试重新连接
        disconnectList.remove(address);

        remoteDevice.connectGatt(this, false, gattCallback);
        Log.d(TAG, "正在连接蓝牙设备 -> " + address);
    }

    private void handleActionDisconnect(String address){
        BluetoothGatt gatt = gattMap.get(address);
        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
            broadcastUpdate(ACTION_GATT_DISCONNECTED, address);
        }
        gattMap.remove(address);
        disconnectList.add(address);
    }

    private void handleActionWrite(String address, String service, String characteristic, byte[] data) {
        if (TextUtils.isEmpty(service) || TextUtils.isEmpty(characteristic)) {
            broadcastDataAction(ACTION_GATT_CHARACTERISTIC_WRITE_FAIL, address, data);
            Log.e(TAG, "未配置服务特征 -> " + address);
            return;
        }

        if(!bluetoothAdapter.isEnabled()){
            broadcastDataAction(ACTION_GATT_CHARACTERISTIC_WRITE_FAIL, address, data);
            Log.e(TAG, "蓝牙未开启 ->" + address);
            return;
        }

        BluetoothGatt gatt = gattMap.get(address);
        if (gatt == null) {
            broadcastDataAction(ACTION_GATT_CHARACTERISTIC_WRITE_FAIL, address, data);
            Log.e(TAG, "蓝牙未连接 ->" + address);
            return;
        }

        BluetoothGattService gattService = gatt.getService(UUID.fromString(service));
        if (gattService == null) {
            broadcastDataAction(ACTION_GATT_CHARACTERISTIC_WRITE_FAIL, address, data);
            Log.e(TAG, "服务不存在 ->" + address);
            return;
        }

        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(characteristic));
        if (gattCharacteristic == null) {
            broadcastDataAction(ACTION_GATT_CHARACTERISTIC_WRITE_FAIL, address, data);
            Log.e(TAG, "特征不存在 -> " + address);
            return;
        }

        if ((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
                && (gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0
                && (gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) != 0){
            Log.e(TAG, "特征无法写入 -> " + address);
            return;
        }

        gattCharacteristic.setValue(data);
        gatt.writeCharacteristic(gattCharacteristic);
    }

    private void handleActionReadCharacteristic(String address, String service, String characteristic) {
        BluetoothGatt gatt = gattMap.get(address);
        if (gatt == null) {
            Log.e(TAG, "蓝牙未连接 -> " + address);
            return;
        }

        BluetoothGattService gattService = gatt.getService(UUID.fromString(service));
        if (gattService == null) {
            broadcastUpdate(ACTION_GATT_CHARACTERISTIC_READ_FAIL, address);
            Log.e(TAG, "服务不存在 ->" + address);
            return;
        }

        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(characteristic));
        if (gattCharacteristic == null) {
            broadcastUpdate(ACTION_GATT_CHARACTERISTIC_READ_FAIL, address);
            Log.e(TAG, "特征不存在 -> " + address);
            return;
        }

        gatt.readCharacteristic(gattCharacteristic);
    }



    /**
     * 用广播更新UI界面
     * @param action 标记
     * @param address 蓝牙地址
     */
    private void broadcastUpdate(String action, String address){
        broadcastUpdate(action, address, null);
    }

    private void broadcastUpdate(String action, String address, String name){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_NAME, name);
        sendBroadcast(intent);
    }

    private void broadcastDataAction(String action, String address, byte[] data){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_DATA, data);
        sendBroadcast(intent);
    }

}