package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

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
import com.fireflyest.fiot.model.ConfigViewModel;
import com.fireflyest.fiot.model.ControlViewModel;
import com.fireflyest.fiot.service.BluetoothIntentService;
import com.fireflyest.fiot.ui.ControlNormalFragment;
import com.fireflyest.fiot.util.StatusBarUtil;

public class ControlActivity extends AppCompatActivity {

    public static final String TAG = "ControlActivity";

    public static final String EXTRA_DEVICE = // 设备
            "com.fireflyest.fiot.activity.extra.DEVICE";

    private ActivityControlBinding binding;

    private ControlViewModel model;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_control);

        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ControlViewModel.class);

        this.initView();

        // 注册广播监听
        receiver = model.getReceiver();
        registerReceiver(receiver, new IntentFilter(BluetoothIntentService.ACTION_DATA_AVAILABLE));
        registerReceiver(receiver, new IntentFilter(BluetoothIntentService.ACTION_GATT_CONNECTED));
        registerReceiver(receiver, new IntentFilter(BluetoothIntentService.ACTION_GATT_CONNECT_LOSE));

    }

    private void initView() {
        StatusBarUtil.StatusBarLightMode(this);

        binding.controlToolbar.setTitle("");
        setSupportActionBar(binding.controlToolbar);
        binding.controlToolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        model.getDeviceData().observe(this, device -> {
            Log.d(TAG, device.toString());
            binding.setDeviceName(device.getName());
            if (device.getCharacteristic() != null) {
                binding.setDeviceCharacteristic(device.getCharacteristic().substring(0, 8));
            }else {
                binding.setDeviceCharacteristic("00000000");
            }
            switch (device.getType()){
                case DeviceType.NON:
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finishAfterTransition();
        }else if(item.getItemId() == R.id.menu_config){
            Intent intent = new Intent(this, ConfigActivity.class);
            intent.putExtra(EXTRA_DEVICE, model.getDeviceData().getValue());
            this.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

}