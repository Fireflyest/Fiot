package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;

import com.fireflyest.fiot.bean.Token;
import com.fireflyest.fiot.databinding.ActivityLoginBinding;
import com.fireflyest.fiot.util.PasswordUtils;
import com.fireflyest.fiot.util.RSAUtils;
import com.fireflyest.fiot.util.StatusBarUtil;
import com.fireflyest.fiot.util.ToastUtil;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    public static final int TOKEN_EMPTY = 0;
    public static final int TOKEN_TIMEOUT = 1;
    public static final int TOKEN_ERROR = 2;
    public static final int TOKEN_SUCCESS = 3;

    private final Handler handler = new Handler(msg -> {
        boolean register = binding.loginSwitch.getSelect() == 1;
        switch (msg.what){
            case TOKEN_EMPTY:
                ToastUtil.showShort(LoginActivity.this, "账户不存在");
                break;
            case TOKEN_TIMEOUT:
                ToastUtil.showShort(LoginActivity.this, "登录超时，请检查网络");
                break;
            case TOKEN_ERROR:
                ToastUtil.showShort(LoginActivity.this, register ? "账户已存在":"账户或密码错误");
                break;
            case TOKEN_SUCCESS:
                ToastUtil.showShort(LoginActivity.this, register ? "成功注册":"登录成功");
                Token token = (Token) msg.obj;
                token.setValue(RSAUtils.publicDecrypt(token.getValue(), RSAUtils.PUBLIC_KEY));
                back(token);
                break;
            default:
        }
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        this.iniView();
    }

    private void iniView(){
        StatusBarUtil.StatusBarLightMode(this);

        Toolbar toolbar = binding.loginToolbar;
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        binding.loginSwitch.setTextArray("登录", "注册");
        binding.loginSwitch.setChangeListener(select ->{
            if(select == 0){
                binding.loginMotion.transitionToStart();
                binding.loginConfirm.setEnabled(false);
                binding.loginForget.setEnabled(true);
                binding.loginAccount.setHint(R.string.login_account);
            }else {
                binding.loginMotion.transitionToEnd();
                binding.loginConfirm.setEnabled(true);
                binding.loginForget.setEnabled(false);
                binding.loginAccount.setHint(R.string.login_username);
            }
        });

        binding.loginAccount.setOnClickListener(v -> v.setBackgroundResource(R.drawable.round_background));
        binding.loginPassword.setOnClickListener(v -> v.setBackgroundResource(R.drawable.round_background));
        binding.loginConfirm.setOnClickListener(v -> v.setBackgroundResource(R.drawable.round_background));

        binding.loginButton.setOnClickListener(v -> {
            String account = binding.loginAccount.getText() == null ? "":binding.loginAccount.getText().toString();
            String password = binding.loginPassword.getText() == null ? "":binding.loginPassword.getText().toString();
            String confirm = binding.loginConfirm.getText() == null ? "":binding.loginConfirm.getText().toString();
            if(binding.loginSwitch.getSelect() == 0){
                if(TextUtils.isEmpty(account)){
                    binding.loginAccount.setBackgroundResource(R.drawable.round_background_warn);
                    return;
                }else if(TextUtils.isEmpty(password)){
                    binding.loginPassword.setBackgroundResource(R.drawable.round_background_warn);
                    return;
                }
                new Thread(
                        new HttpRunnable(account, RSAUtils.publicEncrypt(password, RSAUtils.PUBLIC_KEY), handler, false)
                ).start();
            }else {
                if(TextUtils.isEmpty(account)){
                    binding.loginAccount.setBackgroundResource(R.drawable.round_background_warn);
                    return;
                }else if(TextUtils.isEmpty(password)){
                    binding.loginPassword.setBackgroundResource(R.drawable.round_background_warn);
                    return;
                }else if(TextUtils.isEmpty(confirm)){
                    binding.loginConfirm.setBackgroundResource(R.drawable.round_background_warn);
                    return;
                }
                if(!password.equals(confirm)){
                    binding.loginConfirm.setBackgroundResource(R.drawable.round_background_warn);
                    ToastUtil.showShort(this, "请输入相同的密码");
                    return;
                }
                String strong = PasswordUtils.isStrongPassword(password);
                if(!"".equals(strong)){
                    binding.loginConfirm.setBackgroundResource(R.drawable.round_background_warn);
                    ToastUtil.showShort(this, strong);
                    return;
                }
                new Thread(
                        new HttpRunnable(account, RSAUtils.publicEncrypt(password, RSAUtils.PUBLIC_KEY), handler, true)
                ).start();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) back(null);
        return true;
    }

    @Override
    public void onBackPressed() {
        this.back(null);
        super.onBackPressed();
    }

    private void back(Token token){
        Intent intent = new Intent();
        if (token == null) {
            this.setResult(RESULT_CANCELED);
        }else {
            intent.putExtra("account", token.getId());
            intent.putExtra("token", token.getValue());
            this.setResult(RESULT_OK, intent);
        }
        this.finish();
    }

    static class HttpRunnable implements Runnable {

        private final String account;
        private final String password;
        private final Handler handler;
        private final boolean register;

        public HttpRunnable(String account, String password, Handler handler, boolean register) {
            this.account = account;
            this.password = password;
            this.handler = handler;
            this.register = register;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
            String target = register ? "register" : "login";
//            HttpUrl url = HttpUrl.get("http://www.ft0825.top/" + target)
            HttpUrl url = HttpUrl.get("http://192.168.2.115:8080/" + target)
                    .newBuilder()
                    .addQueryParameter("account", account)
                    .addQueryParameter("password", password)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                ResponseBody body = response.body();
                if (body == null) return;
                Token token = new Gson().fromJson(body.string(), Token.class);
                if (token == null) {
                    handler.obtainMessage(TOKEN_EMPTY).sendToTarget();
                    return;
                }
                if("".equals(token.getValue())){
                    handler.obtainMessage(TOKEN_ERROR).sendToTarget();
                    return;
                }
                handler.obtainMessage(TOKEN_SUCCESS, token).sendToTarget();
            } catch (IOException e) {
                handler.obtainMessage(TOKEN_TIMEOUT).sendToTarget();
                e.printStackTrace();
            }
        }

    }

}