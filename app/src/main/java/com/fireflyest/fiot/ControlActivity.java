package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.data.DeviceType;
import com.fireflyest.fiot.databinding.ActivityControlBinding;
import com.fireflyest.fiot.model.ControlViewModel;
import com.fireflyest.fiot.net.DeviceTypeHttpRunnable;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.ui.ControlInitialFragment;
import com.fireflyest.fiot.ui.ControlNormalFragment;
import com.fireflyest.fiot.util.StatusBarUtil;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class ControlActivity extends BaseActivity {

    public static final String TAG = "ControlActivity";

    public static final int REQUEST_CONFIG = 1;

    private ActivityControlBinding binding;

    private ControlViewModel model;

    private Home home;

    // 默认蓝牙服务
    public static final String DEFAULT_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String DEFAULT_CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static final Map<String, Integer> DEVICE_TYPE_MAP = new HashMap<String, Integer>(){
        {
            put("WS-01", DeviceType.ENVIRONMENT | DeviceType.REMOTE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_control);

        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ControlViewModel.class);

        this.initView();

        // 注册广播监听
        BroadcastReceiver receiver = model.getReceiver();
        super.registerBroadcastReceiver(receiver,
                BleIntentService.ACTION_DATA_AVAILABLE,
                BleIntentService.ACTION_GATT_CONNECTED,
                BleIntentService.ACTION_GATT_CONNECT_LOSE,
                BleIntentService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCEED,
                BleIntentService.ACTION_GATT_CHARACTERISTIC_WRITE_FAIL);

    }

    private void initView() {
        StatusBarUtil.StatusBarLightMode(this);

        binding.controlToolbar.setTitle("");
        setSupportActionBar(binding.controlToolbar);
        binding.controlToolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        // 更新设备数据
        model.getDeviceData().observe(this, device -> {
            Log.d(TAG, device.toString());
            binding.setDevice(device);

            // 收录设备初始化
            if(DEVICE_TYPE_MAP.containsKey(device.getName()) &&
                    home.getWifi() != null &&
                    home.getPassword() != null &&
                    device.getType() == DeviceType.NON){
                Log.d(TAG, "初始化设备");
                this.initDevice(device, DEVICE_TYPE_MAP.get(device.getName()));
            }

            // 根据设备类型切换布局
            Log.d(TAG, String.format("设备类型%s", device.getType()));
            if(device.getType() == DeviceType.NON){
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.control_fragment, new ControlInitialFragment())
                        .commit();
            }else{
                // 默认蓝牙传输
                Log.d(TAG, "打开蓝牙传输界面");
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.control_fragment, new ControlNormalFragment())
                        .commit();
            }
        });

        // 获取主界面传输的数据
        Intent intent = getIntent();
        home = intent.getParcelableExtra(EXTRA_HOME);
        Device d = intent.getParcelableExtra(EXTRA_DEVICE);
        if (d != null) {
            model.getDeviceData().setValue(d);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        switch (requestCode) {
            case REQUEST_CONFIG:
                if (resultCode != Activity.RESULT_OK) return;
                Device device = data.getParcelableExtra(EXTRA_DEVICE);
                model.getDeviceData().setValue(device);
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finishAfterTransition();
        }else if(item.getItemId() == R.id.menu_config){
            model.getEditData().setValue(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_control, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        this.back();
    }

    @Override
    protected void onDestroy() {
        model.getCommands().clear();
        super.onDestroy();
    }

    private void back(){
        Intent intent = new Intent();
        Device device = model.getDeviceData().getValue();
        intent.putExtra(BaseActivity.EXTRA_DEVICE, device);
        setResult(Activity.RESULT_OK, intent);
        this.finishAfterTransition();
    }

    private void initDevice(Device device, int type){
        if(device.getId() == 0) return;
        ToastUtil.showLong(this, "正在初始化设备...");
        // 配置设备类型
        device.setType(type);
        new Thread(new DeviceTypeHttpRunnable(device.getId(), type, model.getDeviceData())).start();
        // 初始化设备
        new Thread(() -> {
            String address = device.getAddress();
            writeData(address, ";R".getBytes());
            writeData(address, "W".getBytes());
            writeData(address, home.getWifi().getBytes());
            writeData(address, ";".getBytes());
            writeData(address, "P".getBytes());
            writeData(address, home.getPassword().getBytes());
            writeData(address, ";".getBytes());
            writeData(address, "U".getBytes());
            writeData(address, String.format("%s", BaseActivity.DEBUG_URL).getBytes());
            writeData(address, ";".getBytes());
            writeData(address, "T".getBytes());
            writeData(address, String.format("H%s/%s", home.getId(), device.getAddress()).getBytes());
            writeData(address, ";".getBytes());
            writeData(address, "F".getBytes());
            writeData(address, "F".getBytes());
            writeData(address, "F".getBytes());
            writeData(address, "F".getBytes());
            writeData(address, "F".getBytes());
            writeData(address, "F".getBytes());
            writeData(address, "F".getBytes());
        }).start();
    }

    private void writeData(String address, byte[] data){
        BleIntentService.write(ControlActivity.this, address, DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID, data);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}