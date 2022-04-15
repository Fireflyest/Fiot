package com.fireflyest.fiot;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.fireflyest.fiot.adapter.ViewPagerAdapter;
import com.fireflyest.fiot.bean.BtDevice;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.databinding.ActivityMainBinding;
import com.fireflyest.fiot.dialog.VagueDialog;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.net.SentenceCreateHttpRunnable;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.service.MqttIntentService;
import com.fireflyest.fiot.ui.DeviceFragment;
import com.fireflyest.fiot.ui.MineFragment;
import com.fireflyest.fiot.util.StatusBarUtil;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    public static final int REQUEST_BLUETOOTH = 1;
    public static final int REQUEST_SENTENCE = 2;
    public static final int REQUEST_HOME = 3;
    public static final int REQUEST_CONTROL = 4;

    public static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private MainViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 绑定model
        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);

        this.initView();

        // 注册广播监听
        BroadcastReceiver receiver = model.getReceiver();
        super.registerBroadcastReceiver(receiver,
                BleIntentService.ACTION_DATA_AVAILABLE,
                BleIntentService.ACTION_GATT_CONNECTED,
                BleIntentService.ACTION_GATT_CONNECT_LOSE,
                MqttIntentService.ACTION_RECEIVER,
                MqttIntentService.ACTION_DEVICE_ONLINE
        );

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

        // 中间按钮
        binding.mainFloat.setOnClickListener(v ->{
            VagueDialog dialog = new VagueDialog(MainActivity.this, R.layout.dialog_main_add);
            dialog.setOnItemClickListener(id -> {
                if(id == R.id.device_add){
                    Intent intent = new Intent(this, ScanActivity.class);
                    this.startActivityForResult(intent, REQUEST_BLUETOOTH, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }else if(id == R.id.command_add){
                    Intent intent = new Intent(this, CommandActivity.class);
                    intent.putExtra(BaseActivity.EXTRA_HOME, model.getHomeData().getValue());
                    this.startActivityForResult(intent, REQUEST_SENTENCE, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }else if(id == R.id.dialog_add_background){
                    dialog.dismiss();
                }
            });
            dialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        String name, address;
        // 请求类型
        switch (requestCode) {
            case REQUEST_BLUETOOTH:
                if (resultCode != Activity.RESULT_OK) return;
                name = data.getStringExtra(BleIntentService.EXTRA_NAME);
                address = data.getStringExtra(BleIntentService.EXTRA_ADDRESS);
                if(null == name) break;
                Log.d(TAG, "exist? -> " + (model.getDeviceIndex(address) != -1));
                if(model.getDeviceIndex(address) != -1) {
                    ToastUtil.showShort(this, "设备已存在");
                    break;
                }
                BtDevice btDevice = new BtDevice(0, name, address);
                model.getBtDeviceData().setValue(btDevice);
                break;
            case REQUEST_HOME:
                if (resultCode != Activity.RESULT_OK) return;
                Home home = data.getParcelableExtra(BaseActivity.EXTRA_HOME);
                model.getHomeData().setValue(home);
                break;
            case REQUEST_CONTROL:
                if (resultCode != Activity.RESULT_OK) return;
                Device device = data.getParcelableExtra(BaseActivity.EXTRA_DEVICE);
                model.getDeviceData().setValue(device);
                break;
            case REQUEST_SENTENCE:
                if (resultCode != Activity.RESULT_OK) return;
                String sentenceName = data.getStringExtra(BaseActivity.EXTRA_NAME);
                Device controlDevice = data.getParcelableExtra(BaseActivity.EXTRA_DEVICE);
                String sentenceData = data.getStringExtra(MqttIntentService.EXTRA_DATA);
                if (controlDevice == null) {
                    break;
                }
                Home selectHome = model.getHomeData().getValue();
                if (selectHome != null) {
                    Log.d(TAG, String.format("home=%s name = %s", selectHome.getId(), sentenceName));
                    new Thread(
                            new SentenceCreateHttpRunnable(selectHome.getId(), sentenceName, controlDevice.getAddress(), sentenceData)).start();
                    ToastUtil.showShort(this, "添加成功");
                }
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        model.getDevices().clear();
        super.onDestroy();
    }

}