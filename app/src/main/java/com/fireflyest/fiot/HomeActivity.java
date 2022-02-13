package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.fireflyest.fiot.adapter.SettingItemAdapter;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.bean.Setting;
import com.fireflyest.fiot.data.SettingType;
import com.fireflyest.fiot.databinding.ActivityHomeBinding;
import com.fireflyest.fiot.net.HomeUpdateHttpRunnable;
import com.fireflyest.fiot.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HomeActivity";

    private ActivityHomeBinding binding;
    private WifiManager wifiManager;
    private SettingItemAdapter itemAdapter;

    private Home home;
    private boolean flag = false;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    // wifi列表扫描完成
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    for (ScanResult scanResult : scanResults) {
                        itemAdapter.getWifiListAdapter().add(scanResult.SSID);
                    }
                    break;
                case WifiManager.EXTRA_WIFI_STATE:
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
         wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
         this.initView();
    }

    private void initView(){
        StatusBarUtil.StatusBarLightMode(this);
        setSupportActionBar(binding.homeToolbar);

        //  获取要编辑的数据
        home = getIntent().getExtras().getParcelable("home");

        // 设置列表
        List<Setting> settingList = new ArrayList<>();
        settingList.add(new Setting("名称", SettingType.EDIT_TEXT, home.getName(), false, true));
        settingList.add(new Setting("位置", SettingType.TEXT, "", false, false));
        settingList.add(new Setting());
        settingList.add(new Setting("房间", SettingType.TEXT, home.getRooms(), false, false));
        settingList.add(new Setting("设备数量", SettingType.TEXT, "1", false, false));
        settingList.add(new Setting());
        settingList.add(new Setting("WiFi", SettingType.WIFI, home.getWifi(), false, true));
        settingList.add(new Setting("WiFi密码", SettingType.PASSWORD, home.getPassword(), false, true));
        settingList.add(new Setting());
        settingList.add(new Setting("个性化", SettingType.TEXT, "默认", false, false));

        itemAdapter = new SettingItemAdapter(this, settingList);
        binding.homeSetting.setAdapter(itemAdapter);
        // 修改
        itemAdapter.setEditListener(setting -> {
            flag = true;
            switch (setting.getName()){
                case "名称":
                    home.setName(setting.getStringValue());
                    new Thread(new HomeUpdateHttpRunnable(home.getId(), "name", home.getName())).start();
                    break;
                case "WiFi":
                    home.setWifi(setting.getStringValue());
                    new Thread(new HomeUpdateHttpRunnable(home.getId(), "wifi", home.getWifi())).start();
                    break;
                case "WiFi密码":
                    home.setPassword(setting.getStringValue());
                    new Thread(new HomeUpdateHttpRunnable(home.getId(), "password", home.getPassword())).start();
                    break;
                default:
            }
        });

        // 注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.EXTRA_WIFI_STATE);
        this.registerReceiver(receiver, intentFilter);

        // 扫描wifi
        if (!wifiManager.isWifiEnabled()) {
            //开启wifi
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    private void back(){
        Intent intent = new Intent();
        if (flag) {
            intent.putExtra("home", home);
            this.setResult(RESULT_OK, intent);
        }else {
            this.setResult(RESULT_CANCELED);
        }
        this.finish();
    }
}