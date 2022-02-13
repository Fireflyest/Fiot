package com.fireflyest.fiot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

    // 以下是点击非输入框区域取消输入
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideInput(view, event)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
            return super.dispatchTouchEvent(event);
        }
        if (getWindow().superDispatchTouchEvent(event)) {
            return true;
        }
        return onTouchEvent(event);
    }

    private boolean isShouldHideInput(View view, MotionEvent event) {
        if (view instanceof EditText) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            view.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            // 点击的是输入框区域，保留点击EditText的事件
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        return false;
    }

}
