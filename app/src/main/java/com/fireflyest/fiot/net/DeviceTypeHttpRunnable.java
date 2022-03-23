package com.fireflyest.fiot.net;

import androidx.lifecycle.MutableLiveData;

import com.fireflyest.fiot.BaseActivity;
import com.fireflyest.fiot.bean.Device;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DeviceTypeHttpRunnable implements Runnable{

    private final long id;
    private final int type;
    private final MutableLiveData<Device> data;

    public DeviceTypeHttpRunnable(long id, int type, MutableLiveData<Device> data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/account")
        HttpUrl url = HttpUrl.get("http://"+ BaseActivity.DEBUG_URL +":8080/deviceType")
                .newBuilder()
                .addQueryParameter("id", String.valueOf(id))
                .addQueryParameter("type", String.valueOf(type))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
