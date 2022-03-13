package com.fireflyest.fiot.net;

import androidx.lifecycle.MutableLiveData;

import com.fireflyest.fiot.BaseActivity;
import com.fireflyest.fiot.bean.Account;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AccountHttpRunnable implements Runnable{

    private final long account;
    private final String token;
    private final MutableLiveData<Account> data;

    public AccountHttpRunnable(long account, String token, MutableLiveData<Account> data) {
        this.account = account;
        this.token = token;
        this.data = data;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        HttpUrl url = HttpUrl.get("http://www.ft0825.top/account")
        HttpUrl url = HttpUrl.get("http://"+ BaseActivity.DEBUG_URL +":8080/account")
                .newBuilder()
                .addQueryParameter("account", String.valueOf(account))
                .addQueryParameter("token", token)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body == null) return;
            Account account = new Gson().fromJson(body.string(), Account.class);
            if (account != null) data.postValue(account);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
