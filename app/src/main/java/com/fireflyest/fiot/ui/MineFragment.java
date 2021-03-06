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

import com.fireflyest.fiot.BaseActivity;
import com.fireflyest.fiot.LoginActivity;
import com.fireflyest.fiot.R;
import com.fireflyest.fiot.SceneActivity;
import com.fireflyest.fiot.adapter.LineItemAdapter;
import com.fireflyest.fiot.bean.Account;
import com.fireflyest.fiot.bean.Line;
import com.fireflyest.fiot.databinding.FragmentMineBinding;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.net.DevicesHttpRunnable;
import com.fireflyest.fiot.net.HomeCreateHttpRunnable;
import com.fireflyest.fiot.net.HomesHttpRunnable;
import com.fireflyest.fiot.service.MqttIntentService;
import com.fireflyest.fiot.util.PreferencesUtils;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class MineFragment extends Fragment {

    private static final String TAG = "MineFragment";
    private static final int RESULT_LOGIN = 0;
    private static final int RESULT_SCENE = 0;

    int tryAmount = 0;

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

        // ????????????
        List<Line> lines = new ArrayList<>();
        lines.add(new Line("??????", 0));
        lines.add(new Line("??????", 0));
        lines.add(new Line("??????", 0));
        lines.add(new Line("??????", 0));
        LineItemAdapter lineItemAdapter = new LineItemAdapter(view.getContext(), lines);
        lineItemAdapter.setClickListener(line -> {
            ToastUtil.showShort(getContext(), line.getName());
        });
        binding.navigationList.setAdapter(lineItemAdapter);

        // ????????????
        binding.mineAvator.setOnClickListener(v -> {
            Account account = model.getAccountData().getValue();
            // ??????????????????
            if(account != null && this.getString(R.string.login_default).equals(account.getName())){
                this.startActivityForResult(new Intent(view.getContext(), LoginActivity.class), RESULT_LOGIN);
            }else {
                // todo ???????????????
                // this.startActivity(new Intent(view.getContext(), ProfileActivity.class));
            }
        });

        // ???????????????
        model.getHomesData().observe(this.getViewLifecycleOwner(), homes -> {
            Account account = model.getAccountData().getValue();
            if (account == null) return;
            // ??????????????????
            if(homes == null || homes.size() == 0){
                // ????????????????????????
                if(tryAmount <= 2){
                    new Thread(new HomesHttpRunnable(account.getId(), model.getHomesData())).start();
                }else {
                    // ?????????
                    new Thread(
                            new HomeCreateHttpRunnable(account.getId(), account.getName() + "??????", model.getHomeData()) ).start();
                }
                tryAmount ++;
            }else {
                model.getHomeData().setValue(homes.get(0));
            }
        });

        // ??????????????????
        // todo ????????????
        model.getAccountData().observe(this.getViewLifecycleOwner(), account -> {
            binding.setAccount(account);
            // ??????????????????
            new Thread(new HomesHttpRunnable(account.getId(), model.getHomesData())).start();
            // ?????????????????????

        });

        // ???????????????
        model.getDeviceNumData().observe(getViewLifecycleOwner(), num-> binding.setDeviceInfo(String.format("%s???????????????", num)));
        model.getSceneNumData().observe(getViewLifecycleOwner(), num-> binding.setSceneInfo(String.format("%s?????????", num)));

        binding.mineScene.setOnClickListener(v ->{
            Intent intent = new Intent(activity, SceneActivity.class);
            intent.putExtra(BaseActivity.EXTRA_HOME, model.getHomeData().getValue());
            startActivityForResult(intent, RESULT_SCENE);
        });

        // ????????????
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        MqttIntentService.closeClient(getContext());
    }
}