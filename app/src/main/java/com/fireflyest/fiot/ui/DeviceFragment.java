package com.fireflyest.fiot.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireflyest.fiot.ControlActivity;
import com.fireflyest.fiot.R;
import com.fireflyest.fiot.ScanActivity;
import com.fireflyest.fiot.adapter.DeviceItemAdapter;
import com.fireflyest.fiot.anim.DeviceItemAnimator;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.databinding.FragmentDeviceBinding;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.service.BluetoothIntentService;

public class DeviceFragment extends Fragment {

    private FragmentDeviceBinding binding;
    private MainViewModel model;

    private DeviceItemAdapter deviceItemAdapter;

    public DeviceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeviceBinding.inflate(inflater, container, false);
        model = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) activity.setSupportActionBar(binding.deviceToolbar);
        this.setHasOptionsMenu(true);

        model.getBluetoothStateData().observe(this, state -> binding.setBluetoothState(state));
        model.getNetStateData().observe(this, state -> binding.setNetState(state));
        model.getServerData().observe(this, server -> binding.setServer(server));

        model.getDeviceData().observe(this, device ->{
            // 判断是添加还是修改
            int index = model.getDeviceIndex(device.getAddress());
            if(-1 != index){
                deviceItemAdapter.updateItem(index, device);
            }else {
                deviceItemAdapter.addItem(device);
            }
        });
        deviceItemAdapter = new DeviceItemAdapter(view.getContext(), model.getDevices());
        deviceItemAdapter.setClickListener((device, name) -> {
            if(device.isConnect()){
                // 已连接控制打开界面
                Intent intent = new Intent(this.getActivity(), ControlActivity.class);
                intent.putExtra(ControlActivity.EXTRA_DEVICE, device);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this.requireActivity(), name, "device_name");
                this.startActivity(intent, options.toBundle());
            }else {
                // 未连接就先连接
                BluetoothIntentService.connect(getContext(), device.getAddress());
            }
        });
        deviceItemAdapter.setLongClickListener(device -> {
//            int index = model.getDeviceIndex(address);
//            if(-1 == index) return;
//            Device d = model.getDevices().get(index);
//            if(d.isConnect()) BluetoothIntentService.disconnect(getContext(), d.getAddress());
//            deviceItemAdapter.removeItem(index);
        });
        binding.deviceList.setAdapter(deviceItemAdapter);
        binding.deviceList.setItemAnimator(new DeviceItemAnimator());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

}