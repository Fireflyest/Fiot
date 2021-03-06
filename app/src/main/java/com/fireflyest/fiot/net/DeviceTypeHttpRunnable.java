package com.fireflyest.fiot.net;

import com.fireflyest.fiot.BaseActivity;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DeviceTypeHttpRunnable implements Runnable{

    private final long id;
    private final int type;

    public DeviceTypeHttpRunnable(long id, int type) {
        this.id = id;
        this.type = type;
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
