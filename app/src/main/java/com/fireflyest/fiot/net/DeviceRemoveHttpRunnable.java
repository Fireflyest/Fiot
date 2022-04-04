package com.fireflyest.fiot.net;

import com.fireflyest.fiot.BaseActivity;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DeviceRemoveHttpRunnable implements Runnable{

    private final long id;

    public DeviceRemoveHttpRunnable(long id) {
        this.id = id;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/")
        HttpUrl url = HttpUrl.get("http://"+ BaseActivity.DEBUG_URL +":8080/deleteDevice")
                .newBuilder()
                .addQueryParameter("id", String.valueOf(id))
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
