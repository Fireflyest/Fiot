package com.fireflyest.fiot.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireflyest.fiot.R;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.data.DeviceType;
import com.fireflyest.fiot.databinding.FragmentControlBinding;
import com.fireflyest.fiot.model.ControlViewModel;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.util.AnimationUtils;

public class ControlFragment extends Fragment {

    private static final String TAG = "ControlFragment";

    private FragmentControlBinding binding;
    private ControlViewModel model;

    private Device device;

    public ControlFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentControlBinding.inflate(inflater, container, false);
        model = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(ControlViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        device = model.getDeviceData().getValue();

        if (device == null) {
            return;
        }

        // 温湿度
        if ((device.getType() & DeviceType.ENVIRONMENT) != 0){
            AnimationUtils.show(binding.controlTemp);
            AnimationUtils.show(binding.controlHumi);
            AnimationUtils.show(binding.controlData);
        }
        // 环境控制
        if((device.getType() & DeviceType.ENVIRONMENT_CONTROL) != 0){
            AnimationUtils.show(binding.controlEnvironment);
        }
        // 亮度
        if((device.getType() & DeviceType.ILLUMINATION) != 0){
            AnimationUtils.show(binding.controlLuminance);
            String[] lightValues = new String[]{"耀眼", "敞亮", "昏暗", "黑暗"};
            binding.controlLuminanceValue.setTextArray(lightValues);
        }
        // 灯光
        if((device.getType() & DeviceType.ILLUMINATION_LIGHT) != 0){
            AnimationUtils.show(binding.controlLight);
        }
        // 彩灯
        if((device.getType() & DeviceType.ILLUMINATION_COLOR) != 0){
            AnimationUtils.show(binding.controlColor);
            binding.controlColorRed.setColorType(1);
            binding.controlColorGreen.setColorType(2);
            binding.controlColorBlue.setColorType(3);
        }
    }
}