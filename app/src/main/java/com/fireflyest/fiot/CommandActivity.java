package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.databinding.ActivityCommandBinding;
import com.fireflyest.fiot.model.CommandViewModel;
import com.fireflyest.fiot.model.ControlViewModel;
import com.fireflyest.fiot.net.DeviceListHttpRunnable;
import com.fireflyest.fiot.service.MqttIntentService;
import com.fireflyest.fiot.util.CommandUtils;
import com.fireflyest.fiot.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandActivity extends BaseActivity {

    private static final String TAG = "CommandActivity";

    private ActivityCommandBinding binding;

    private CommandViewModel model;

    private ArrayAdapter<String> deviceArrayAdapter;
    private ArrayAdapter<String> commandArrayAdapter;

    private boolean flag = false;
    private Device device;
    private String control = "无操作";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_command);

        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CommandViewModel.class);

        this.initView();
    }

    private void initView(){
        StatusBarUtil.StatusBarLightMode(this);

        binding.commandToolbar.setTitle("");
        setSupportActionBar(binding.commandToolbar);

        // 获取主界面传输的家庭数据
        Home home = getIntent().getParcelableExtra(BaseActivity.EXTRA_HOME);
        if (home == null) {
            return;
        }

        // 监测设备列表变化
        model.getDeviceListData().observe(this, deviceList -> {
            List<String> deviceNameList = new ArrayList<>();
            for (Device device : deviceList) {
                deviceNameList.add(device.getNickname());
            }
            Log.d(TAG, String.format("devices=%s", deviceNameList.toString()));
            deviceArrayAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, deviceNameList);
            //第三步：设置下拉列表下拉时的菜单样式
            deviceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            binding.commandDeviceSpinner.setAdapter(deviceArrayAdapter);
        });

        // 设备选择
        binding.commandDeviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Device> deviceList = model.getDeviceListData().getValue();
                if (deviceList == null || deviceList.size() < position) {
                    return;
                }
                device = deviceList.get(position);
                List<String> commandList = CommandUtils.getCommands(device.getType());
                commandArrayAdapter = new ArrayAdapter<>(CommandActivity.this, R.layout.item_spinner, commandList);
                //第三步：设置下拉列表下拉时的菜单样式
                commandArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                binding.commandSpinner.setAdapter(commandArrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 指令选择
        binding.commandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                control = ((TextView)view).getText().toString();
                binding.commandDataEdit.setText(CommandUtils.getCommandData(control));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 点击添加
        binding.commandSave.setOnClickListener(view -> {
            flag = true;
            back();
        });

        // 获取设备列表
        new Thread(new DeviceListHttpRunnable(home.getOwner(), home.getId(), model.getDeviceListData())).start();
        Log.d(TAG, String.format("DeviceListHttpRunnable owner=%s home=%s", home.getOwner(), home.getId()));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) back();
        return true;
    }

    @Override
    public void onBackPressed() {
        this.back();
        super.onBackPressed();
    }

    private void back(){
        Intent intent = new Intent();
        if (flag && control != null && device != null) {
            intent.putExtra(MqttIntentService.EXTRA_DATA, binding.commandDataEdit.getText().toString());
            intent.putExtra(BaseActivity.EXTRA_DEVICE, device);
            intent.putExtra(BaseActivity.EXTRA_NAME, control+device.getNickname());
            this.setResult(RESULT_OK, intent);
        }else {
            this.setResult(RESULT_CANCELED);
        }
        this.finishAfterTransition();
    }

}