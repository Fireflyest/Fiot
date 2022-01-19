package com.fireflyest.fiot.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireflyest.fiot.LoginActivity;
import com.fireflyest.fiot.R;
import com.fireflyest.fiot.databinding.FragmentMineBinding;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.util.PreferencesUtils;

public class MineFragment extends Fragment {

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

        binding.mineAvator.setOnClickListener(v -> {
            if(model.getDataAccount().getValue() == null){
                this.startActivityForResult(new Intent(view.getContext(), LoginActivity.class), RESULT_LOGIN);
            }else {
//                    this.startActivity(new Intent(view.getContext(), ProfileActivity.class));
            }
        });

        model.getDataAccount().observe(this.getViewLifecycleOwner(), account -> binding.setAccount(account));

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