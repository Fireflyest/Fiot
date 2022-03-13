package com.fireflyest.fiot.net;

import androidx.lifecycle.MutableLiveData;

import com.fireflyest.fiot.BaseActivity;
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

public class HomeCreateHttpRunnable implements Runnable{

    private final long owner;
    private final String name;
    private final MutableLiveData<Home> home;

    public HomeCreateHttpRunnable(long owner, String name, MutableLiveData<Home> home){
        this.owner = owner;
        this.name = name;
        this.home = home;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/createHome")
        HttpUrl url = HttpUrl.get("http://"+ BaseActivity.DEBUG_URL +":8080/createHome")
                .newBuilder()
                .addQueryParameter("owner", String.valueOf(owner))
                .addQueryParameter("name", name)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body == null) return;
            Home h = new Gson().fromJson(body.string(), Home.class);
            home.postValue(h);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
