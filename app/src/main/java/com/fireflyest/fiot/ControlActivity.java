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

import com.fireflyest.fiot.bean.BtDevice;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.data.DeviceType;
import com.fireflyest.fiot.databinding.ActivityControlBinding;
import com.fireflyest.fiot.model.ControlViewModel;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.ui.ControlNormalFragment;
import com.fireflyest.fiot.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class ControlActivity extends BaseActivity {

    public static final String TAG = "ControlActivity";

    public static final int REQUEST_CONFIG = 1;

    private ActivityControlBinding binding;

    private ControlViewModel model;

    private Home home;

    // 默认蓝牙服务
    public static final String DEFAULT_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String DEFAULT_CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static final List<String> DEVICES = new ArrayList<String>(){
        {
            add("WS-01");
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
            if(DEVICES.contains(device.getName()) && home.getWifi() != null && home.getPassword() != null){
                new Thread(() -> {
                    Log.d(TAG, "!device.isConfig() && DEVICES.contains(device.getName()) && home.getWifi() != null && home.getPassword() != null");
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();  }
                    BleIntentService.write(ControlActivity.this, device.getAddress(), DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID, ";R".getBytes());
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();  }
                    BleIntentService.write(ControlActivity.this, device.getAddress(), DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID, "W".getBytes());
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();  }
                    BleIntentService.write(ControlActivity.this, device.getAddress(), DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID, (home.getWifi()+";").getBytes());
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();  }
                    BleIntentService.write(ControlActivity.this, device.getAddress(), DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID, "P".getBytes());
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();  }
                    BleIntentService.write(ControlActivity.this, device.getAddress(), DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID, (home.getPassword()+";").getBytes());
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();  }
                    BleIntentService.write(ControlActivity.this, device.getAddress(), DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID, "U".getBytes());
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();  }
                    BleIntentService.write(ControlActivity.this, device.getAddress(), DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID, String.format("%s;", BaseActivity.DEBUG_URL).getBytes());
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();  }
                    BleIntentService.write(ControlActivity.this, device.getAddress(), DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID, "F".getBytes());

                }).start();
            }

            // 根据设备类型切换布局
            switch (device.getType()){
                case DeviceType.NON:
//                    this.startConfigActivity();
//                    break;
                case DeviceType.LOCAL:
                case DeviceType.REMOTE:
                default:
                    // 默认使用蓝牙传输
                    this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.control_fragment, new ControlNormalFragment())
                            .commit();
                    break;
                case DeviceType.ENVIRONMENT:
                    // TODO: 2021/4/30

                    break;
            }
        });

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
    protected void onDestroy() {
        model.getCommands().clear();
        super.onDestroy();
    }

}