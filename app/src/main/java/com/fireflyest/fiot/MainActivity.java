package com.fireflyest.fiot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.fireflyest.fiot.adapter.ViewPagerAdapter;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.data.DeviceType;
import com.fireflyest.fiot.databinding.ActivityMainBinding;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.service.BluetoothIntentService;
import com.fireflyest.fiot.ui.DeviceFragment;
import com.fireflyest.fiot.ui.MineFragment;
import com.fireflyest.fiot.util.CalendarUtil;
import com.fireflyest.fiot.util.StatusBarUtil;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH = 1;
    public static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private MainViewModel model;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 绑定model
        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);

        this.initView();

        // 注册广播监听
        receiver = model.getReceiver();
        registerReceiver(receiver, new IntentFilter(BluetoothIntentService.ACTION_DATA_AVAILABLE));
        registerReceiver(receiver, new IntentFilter(BluetoothIntentService.ACTION_GATT_CONNECTED));
        registerReceiver(receiver, new IntentFilter(BluetoothIntentService.ACTION_GATT_CONNECT_LOSE));

        model.initData();
    }

    private void initView() {
        StatusBarUtil.StatusBarLightMode(this);

        // 加载界面
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DeviceFragment());
        fragments.add(new MineFragment());
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager(), fragments);
        binding.mainPages.setAdapter(pagerAdapter);

        // 底部导航点击，根据选择状态修改图标
        binding.mainBottom.getMenu().findItem(R.id.menu_device).setIcon(R.drawable.ic_device_select);
        binding.mainBottom.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_device) {
                binding.mainPages.setCurrentItem(0);
                item.setIcon(R.drawable.ic_device_select);
                binding.mainBottom.getMenu().findItem(R.id.menu_mine).setIcon(R.drawable.ic_mine);
            }else if(item.getItemId() == R.id.menu_mine){
                binding.mainPages.setCurrentItem(1);
                item.setIcon(R.drawable.ic_mine_select);
                binding.mainBottom.getMenu().findItem(R.id.menu_device).setIcon(R.drawable.ic_device);
            }else {
                return item.getItemId() != R.id.menu_add;
            }
            return true;
        });

        // 底部导航再次点击
        binding.mainBottom.setOnNavigationItemReselectedListener(item -> {
            if (item.getItemId() == R.id.menu_device) {
                binding.mainPages.setCurrentItem(0);
            }else if(item.getItemId() == R.id.menu_mine){
                binding.mainPages.setCurrentItem(1);
            }
        });

        // 页面滑动
        binding.mainPages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private MenuItem item;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (item != null){
                    item.setChecked(false);
                    disSelectItem(item);
                }
                // 获取页码
                int index = position == 1 ? 2 : position;
                item = binding.mainBottom.getMenu().getItem(index).setChecked(true);
                selectItem(item);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            // 选择物品，改成实体图标
            private void selectItem(MenuItem i){
                if (i.getItemId() == R.id.menu_device) {
                    i.setIcon(R.drawable.ic_device_select);
                }else if(i.getItemId() == R.id.menu_mine){
                    i.setIcon(R.drawable.ic_mine_select);
                }
            }

            // 取消选择，改成框图标
            private void disSelectItem(MenuItem i){
                if (i.getItemId() == R.id.menu_device) {
                    i.setIcon(R.drawable.ic_device);
                }else if(i.getItemId() == R.id.menu_mine){
                    i.setIcon(R.drawable.ic_mine);
                }
            }
        });

        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.item_down);
        binding.mainFloat.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            Intent intent = new Intent(this, ScanActivity.class);
            this.startActivityForResult(intent, REQUEST_BLUETOOTH, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        String name, address;
        switch (requestCode) {
            case REQUEST_BLUETOOTH:
                if (resultCode != Activity.RESULT_OK) return;
                    name = data.getStringExtra(BluetoothIntentService.EXTRA_NAME);
                    address = data.getStringExtra(BluetoothIntentService.EXTRA_ADDRESS);
                    if(null == name) break;
                    Log.d(TAG, "exist? -> " + (model.getDeviceIndex(address) != -1));
                    if(model.getDeviceIndex(address) != -1) {
                        ToastUtil.showShort(this, "设备已存在");
                        break;
                    }
                    model.getDeviceData().setValue(new Device(0, name, address, true, DeviceType.NON, CalendarUtil.getDate()));
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(receiver);
        model.getDevices().clear();
        super.onDestroy();
    }
}