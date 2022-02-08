package com.fireflyest.fiot.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fireflyest.fiot.ControlActivity;
import com.fireflyest.fiot.HomeActivity;
import com.fireflyest.fiot.MainActivity;
import com.fireflyest.fiot.R;
import com.fireflyest.fiot.adapter.DeviceItemAdapter;
import com.fireflyest.fiot.anim.DeviceItemAnimator;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.databinding.FragmentDeviceBinding;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.util.ToastUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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

        // toolbar 更新
        model.getTemperatureData().observe(this.getViewLifecycleOwner(), temperature -> binding.setTemperature(temperature));
        model.getHumidityData().observe(this.getViewLifecycleOwner(), humidity -> binding.setHumidity(humidity));
        model.getHomeData().observe(this.getViewLifecycleOwner(), home -> binding.setHome(home));

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
                intent.putExtra("home", model.getHomeData().getValue());
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_HOME);
                popupWindow.dismiss();
            });

            popupWindow.setTouchable(true);
//            popupWindow.setTouchInterceptor((vp, event) -> {
//                vp.callOnClick();
//                return false;
//                // 这里如果返回true的话，touch事件将被拦截
//                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//            });
            popupWindow.showAsDropDown(v, 50, 0);

            // 背景暗淡
            if (activity != null) {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 0.7f;//设置阴影透明度
                activity.getWindow().setAttributes(lp);
                popupWindow.setOnDismissListener(() -> {
                    WindowManager.LayoutParams lp1 = activity.getWindow().getAttributes();
                    lp1.alpha = 1f;
                    activity.getWindow().setAttributes(lp1);
                });
            }

//            PopupMenu popupMenu = new PopupMenu(getContext(), v, Gravity.BOTTOM, 0, R.style.popMenu_style);
//            popupMenu.getMenuInflater().inflate(R.menu.menu_home, popupMenu.getMenu());
//            if (model.getHomesData().getValue() != null) {
//                for (Home home : model.getHomesData().getValue()) {
//                    popupMenu.getMenu().add(home.getName());
//                }
//            }
//            popupMenu.show();
        });

        //  房间管理
        binding.roomMore.setOnClickListener(v -> {
            View popView = LayoutInflater.from(getContext()).inflate(R.layout.pop_rooms,  binding.deviceToolbar, false);
            final PopupWindow popupWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // 选择的家
            TextView select = popView.findViewById(R.id.room_select);

            //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
            //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
            //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
            popupWindow.setTouchable(true);
//            popupWindow.setTouchInterceptor((vp, event) -> {
//                vp.callOnClick();
//                return false;
//                // 这里如果返回true的话，touch事件将被拦截
//                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//            });
            popupWindow.showAsDropDown(v, 0, 0);

            // 背景暗淡
            if (activity != null) {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 0.7f;//设置阴影透明度
                activity.getWindow().setAttributes(lp);
                popupWindow.setOnDismissListener(() -> {
                    WindowManager.LayoutParams lp1 = activity.getWindow().getAttributes();
                    lp1.alpha = 1f;
                    activity.getWindow().setAttributes(lp1);
                });
            }

//            PopupMenu popupMenu = new PopupMenu(getContext(), v, Gravity.BOTTOM, 0, R.style.popMenu_style);
//            popupMenu.getMenuInflater().inflate(R.menu.menu_home, popupMenu.getMenu());
//            if (model.getHomesData().getValue() != null) {
//                for (Home home : model.getHomesData().getValue()) {
//                    popupMenu.getMenu().add(home.getName());
//                }
//            }
//            popupMenu.show();
        });

        // 设备列表
        binding.deviceEmpty.setVisibility(View.VISIBLE);
        // 设备更新
        model.getDeviceData().observe(this.getViewLifecycleOwner(), device ->{
            if(binding.deviceEmpty.getVisibility() == View.VISIBLE) {
                binding.deviceEmpty.setVisibility(View.GONE);
            }
            // 判断是添加还是修改
            int index = model.getDeviceIndex(device.getAddress());
            if(-1 != index){
                deviceItemAdapter.updateItem(index);
            }else {
                deviceItemAdapter.addItem(device);
                // todo 配置wifi
            }
        });
        deviceItemAdapter = new DeviceItemAdapter(view.getContext(), model.getDevices());
        // 设备点击
        deviceItemAdapter.setClickListener((device, background) -> {
            if(device.isConnect()){
                // 已连接控制打开界面
                Intent intent = new Intent(this.getActivity(), ControlActivity.class);
                intent.putExtra(ControlActivity.EXTRA_DEVICE, device);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this.requireActivity(), background, "device_background");
                this.startActivity(intent, options.toBundle());
            }else {
                // 未连接就先连接
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