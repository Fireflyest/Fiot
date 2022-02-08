package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.fireflyest.fiot.adapter.SettingItemAdapter;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.bean.Setting;
import com.fireflyest.fiot.bean.Token;
import com.fireflyest.fiot.data.SettingType;
import com.fireflyest.fiot.databinding.ActivityHomeBinding;
import com.fireflyest.fiot.net.HomeUpdateHttpRunnable;
import com.fireflyest.fiot.util.StatusBarUtil;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    private Home home;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

         this.initView();
    }

    private void initView(){
        StatusBarUtil.StatusBarLightMode(this);

        setSupportActionBar(binding.homeToolbar);

        //  获取要编辑的数据
        home = getIntent().getExtras().getParcelable("home");

        // 设置列表
        List<Setting> settingList = new ArrayList<>();
        settingList.add(new Setting("名称", SettingType.TEXT, home.getName(), false, true));
        settingList.add(new Setting("位置", SettingType.TEXT, "", false, false));
        settingList.add(new Setting());
        settingList.add(new Setting("房间", SettingType.TEXT, home.getRooms(), false, false));
        settingList.add(new Setting("设备数量", SettingType.TEXT, "1", false, false));
        settingList.add(new Setting());
        settingList.add(new Setting("WiFi", SettingType.TEXT, home.getWifi(), false, true));
        settingList.add(new Setting("WiFi密码", SettingType.TEXT, home.getWifi(), false, true));
        settingList.add(new Setting());
        settingList.add(new Setting("个性化", SettingType.TEXT, "默认", false, false));

        SettingItemAdapter itemAdapter = new SettingItemAdapter(this, settingList);
        binding.homeSetting.setAdapter(itemAdapter);
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