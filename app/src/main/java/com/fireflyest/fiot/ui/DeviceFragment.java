package com.fireflyest.fiot.ui;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fireflyest.fiot.BaseActivity;
import com.fireflyest.fiot.ControlActivity;
import com.fireflyest.fiot.HomeActivity;
import com.fireflyest.fiot.MainActivity;
import com.fireflyest.fiot.R;
import com.fireflyest.fiot.adapter.DeviceItemAdapter;
import com.fireflyest.fiot.bean.Account;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.data.DeviceType;
import com.fireflyest.fiot.databinding.FragmentDeviceBinding;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.net.DeviceCreateHttpRunnable;
import com.fireflyest.fiot.net.DeviceNicknameHttpRunnable;
import com.fireflyest.fiot.net.DeviceRemoveHttpRunnable;
import com.fireflyest.fiot.net.DeviceRoomHttpRunnable;
import com.fireflyest.fiot.net.DevicesHttpRunnable;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.service.MqttIntentService;
import com.fireflyest.fiot.util.AnimationUtils;
import com.fireflyest.fiot.util.DpOrPxUtil;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceFragment extends Fragment {

    private static final String TAG = "DeviceFragment";

    private FragmentDeviceBinding binding;
    private MainViewModel model;

    private DeviceItemAdapter deviceItemAdapter;

    private final List<String> rooms = new ArrayList<>();

    public DeviceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeviceBinding.inflate(inflater, container, false);
        model = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) activity.setSupportActionBar(binding.deviceToolbar);
        this.setHasOptionsMenu(true);

        // ?????????
        model.getTemperatureData().observe(this.getViewLifecycleOwner(), temperature -> {
            binding.mainTemperatureIcon.setVisibility(View.VISIBLE);
            binding.mainTemperatureValue.setVisibility(View.VISIBLE);
            binding.setTemperature(temperature);
        });
        model.getHumidityData().observe(this.getViewLifecycleOwner(), humidity -> {
            binding.mainHumidityIcon.setVisibility(View.VISIBLE);
            binding.mainHumidityValue.setVisibility(View.VISIBLE);
            binding.setHumidity(humidity);
        });
        // toolbar ??????
        model.getHomeData().observe(this.getViewLifecycleOwner(), home -> {
            binding.setHome(home);
            // ??????mqtt?????????
            // TODO: 2022/3/23 ??????
            Account account = model.getAccountData().getValue();
            if(home == null || home.getId() == 0 || account == null) return;
            MqttIntentService.createClient(getContext(), String.format("H%s", home.getId()), String.valueOf(home.getOwner()), "password");
            // ????????????
            new Thread(new DevicesHttpRunnable(account.getId(), home.getId(), model.getDeviceData())).start();
        });

        // ????????????
        rooms.add("??????");
        binding.roomSelect.setTextArray(rooms.toArray(new String[0]));

        // toolbar??????
        binding.deviceToolbar.setTitle("");
        binding.deviceAppbar.addOnOffsetChangedListener((v,offset) ->{
            float scrollRange = v.getTotalScrollRange();
            binding.deviceToolbar.setAlpha(1+offset/scrollRange*1.9F);

            binding.mainTemperatureIcon.setAlpha(1+offset/scrollRange*1.5F);
            binding.mainTemperatureValue.setAlpha(1+offset/scrollRange*1.5F);
            binding.mainHumidityIcon.setAlpha(1+offset/scrollRange*1.5F);
            binding.mainHumidityValue.setAlpha(1+offset/scrollRange*1.5F);

            binding.deviceSearch.setTranslationY(offset/scrollRange*200F);
            binding.deviceSearch.setAlpha(1+offset/scrollRange);

            binding.roomSelect.setAlpha(0-offset/scrollRange*1.5F);
        });

        //  ????????????
        binding.homeMore.setOnClickListener(v -> {
            View popView = LayoutInflater.from(getContext()).inflate(R.layout.pop_homes,  binding.deviceToolbar, false);
            final PopupWindow popupWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // ????????????
            TextView select = popView.findViewById(R.id.homes_select);
            Home selectHome = model.getHomeData().getValue();
            if (selectHome != null) {
                select.setText(selectHome.getName());
            }

            // ??????????????????
            TextView manager = popView.findViewById(R.id.homes_manager);
            manager.setOnClickListener(vm -> {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.putExtra(BaseActivity.EXTRA_HOME, model.getHomeData().getValue());
                activity.startActivityForResult(intent, MainActivity.REQUEST_HOME);
                popupWindow.dismiss();
            });

            popupWindow.setTouchable(true);
            popupWindow.showAsDropDown(v, 0, DpOrPxUtil.dip2px(v.getContext(), -20));
        });

        //  ????????????
        binding.roomMore.setOnClickListener(v -> {
            View popView = LayoutInflater.from(getContext()).inflate(R.layout.pop_rooms,  binding.deviceToolbar, false);
            final PopupWindow popupWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // ????????????
            // TODO: 2022/4/4
            TextView select = popView.findViewById(R.id.room_select);

            popupWindow.setTouchable(true);
            popupWindow.showAsDropDown(v, 0, DpOrPxUtil.dip2px(v.getContext(), -20));

        });

        // ????????????
        binding.deviceEmpty.setVisibility(View.VISIBLE);
        // ??????????????????
        model.getBtDeviceData().observe(this.getViewLifecycleOwner(), btDevice ->{
            if(binding.deviceEmpty.getVisibility() == View.VISIBLE) {
                AnimationUtils.hide(binding.deviceEmpty);
            }
            Device device = new Device(btDevice);
            device.setType(DeviceType.NON);
            deviceItemAdapter.addItem(device);
            // ??????????????????????????????
            Account account = model.getAccountData().getValue();
            Home home = model.getHomeData().getValue();
            if (account != null && home != null && home.getId() != 0) {
                Log.d(TAG, "?????????" + account.getId() + " ??????" + home.getId());
                new Thread(
                        new DeviceCreateHttpRunnable(account.getId(), home.getId(), device.getName(), device.getAddress(), device.getRoom(), device.getType(), model.getDeviceData())).start();
            }
        });
        // ????????????
        model.getDeviceData().observe(this.getViewLifecycleOwner(), device -> {
            if(binding.deviceEmpty.getVisibility() == View.VISIBLE) {
                AnimationUtils.hide(binding.deviceEmpty);
            }
            Log.d(TAG, String.format("???????????????%s", device.toString()));
            String address = device.getAddress();
            // ???????????????????????????
            int index = model.getDeviceIndex(address);
            if(-1 != index){
                model.getDevices().set(index, device);
                deviceItemAdapter.updateItem(index);
            }else {
                deviceItemAdapter.addItem(device);
                // ??????????????????
                int num = model.getDeviceNumData().getValue() + 1;
                model.getDeviceNumData().setValue(num);
                // mqtt??????
                MqttIntentService.subscribe(getContext(),  device.getAddress());
            }
            // ??????
            // TODO: 2022/4/4
            if(null != device.getRoom() && !rooms.contains(device.getRoom())){
                rooms.add(device.getRoom());
                binding.roomSelect.setTextArray(rooms.toArray(new String[0]));
            }
        });

        deviceItemAdapter = new DeviceItemAdapter(view.getContext(), model.getDevices());
        // ????????????
        deviceItemAdapter.setClickListener((device, background) -> {
            // ????????????????????????????????????????????????
            if(MainViewModel.getConnectState(device.getAddress()) != 0){
                // ???????????????????????????
                Intent intent = new Intent(activity, ControlActivity.class);
                intent.putExtra(BaseActivity.EXTRA_DEVICE, device);
                intent.putExtra(BaseActivity.EXTRA_HOME, model.getHomeData().getValue());
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this.requireActivity(), background, "device_background");
                this.startActivityForResult(intent, MainActivity.REQUEST_CONTROL, options.toBundle());
            }else if((device.getType() & DeviceType.REMOTE) != 0){
                // ????????????????????????
                MqttIntentService.send(getContext(), device.getAddress(), "?");
            }else {
                // ??????????????????
                BleIntentService.connect(getContext(), device.getAddress());
            }
        });

        //????????????
        deviceItemAdapter.setLongClickListener((device, background) -> {
            View popView = LayoutInflater.from(getContext()).inflate(R.layout.pop_device,  binding.deviceToolbar, false);
            final PopupWindow popupWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            popupWindow.setTouchable(true);
            popupWindow.showAsDropDown(background, 0, DpOrPxUtil.dip2px(background.getContext(), -80));

            // ??????
            TextView open = popView.findViewById(R.id.device_open);
            open.setOnClickListener(v -> {
                popupWindow.dismiss();

                Intent intent = new Intent(activity, ControlActivity.class);
                intent.putExtra(BaseActivity.EXTRA_DEVICE, device);
                intent.putExtra(BaseActivity.EXTRA_HOME, model.getHomeData().getValue());
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this.requireActivity(), background, "device_background");
                this.startActivityForResult(intent, MainActivity.REQUEST_CONTROL, options.toBundle());
            });
            // ?????????????????????
            TextView move = popView.findViewById(R.id.device_move);
            move.setOnClickListener(v ->{
                popupWindow.dismiss();
                // ??????
                AlertDialog moveDialog = new AlertDialog.Builder(activity)
                        .setMessage("?????????????????????")
                        .setTitle(R.string.move)
                        .setView(R.layout.diaog_edittext)
                        .setPositiveButton(R.string.done, (DialogInterface.OnClickListener) (dialog, id) -> {
                            EditText editText = ((AlertDialog)dialog).getWindow().getDecorView().findViewById(R.id.dialog_edittext);
                            String room = editText.getText().toString();
                            device.setRoom(room);
                            // ????????????
                            new Thread(new DeviceRoomHttpRunnable(device.getId(), room)).start();
                            // ????????????
                            model.getDeviceData().setValue(device);
                        })
                        .setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) (dialog, id) -> {
                            dialog.cancel();
                        })
                        .create();
                moveDialog.show();
            });
            // ??????
            TextView nickname = popView.findViewById(R.id.device_nickname);
            nickname.setOnClickListener(v->{
                popupWindow.dismiss();
                // ??????
                AlertDialog nicknameDialog = new AlertDialog.Builder(activity)
                        .setMessage("????????????")
                        .setTitle(R.string.nickname)
                        .setView(R.layout.diaog_edittext)
                        .setPositiveButton(R.string.done, (DialogInterface.OnClickListener) (dialog, id) -> {
                            EditText editText = ((AlertDialog)dialog).getWindow().getDecorView().findViewById(R.id.dialog_edittext);
                            String nick = editText.getText().toString();
                            device.setNickname(nick);
                            // ????????????
                            new Thread(new DeviceNicknameHttpRunnable(device.getId(), nick)).start();
                            // ????????????
                            model.getDeviceData().setValue(device);
                        })
                        .setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) (dialog, id) -> {
                            dialog.cancel();
                        })
                        .create();
                nicknameDialog.show();
            });
            // ??????
            TextView remove = popView.findViewById(R.id.device_remove);
            remove.setOnClickListener(v ->{
                popupWindow.dismiss();

                int index = model.getDeviceIndex(device.getAddress());
                BleIntentService.disconnect(getContext(), device.getAddress());
                if (index != -1) deviceItemAdapter.removeItem(index);
                // ??????????????????
                if((device.getType() & DeviceType.REMOTE) != 0){
                    new Thread(new DeviceRemoveHttpRunnable(device.getId())).start();
                }
            });
        });
        binding.deviceList.setAdapter(deviceItemAdapter);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

}