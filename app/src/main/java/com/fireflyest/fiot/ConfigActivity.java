package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.fireflyest.fiot.adapter.CharacteristicItemAdapter;
import com.fireflyest.fiot.adapter.ServiceItemAdapter;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.databinding.ActivityConfigBinding;
import com.fireflyest.fiot.model.ConfigViewModel;
import com.fireflyest.fiot.util.StatusBarUtil;

import java.util.Collections;

public class ConfigActivity extends AppCompatActivity {

    private ActivityConfigBinding binding;

    private ConfigViewModel model;

    private ServiceItemAdapter serviceItemAdapter;
    private CharacteristicItemAdapter characteristicItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config);

        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ConfigViewModel.class);

        this.initView();

    }

    private void initView() {
        StatusBarUtil.StatusBarLightMode(this);

        serviceItemAdapter = new ServiceItemAdapter(this, Collections.emptyList(), () ->{

        });

        characteristicItemAdapter = new CharacteristicItemAdapter(this, Collections.emptyList(), () ->{

        });

        binding.configService.setAdapter(serviceItemAdapter);
        binding.configCharacteristic.setAdapter(characteristicItemAdapter);

        model.getServicesData().observe(this, services ->{
            serviceItemAdapter.setServices(services);
            serviceItemAdapter.notifyDataSetChanged();
        });

        model.getCharacteristicsData().observe(this, characteristics ->{
            characteristicItemAdapter.setCharacteristics(characteristics);
            characteristicItemAdapter.notifyDataSetChanged();
        });

        Intent intent = getIntent();
        Device device = intent.getParcelableExtra(ControlActivity.EXTRA_DEVICE);
        if (device != null) {
            model.initView(device.getAddress());
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