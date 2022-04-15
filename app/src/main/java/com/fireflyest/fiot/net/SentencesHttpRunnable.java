package com.fireflyest.fiot.net;

import androidx.lifecycle.MutableLiveData;

import com.fireflyest.fiot.BaseActivity;
import com.fireflyest.fiot.bean.Sentence;
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

public class SentencesHttpRunnable implements Runnable{

    private final long home;
    private final MutableLiveData<Sentence> data;

    public SentencesHttpRunnable(long home, MutableLiveData<Sentence> data) {
        this.home = home;
        this.data = data;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/account")
        HttpUrl url = HttpUrl.get("http://"+ BaseActivity.DEBUG_URL +":8080/sentences")
                .newBuilder()
                .addQueryParameter("home", String.valueOf(home))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body == null) return;

            Type type = new TypeToken<ArrayList<Sentence>>(){}.getType();
            List<Sentence> sentenceList = new Gson().fromJson(body.string(), type);
            for (Sentence sentence : sentenceList) {
                Thread.sleep(500);
                data.postValue(sentence);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
