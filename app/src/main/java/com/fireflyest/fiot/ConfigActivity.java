package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.fireflyest.fiot.adapter.CharacteristicItemAdapter;
import com.fireflyest.fiot.adapter.ServiceItemAdapter;
import com.fireflyest.fiot.bean.Characteristic;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Service;
import com.fireflyest.fiot.databinding.ActivityConfigBinding;
import com.fireflyest.fiot.model.ConfigViewModel;
import com.fireflyest.fiot.util.StatusBarUtil;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ConfigActivity extends AppCompatActivity {

    public static final String TAG = ConfigActivity.class.getSimpleName();

    private ActivityConfigBinding binding;

    private ConfigViewModel model;

    private ServiceItemAdapter serviceItemAdapter;
    private CharacteristicItemAdapter characteristicItemAdapter;

    private final List<Service> services = new ArrayList<>();
    private final List<Characteristic> characteristics = new ArrayList<>();

    private Device rawDevice;

    public static final String EXTRA_SAVE_DEVICE = // 保存的设备
            "com.fireflyest.fiot.activity.extra.SAVE_DEVICE";
    public static final String EXTRA_EDIT_DEVICE = // 更改的设备
            "com.fireflyest.fiot.activity.extra.EDIT_DEVICE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config);

        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ConfigViewModel.class);

        this.initView();

//        // 已保存的数据
//        if (savedInstanceState != null) {
//            saveDevice = savedInstanceState.getParcelable(EXTRA_SAVE_DEVICE);
//            editDevice = savedInstanceState.getParcelable(EXTRA_EDIT_DEVICE);
//        }else {
//        }

        this.initData();

    }

    private void initView() {
        StatusBarUtil.StatusBarLightMode(this);

        setSupportActionBar(binding.configToolbar);

        // 服务特征列表适配器
        serviceItemAdapter = new ServiceItemAdapter(this, services);
        characteristicItemAdapter = new CharacteristicItemAdapter(this, characteristics);
        binding.configService.setAdapter(serviceItemAdapter);
        binding.configCharacteristic.setAdapter(characteristicItemAdapter);

        // 服务选项更新
        model.getServicesData().observe(this, s ->{
            if (s != null) {
                services.clear();
                // 放置一个空选项
                services.add(new Service("点击选择服务"));
                services.addAll(s);
                Log.d(TAG, "ServicesData ->" + services.toString());
                serviceItemAdapter.notifyDataSetChanged();
            }
        });

        // 特征选项更新
        model.getCharacteristicsData().observe(this, c ->{
            if (c != null) {
                characteristics.clear();
                // 放置一个空选项
                characteristics.add(new Characteristic("点击选择特征"));
                characteristics.addAll(c);
                Log.d(TAG, "CharacteristicsData ->" + c.toString());
                characteristicItemAdapter.notifyDataSetChanged();
            }
        });

        // 监控设备数据更新
        model.getDeviceData().observe(this, d -> {

            this.saveRawDevice(d);
            binding.setDevice(d);

            // 更新蓝牙服务可选列表
            model.initServiceData(d.getAddress());

            binding.configService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) return;
                    Service service = services.get(position);
                    String uuid = service.getUuid();
                    model.selectService(uuid);

                    Log.d(TAG, "SelectedService -> " + uuid);
                    binding.getDevice().setService(uuid);

                    // 如果设备有选择特征
                    if (d.getCharacteristic() != null){
                        int index = indexOfCharacteristic(d.getCharacteristic());
                        Log.d(TAG, "device characteristic index -> " + index);
                        binding.configCharacteristic.setSelection(index);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            // 如果设备有选择服务
            if (d.getService() != null){
                int index = indexOfService(d.getService());
                Log.d(TAG, "device service index -> " + index);
                binding.configService.setSelection(index);
            }

            binding.configCharacteristic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) return;
                    Characteristic characteristic = characteristics.get(position);
                    String uuid = characteristic.getUuid();

                    Log.d(TAG, "SelectedCharacteristic -> " + uuid);
                    binding.getDevice().setCharacteristic(uuid);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            // 展示
            binding.configDisplayText.setOnClickListener(v -> {
                boolean display = binding.configDisplaySwitch.isChecked();
                binding.configDisplaySwitch.setChecked(!display);
            });

            // 展示
            binding.configAutoText.setOnClickListener(v -> {
                boolean display = binding.configAutoSwitch.isChecked();
                binding.configAutoSwitch.setChecked(!display);
            });


        });

    }

    private void initData() {
        Intent intent = getIntent();
        Device device = intent.getParcelableExtra(ControlActivity.EXTRA_DEVICE);
        if (device != null) {
            model.getDeviceData().setValue(device);
        }
    }

    /**
     * 保存原有的设备来判断是否修改
     * @param device 设备
     */
    private void saveRawDevice(Device device){
        Log.d(TAG, "saveRawDevice -> " + device.toString());
        rawDevice = new Device(device.getId(),
                device.getName(),
                device.getAddress(),
                device.isDisplay(),
                device.getType(),
                device.getCreate(),
                device.isAuto());
        rawDevice.setNickname(device.getNickname());
        rawDevice.setConnect(device.isConnect());
        rawDevice.setService(device.getService());
        rawDevice.setCharacteristic(device.getCharacteristic());
    }

    /**
     * 返回上一个界面
     */
    private void back(){
        Log.d(TAG, "back after -> " + binding.getDevice().toString());
        Log.d(TAG, "back raw-> " + rawDevice.toString());
        Log.d(TAG, "数据更改 -> " + !binding.getDevice().equals(rawDevice));
        // 判断是否改变
        if(!binding.getDevice().equals(rawDevice)){
            AlertDialog saveDialog = new AlertDialog.Builder(this)
                    .setTitle("保存")
                    .setMessage("设备数据已更改，是否保存")
                    .setNegativeButton("不保存", (dialog, which) -> {
                        // 取消保存，直接回到上一个界面
                        dialog.dismiss();
                        this.finishAfterTransition();
                    })
                    .setNeutralButton("关闭", (dialog, which) -> dialog.cancel())
                    .setPositiveButton("保存", (dialog, which) -> {
                        // 保存数据
                        this.saveDevice();
                        dialog.dismiss();
                        this.finishAfterTransition();
                    })
                    .create();
            saveDialog.show();
        }else {
            // 未更改
            this.finishAfterTransition();
        }
    }

    public void saveDevice(){
        // 数据库保存
        // TODO: 2021/5/3 保存到数据库
        // 提供给上一个界面
        Intent intent = new Intent();
        // 未选择特征
        if (binding.configCharacteristic.getSelectedItemPosition() == 0){
            binding.getDevice().setCharacteristic(null);
        }
        intent.putExtra(ControlActivity.EXTRA_DEVICE, binding.getDevice());
        this.setResult(Activity.RESULT_OK, intent);
        // 更新原数据
        this.saveRawDevice(binding.getDevice());
        // 将更改完的设备保存
        ToastUtil.showShort(this, "保存成功");
    }

    @Override
    public void onBackPressed() {
        this.back();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        if (saveDevice != null) {
//            outState.putParcelable(ControlActivity.EXTRA_DEVICE, saveDevice);
//        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.back();
        }else if(id == R.id.menu_save){
            this.saveDevice();
        }
        return super.onOptionsItemSelected(item);
    }

    private int indexOfService(String uuid){
        int i = 1;
        for (Service service : services) {
            if (service.getUuid() == null) continue;
            if (service.getUuid().equals(uuid)) return i;
            i++;
        }
        return 0;
    }

    private int indexOfCharacteristic(String uuid){
        int i = 1;
        for (Characteristic characteristic : characteristics) {
            if (characteristic.getUuid() == null) continue;
            if (characteristic.getUuid().equals(uuid)) return i;
            i++;
        }
        return 0;
    }

}