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

public class DeviceListHttpRunnable implements Runnable{

    private final long owner;
    private final long home;
    private final MutableLiveData<List<Device>> data;

    public DeviceListHttpRunnable(long owner, long home, MutableLiveData<List<Device>> data) {
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
            data.postValue(deviceList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
