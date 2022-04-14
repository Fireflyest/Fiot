package com.fireflyest.fiot.net;

import androidx.lifecycle.MutableLiveData;

import com.fireflyest.fiot.BaseActivity;
import com.fireflyest.fiot.bean.Device;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SentenceCreateHttpRunnable implements Runnable{

    private final long home;
    private final String name;
    private final String target;
    private final String data;

    public SentenceCreateHttpRunnable(long home, String name, String target, String data) {
        this.home = home;
        this.name = name;
        this.target = target;
        this.data = data;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/account")
        HttpUrl url = HttpUrl.get("http://"+ BaseActivity.DEBUG_URL +":8080/createSentence")
                .newBuilder()
                .addQueryParameter("home", String.valueOf(home))
                .addQueryParameter("name", name)
                .addQueryParameter("target", target)
                .addQueryParameter("data", data)
                .addQueryParameter("home", String.valueOf(home))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            client.newCall(request).execute();
//            Response response =
//            ResponseBody body = response.body();
//            if (body == null) return;
//            Device device = new Gson().fromJson(body.string(), Device.class);
//            if (device != null) data.postValue(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
