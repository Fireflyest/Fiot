package com.fireflyest.fiot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.fireflyest.fiot.util.PreferencesUtils;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesUtils.initSharedPreferences(this);

        this.startActivity(new Intent(this, MainActivity.class));

        this.finish();
    }
}