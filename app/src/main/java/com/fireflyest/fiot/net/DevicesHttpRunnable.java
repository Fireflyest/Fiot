package com.fireflyest.fiot.net;

import androidx.lifecycle.MutableLiveData;

import com.fireflyest.fiot.BaseActivity;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Home;
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

public class DevicesHttpRunnable implements Runnable{

    private final long owner;
    private final long home;
    private final MutableLiveData<Device> data;

    public DevicesHttpRunnable(long owner, long home, MutableLiveData<Device> data) {
        this.owner = owner;
        this.home = home;
        this.data = data;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/account")
        HttpUrl url = HttpUrl.get("http://"+ BaseActivity.DEBUG_URL +":8080/devices")
                .newBuilder()
                .addQueryParameter("owner", String.valueOf(owner))
                .addQueryParameter("home", String.valueOf(home))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body == null) return;

            Type type = new TypeToken<ArrayList<Device>>(){}.getType();
            List<Device> deviceList = new Gson().fromJson(body.string(), type);
            for (Device device : deviceList) {
                Thread.sleep(500);
                data.postValue(device);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
