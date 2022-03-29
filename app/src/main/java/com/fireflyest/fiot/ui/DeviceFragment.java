package com.fireflyest.fiot.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fireflyest.fiot.BaseActivity;
import com.fireflyest.fiot.ControlActivity;
import com.fireflyest.fiot.HomeActivity;
import com.fireflyest.fiot.MainActivity;
import com.fireflyest.fiot.R;
import com.fireflyest.fiot.adapter.DeviceItemAdapter;
import com.fireflyest.fiot.anim.DeviceItemAnimator;
import com.fireflyest.fiot.bean.Account;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.data.DeviceType;
import com.fireflyest.fiot.databinding.FragmentDeviceBinding;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.net.DeviceCreateHttpRunnable;
import com.fireflyest.fiot.net.DevicesHttpRunnable;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.service.MqttIntentService;
import com.fireflyest.fiot.util.AnimationUtils;
import com.fireflyest.fiot.util.DpOrPxUtil;

public class DeviceFragment extends Fragment {

    private static final String TAG = "DeviceFragment";

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

        // 温湿度
        model.getTemperatureData().observe(this.getViewLifecycleOwner(), temperature -> {
            binding.mainTemperatureIcon.setVisibility(View.VISIBLE);
            binding.mainTemperatureValue.setVisibility(View.VISIBLE);
            binding.setTemperature(temperature);
        });
        model.getHumidityData().observe(this.getViewLifecycleOwner(), humidity -> {
            binding.mainHumidityIcon.setVisibility(View.VISIBLE);
            binding.mainHumidityValue.setVisibility(View.VISIBLE);
            binding.setHumidity(humidity);
        });
        // toolbar 更新
        model.getHomeData().observe(this.getViewLifecycleOwner(), home -> {
            binding.setHome(home);
            // 连接mqtt服务器
            // TODO: 2022/3/23 参数
            Account account = model.getAccountData().getValue();
            if(home == null || home.getId() == 0 || account == null) return;
            MqttIntentService.createClient(getContext(), String.format("H%s", home.getId()), String.valueOf(home.getOwner()), "password");
            // 设备列表
            new Thread(new DevicesHttpRunnable(account.getId(), home.getId(), model.getDeviceData())).start();
        });

        // 房间更新
        binding.roomSelect.setTextArray("全部");
        // 环境


        // toolbar动画
        binding.deviceToolbar.setTitle("");
        binding.deviceAppbar.addOnOffsetChangedListener((v,offset) ->{
            float scrollRange = v.getTotalScrollRange();
            binding.deviceToolbar.setAlpha(1+offset/scrollRange*1.9F);

            binding.mainTemperatureIcon.setAlpha(1+offset/scrollRange*1.5F);
            binding.mainTemperatureValue.setAlpha(1+offset/scrollRange*1.5F);
            binding.mainHumidityIcon.setAlpha(1+offset/scrollRange*1.5F);
            binding.mainHumidityValue.setAlpha(1+offset/scrollRange*1.5F);

            binding.deviceSearch.setTranslationY(offset/scrollRange*200F);
            binding.deviceSearch.setAlpha(1+offset/scrollRange);

            binding.roomSelect.setAlpha(0-offset/scrollRange*1.5F);
        });

        //  家庭管理
        binding.homeMore.setOnClickListener(v -> {
            View popView = LayoutInflater.from(getContext()).inflate(R.layout.pop_homes,  binding.deviceToolbar, false);
            final PopupWindow popupWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // 选择的家
            TextView select = popView.findViewById(R.id.homes_select);
            Home selectHome = model.getHomeData().getValue();
            if (selectHome != null) {
                select.setText(selectHome.getName());
            }

            // 打开家庭管理
            TextView manager = popView.findViewById(R.id.homes_manager);
            manager.setOnClickListener(vm -> {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.putExtra(BaseActivity.EXTRA_HOME, model.getHomeData().getValue());
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_HOME);
                popupWindow.dismiss();
            });

            popupWindow.setTouchable(true);
            popupWindow.showAsDropDown(v, 0, DpOrPxUtil.dip2px(v.getContext(), -20));
        });

        //  房间管理
        binding.roomMore.setOnClickListener(v -> {
            View popView = LayoutInflater.from(getContext()).inflate(R.layout.pop_rooms,  binding.deviceToolbar, false);
            final PopupWindow popupWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // 选择的家
            TextView select = popView.findViewById(R.id.room_select);

            popupWindow.setTouchable(true);
            popupWindow.showAsDropDown(v, 0, DpOrPxUtil.dip2px(v.getContext(), -20));

        });

        // 设备列表
        binding.deviceEmpty.setVisibility(View.VISIBLE);
        // 蓝牙设备更新
        model.getBtDeviceData().observe(this.getViewLifecycleOwner(), btDevice ->{
            if(binding.deviceEmpty.getVisibility() == View.VISIBLE) {
                AnimationUtils.hide(binding.deviceEmpty);
            }
            Device device = new Device(btDevice);
            device.setType(DeviceType.NON);
            deviceItemAdapter.addItem(device);
            // 如果在线，存到服务器
            Account account = model.getAccountData().getValue();
            Home home = model.getHomeData().getValue();
            if (account != null && home != null && home.getId() != 0) {
                Log.d(TAG, "账户：" + account.getId() + " 家：" + home.getId());
                new Thread(
                        new DeviceCreateHttpRunnable(account.getId(), home.getId(), device.getName(), device.getAddress(), device.getRoom(), device.getType(), model.getDeviceData())).start();
            }
        });
        // 设备更新
        model.getDeviceData().observe(this.getViewLifecycleOwner(), device -> {
            if(binding.deviceEmpty.getVisibility() == View.VISIBLE) {
                AnimationUtils.hide(binding.deviceEmpty);
            }
            Log.d(TAG, String.format("设备更新：%s", device.toString()));
            String address = device.getAddress();
            // 判断是添加还是修改
            int index = model.getDeviceIndex(address);
            if(-1 != index){
                model.getDevices().set(index, device);
                deviceItemAdapter.updateItem(index);
            }else {
                deviceItemAdapter.addItem(device);
                // mqtt订阅
                MqttIntentService.subscribe(getContext(), device.getAddress());
            }
        });

        deviceItemAdapter = new DeviceItemAdapter(view.getContext(), model.getDevices());
        // 设备点击
        deviceItemAdapter.setClickListener((device, background) -> {
            // 如果蓝牙已连接或是网络设备，打开
            if(MainViewModel.getConnectState(device.getAddress()) != 0){
                // 已连接控制打开界面
                Intent intent = new Intent(this.getActivity(), ControlActivity.class);
                intent.putExtra(BaseActivity.EXTRA_DEVICE, device);
                intent.putExtra(BaseActivity.EXTRA_HOME, model.getHomeData().getValue());
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this.requireActivity(), background, "device_background");
                this.startActivityForResult(intent, MainActivity.REQUEST_CONTROL, options.toBundle());
            }else if((device.getType() & DeviceType.REMOTE) != 0){
                // 刷新设备网络状态
                MqttIntentService.send(getContext(), device.getAddress(), "?");
            }else {
                // 尝试蓝牙连接
                BleIntentService.connect(getContext(), device.getAddress());
            }
        });

        //设备长按
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