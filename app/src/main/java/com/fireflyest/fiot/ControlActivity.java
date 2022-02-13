package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.data.DeviceType;
import com.fireflyest.fiot.databinding.ActivityControlBinding;
import com.fireflyest.fiot.model.ControlViewModel;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.ui.ControlNormalFragment;
import com.fireflyest.fiot.util.StatusBarUtil;

public class ControlActivity extends BaseActivity {

    public static final String TAG = "ControlActivity";

    public static final String EXTRA_DEVICE = // 设备
            "com.fireflyest.fiot.activity.extra.DEVICE";

    public static final int REQUEST_CONFIG = 1;

    private ActivityControlBinding binding;

    private ControlViewModel model;

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

            // 根据设备类型切换布局
            switch (device.getType()){
                case DeviceType.NON:
//                    this.startConfigActivity();
//                    break;
                case DeviceType.LOCAL:
                case DeviceType.REMOTE:
                default:
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
        Device d = intent.getParcelableExtra(EXTRA_DEVICE);
        if (d != null) {
            model.getDeviceData().setValue(d);
        }

    }

    private void startConfigActivity(){
        Intent intent = new Intent(this, ConfigActivity.class);
        intent.putExtra(EXTRA_DEVICE, model.getDeviceData().getValue());
        this.startActivityForResult(intent, REQUEST_CONFIG, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
            this.startConfigActivity();
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