package com.fireflyest.fiot.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireflyest.fiot.ControlActivity;
import com.fireflyest.fiot.adapter.CommandItemAdapter;
import com.fireflyest.fiot.bean.Command;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.data.CommandType;
import com.fireflyest.fiot.databinding.FragmentControlNormalBinding;
import com.fireflyest.fiot.model.ControlViewModel;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.util.AnimationUtils;
import com.fireflyest.fiot.util.CalendarUtil;
import com.fireflyest.fiot.util.ConvertUtil;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.regex.Pattern;

public class ControlNormalFragment extends Fragment {

    private static final String TAG = ControlNormalFragment.class.getSimpleName();

    private static final Pattern patten = Pattern.compile("^[a-fA-F0-9]+$");

    private ControlViewModel model;

    private FragmentControlNormalBinding binding;

    private CommandItemAdapter commandItemAdapter;

    private Device device;

    public ControlNormalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            device = savedInstanceState.getParcelable(ControlActivity.EXTRA_DEVICE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentControlNormalBinding.inflate(inflater, container, false);
        model = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(ControlViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        device = model.getDeviceData().getValue();
        if (device == null) return;

        // 十六进制收发
        model.getHexData().observe(getViewLifecycleOwner(), hex -> binding.setHex(hex));
        model.getHexData().setValue(true);
        binding.commandHex.setOnClickListener(v -> {
            Boolean hex = model.getHexData().getValue();
            if (hex == null) hex = false;
            model.getHexData().setValue(!hex);
            AnimationUtils.click(v);
        });

        binding.commandEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (TextUtils.isEmpty(text)) {
                    binding.controlCommandBox.transitionToStart();
                    model.setText("");
                    binding.commandEdit.setError(null);
                } else {
                    binding.controlCommandBox.transitionToEnd();
                    model.setText(text);
                    Boolean hex = model.getHexData().getValue();
                    if (hex == null) hex = false;
                    if (hex && !patten.matcher(text).matches()){
                        binding.commandEdit.setError("文本非十六进制字符串");
                    }
                }
            }
        });
        binding.commandSend.setOnClickListener(v -> {
            AnimationUtils.click(v);
            String text = model.getText();
            if (TextUtils.isEmpty(device.getService()) || TextUtils.isEmpty(device.getService())){
                ToastUtil.showShort(getContext(), "未配置蓝牙属性");
                return;
            }
            // 新建指令
            Command command = new Command(0, device.getAddress(), true, false, CalendarUtil.getDate(), text, CommandType.SEND);
            commandItemAdapter.addItem(command);
            Boolean hex = model.getHexData().getValue();
            if (hex == null) hex = false;
            // 发送指令
            BleIntentService.write(getContext(), device.getAddress(), device.getService(), device.getCharacteristic(),  hex ? ConvertUtil.getHexBytes(text) : text.getBytes());
            binding.commandEdit.setText("");
        });
        binding.commandMore.setOnClickListener(v -> {
            AnimationUtils.click(v);
            ToastUtil.showShort(getContext(), "more");
        });

        model.getCommandData().observe(getViewLifecycleOwner(), command -> {

        });

        commandItemAdapter = new CommandItemAdapter(getContext(), model.getCommands());
        binding.controlCommandList.setAdapter(commandItemAdapter);

        // 发送结果
        model.getCommandData().observe(getViewLifecycleOwner(), command -> commandItemAdapter.updateItem(model.getCommands().indexOf(command)));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(ControlActivity.EXTRA_DEVICE, device);
        super.onSaveInstanceState(outState);
    }


}