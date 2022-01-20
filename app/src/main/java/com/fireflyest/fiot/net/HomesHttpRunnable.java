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

public class HomesHttpRunnable implements Runnable{

    private final long owner;
    private final MutableLiveData<List<Home>> homes;

    public HomesHttpRunnable(long owner, MutableLiveData<List<Home>> homes){
        this.owner = owner;
        this.homes = homes;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/account")
        HttpUrl url = HttpUrl.get("http://192.168.2.115:8080/homes")
                .newBuilder()
                .addQueryParameter("owner", String.valueOf(owner))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body == null) return;

            Type type = new TypeToken<ArrayList<Home>>(){}.getType();
            List<Home> hs = new Gson().fromJson(body.string(), type);
            homes.postValue(hs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
