package com.fireflyest.fiot.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fireflyest.fiot.LoginActivity;
import com.fireflyest.fiot.R;
import com.fireflyest.fiot.adapter.LineItemAdapter;
import com.fireflyest.fiot.bean.Account;
import com.fireflyest.fiot.bean.Line;
import com.fireflyest.fiot.databinding.FragmentMineBinding;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.net.HomeCreateHttpRunnable;
import com.fireflyest.fiot.net.HomesHttpRunnable;
import com.fireflyest.fiot.util.PreferencesUtils;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class MineFragment extends Fragment {

    private static final String TAG = "MineFragment";
    private static final int RESULT_LOGIN = 0;

    private FragmentMineBinding binding;

    private MainViewModel model;

    public MineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(inflater, container, false);
        model = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) activity.setSupportActionBar(binding.mineToolbar);
        binding.mineToolbar.setTitle(R.string.mine);
        this.setHasOptionsMenu(true);

        // 导航列表
        List<Line> lines = new ArrayList<>();
        lines.add(new Line("光线", 0));
        lines.add(new Line("安防", 0));
        lines.add(new Line("音视", 0));
        lines.add(new Line("环境", 0));
        LineItemAdapter lineItemAdapter = new LineItemAdapter(view.getContext(), lines);
        lineItemAdapter.setClickListener(line -> {
            ToastUtil.showShort(getContext(), line.getName());
        });
        binding.navigationList.setAdapter(lineItemAdapter);

        // 头像点击
        binding.mineAvator.setOnClickListener(v -> {
            Account account = model.getAccountData().getValue();
            if(account != null && this.getString(R.string.login_default).equals(account.getName())){
                this.startActivityForResult(new Intent(view.getContext(), LoginActivity.class), RESULT_LOGIN);
            }else {
                // todo 已登录状态
                // this.startActivity(new Intent(view.getContext(), ProfileActivity.class));
            }
        });

        // 家列表更新
        model.getHomesData().observe(this.getViewLifecycleOwner(), homes -> {
            Account account = model.getAccountData().getValue();
            if (account == null) return;
            if(homes == null || homes.size() == 0){
                new Thread(
                        new HomeCreateHttpRunnable(account.getId(), account.getName() + "的家", model.getHomeData()) ).start();
            }else {
                model.getHomeData().setValue(homes.get(0));
            }
        });

        // 账户状态监控
        // todo 登录成功
        model.getAccountData().observe(this.getViewLifecycleOwner(), account -> {
            binding.setAccount(account);
            // 初始化家
            new Thread(new HomesHttpRunnable(account.getId(), model.getHomesData())).start();
            // 更新设备列表

        });

        // 初始化账户
        binding.setDeviceInfo("0个智能设备");

        // 自动登录
        long account = PreferencesUtils.getLongData("login_account");
        String token = PreferencesUtils.getStringData("login_token");
        if(0 != account && token != null){
            model.updateAccount(account, token);
            Log.i(TAG, "account = " + account);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case RESULT_LOGIN:
                    if (data == null) return;
                    long account = data.getLongExtra("account", 0);
                    String token = data.getStringExtra("token");
                    PreferencesUtils.putData("login_account", account);
                    PreferencesUtils.putData("login_token", token);
                    model.updateAccount(account, token);
                    break;
                default:
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }
}