package com.fireflyest.fiot.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.fireflyest.fiot.ControlActivity;
import com.fireflyest.fiot.adapter.CharacteristicItemAdapter;
import com.fireflyest.fiot.adapter.CommandItemAdapter;
import com.fireflyest.fiot.adapter.ServiceItemAdapter;
import com.fireflyest.fiot.bean.BtDevice;
import com.fireflyest.fiot.bean.Characteristic;
import com.fireflyest.fiot.bean.Command;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Service;
import com.fireflyest.fiot.data.CommandType;
import com.fireflyest.fiot.databinding.FragmentControlNormalBinding;
import com.fireflyest.fiot.model.ControlViewModel;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.util.AnimationUtils;
import com.fireflyest.fiot.util.CalendarUtil;
import com.fireflyest.fiot.util.ConvertUtil;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ControlNormalFragment extends Fragment {

    private static final String TAG = ControlNormalFragment.class.getSimpleName();

    private static final Pattern patten = Pattern.compile("^[a-fA-F0-9]+$");

    private ControlViewModel model;

    private FragmentControlNormalBinding binding;

    private CommandItemAdapter commandItemAdapter;
    private ServiceItemAdapter serviceItemAdapter;
    private CharacteristicItemAdapter characteristicItemAdapter;

    private final List<Service> services = new ArrayList<>();
    private final List<Characteristic> characteristics = new ArrayList<>();

    private Device device;

    private String serviceUUID = null;
    private String characteristicUUID = null;

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

        // ?????????????????? ????????????
        model.getHexData().observe(getViewLifecycleOwner(), hex -> binding.setHex(hex));
        model.getHexData().setValue(false);
        binding.commandHex.setOnClickListener(v -> {
            boolean hex = isHex();
            model.getHexData().setValue(!hex);
            AnimationUtils.click(v);
        });

        // ?????????????????????
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
                    return;
                }
                binding.controlCommandBox.transitionToEnd();
                model.setText(text);
                boolean hex = isHex();
                if (hex && !patten.matcher(text).matches()){
                    binding.commandEdit.setError("??????????????????????????????");
                }
            }
        });

        // ??????????????????
        binding.commandSend.setOnClickListener(v -> {
            AnimationUtils.click(v);
            String text = model.getText();
            // ???????????????
            if (TextUtils.isEmpty(serviceUUID) || TextUtils.isEmpty(characteristicUUID)){
                ToastUtil.showShort(getContext(), "?????????????????????");
                return;
            }
            // ?????????????????? ????????????????????????
            boolean hex = isHex();
            if (hex && (!patten.matcher(text).matches() || text.length()%2 != 0)){
                ToastUtil.showShort(getContext(), "??????????????????");
                return;
            }
            // ???????????? ????????????
            Command command = new Command(0, device.getAddress(), true, false, CalendarUtil.getDate(), text, CommandType.SEND);
            commandItemAdapter.addItem(command);
            // ???????????????
//            if(!hex) text = text+???;???;
            // ????????????
            BleIntentService.write(getContext(), device.getAddress(), serviceUUID, characteristicUUID,  hex ? ConvertUtil.getHexBytes(text) : text.getBytes());
            binding.commandEdit.setText("");
        });

        // ????????????
        binding.commandMore.setOnClickListener(v -> {
            // TODO: 2022/3/13
        });

        // ????????????
        model.getCommandData().observe(getViewLifecycleOwner(), command -> {
            Log.d(TAG, "???????????? -> "+ command.toString());
            int index = model.getCommands().indexOf(command);
            if (index == -1){
                commandItemAdapter.addItem(command);
                binding.controlCommandList.smoothScrollToPosition(commandItemAdapter.getItemCount());
            }else {
                commandItemAdapter.updateItem(index);
            }
        });

        // ??????????????????
        commandItemAdapter = new CommandItemAdapter(getContext(), model.getCommands());
        binding.controlCommandList.setAdapter(commandItemAdapter);

        // ???????????????????????????
        serviceItemAdapter = new ServiceItemAdapter(getContext(), services);
        characteristicItemAdapter = new CharacteristicItemAdapter(getContext(), characteristics);
        binding.controlService.setAdapter(serviceItemAdapter);
        binding.controlCharacteristic.setAdapter(characteristicItemAdapter);

        // ??????????????????
        model.getServicesData().observe(getViewLifecycleOwner(), s ->{
            if (s != null) {
                services.clear();
                // ?????????????????????
                services.add(new Service("??????", "????????????"));
                services.addAll(s);
                Log.d(TAG, "ServicesData ->" + services.toString());
                serviceItemAdapter.notifyDataSetChanged();
            }
        });

        // ??????????????????
        model.getCharacteristicsData().observe(getViewLifecycleOwner(), c ->{
            if (c != null) {
                characteristics.clear();
                // ?????????????????????
                characteristics.add(new Characteristic("??????", "????????????"));
                characteristics.addAll(c);
                Log.d(TAG, "CharacteristicsData ->" + c.toString());
                characteristicItemAdapter.notifyDataSetChanged();
            }
        });

        // ??????????????????????????????
        model.initServiceData(device.getAddress());

        // ????????????
        binding.controlService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                Service service = services.get(position);
                String uuid = service.getUuid();
                model.selectService(uuid);
                Log.d(TAG, "SelectedService -> " + uuid);
                serviceUUID = uuid;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        binding.controlCharacteristic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                Characteristic characteristic = characteristics.get(position);
                String uuid = characteristic.getUuid();
                Log.d(TAG, "SelectedCharacteristic -> " + uuid);
                characteristicUUID = uuid;

                // 3????????????????????????
                hideService(3000);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        model.getEditData().observe(getViewLifecycleOwner(), display -> {
            if(display){
                AnimationUtils.show(binding.controlService);
                AnimationUtils.show(binding.controlCharacteristic);
                hideService(10000);
            }else {
                AnimationUtils.hide(binding.controlService);
                AnimationUtils.hide(binding.controlCharacteristic);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(ControlActivity.EXTRA_DEVICE, device);
        super.onSaveInstanceState(outState);
    }

    private boolean isHex(){
        Boolean hex = model.getHexData().getValue();
        if (hex == null) hex = false;
        return hex;
    }

    private void hideService(final long delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                model.getEditData().postValue(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


}