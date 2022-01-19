package com.fireflyest.fiot;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private final List<BroadcastReceiver>  receivers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        for (BroadcastReceiver receiver : receivers) {
            this.unregisterReceiver(receiver);
            this.receivers.remove(receiver);
        }
        super.onDestroy();
    }

    protected void registerBroadcastReceiver(BroadcastReceiver receiver, String... actions){
        IntentFilter intentFilter = new IntentFilter();
        for (String action : actions) {
            intentFilter.addAction(action);
        }
        this.registerReceiver(receiver, intentFilter);
        this.receivers.add(receiver);
    }

}
