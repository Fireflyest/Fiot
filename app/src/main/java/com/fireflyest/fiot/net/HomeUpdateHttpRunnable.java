package com.fireflyest.fiot.net;

import androidx.lifecycle.MutableLiveData;

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

public class HomeUpdateHttpRunnable implements Runnable{

    private final long id;
    private final String key;
    private final String value;

    public HomeUpdateHttpRunnable(long id, String key, String value){
        this.id = id;
        this.key = key;
        this.value = value;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/account")
        HttpUrl url = HttpUrl.get("http://192.168.2.115:8080/updateHome")
                .newBuilder()
                .addQueryParameter("id", String.valueOf(id))
                .addQueryParameter("key", key)
                .addQueryParameter("value", value)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body == null) return;

//            Home h = new Gson().fromJson(body.string(), Home.class);
//            home.postValue(h);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
