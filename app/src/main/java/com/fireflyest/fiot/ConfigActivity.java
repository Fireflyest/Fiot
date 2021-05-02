package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.fireflyest.fiot.adapter.CharacteristicItemAdapter;
import com.fireflyest.fiot.adapter.ServiceItemAdapter;
import com.fireflyest.fiot.bean.Characteristic;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Service;
import com.fireflyest.fiot.databinding.ActivityConfigBinding;
import com.fireflyest.fiot.model.ConfigViewModel;
import com.fireflyest.fiot.util.CalendarUtil;
import com.fireflyest.fiot.util.StatusBarUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config);

        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ConfigViewModel.class);

        this.initView();

    }

    private void initView() {
        StatusBarUtil.StatusBarLightMode(this);

        setSupportActionBar(binding.configToolbar);

        serviceItemAdapter = new ServiceItemAdapter(this, services);
        characteristicItemAdapter = new CharacteristicItemAdapter(this, characteristics);
        binding.configService.setAdapter(serviceItemAdapter);
        binding.configCharacteristic.setAdapter(characteristicItemAdapter);
        binding.configService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Service service = services.get(position);
                String uuid = service.getUuid();
                model.selectCharacteristic(uuid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.configCharacteristic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        model.getCharacteristicsData().observe(this, c ->{
            if (c != null) {
                characteristics.clear();
                characteristics.addAll(c);
                Log.d(TAG, "CharacteristicsData ->" + c.toString());
                characteristicItemAdapter.notifyDataSetChanged();
            }
        });

        model.getServicesData().observe(this, s ->{
            if (s != null) {
                services.clear();
                services.addAll(s);
                Log.d(TAG, "ServicesData ->" + services.toString());
                serviceItemAdapter.notifyDataSetChanged();
            }
        });

        model.getDeviceData().observe(this, d -> {
            binding.setDeviceName(d.getName());
            binding.setCreateTime("创建于: " + CalendarUtil.convertTime(d.getCreate()));

            model.initView(d.getAddress());
        });

        Intent intent = getIntent();
        Device device = intent.getParcelableExtra(ControlActivity.EXTRA_DEVICE);
        if (device != null) {
            model.getDeviceData().setValue(device);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

}