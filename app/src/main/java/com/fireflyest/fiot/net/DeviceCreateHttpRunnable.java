package com.fireflyest.fiot.net;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.fireflyest.fiot.BaseActivity;
import com.fireflyest.fiot.bean.Account;
import com.fireflyest.fiot.bean.Device;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DeviceCreateHttpRunnable implements Runnable{
    
    private final long owner;
    private final String name;
    private final String address;
    private final String room;
    private final int type;
    private final MutableLiveData<Device> data;

    public DeviceCreateHttpRunnable(long owner, String name, String address, String room, int type, MutableLiveData<Device> data) {
        this.owner = owner;
        this.name = name;
        this.address = address;
        this.room = room;
        this.type = type;
        this.data = data;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/account")
        HttpUrl url = HttpUrl.get("http://"+ BaseActivity.DEBUG_URL +":8080/createDevice")
                .newBuilder()
                .addQueryParameter("owner", String.valueOf(owner))
                .addQueryParameter("name", name)
                .addQueryParameter("address", address)
                .addQueryParameter("room", room)
                .addQueryParameter("type", String.valueOf(type))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body == null) return;
            Device device = new Gson().fromJson(body.string(), Device.class);
            if (device != null) data.postValue(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
