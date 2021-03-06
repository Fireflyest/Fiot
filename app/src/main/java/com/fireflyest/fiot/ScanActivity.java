package com.fireflyest.fiot;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.TextView;

import com.fireflyest.fiot.adapter.ScanedItemAdapter;
import com.fireflyest.fiot.anim.FallItemAnimator;
import com.fireflyest.fiot.bean.Scaned;
import com.fireflyest.fiot.databinding.ActivityScanBinding;
import com.fireflyest.fiot.model.ScanViewModel;
import com.fireflyest.fiot.service.BleIntentService;
import com.fireflyest.fiot.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends BaseActivity {

    public static final String TAG = ScanActivity.class.getSimpleName();

    private ActivityScanBinding binding;
    private ScanViewModel model;

    private BroadcastReceiver receiver;

    private ScanedItemAdapter scanedItemAdapter;
    private BluetoothAdapter bluetoothAdapter;

    public static final int ENABLE_BLUETOOTH = 0;

    private final Handler handler = new Handler(msg -> {
        switch (msg.what){
            case ENABLE_BLUETOOTH:
                break;
            default:
        }
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan);
        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ScanViewModel.class);

        this.intiView();

        model.initData();
    }

    private void intiView() {
        StatusBarUtil.StatusBarLightMode(this);

        setSupportActionBar(binding.scanToolbar);
        binding.scanToolbar.setNavigationOnClickListener(v -> back(null, null));

        // ???????????????
        List<Scaned> scaneds = new ArrayList<>();
        scanedItemAdapter = new ScanedItemAdapter(this, scaneds, (name,address) -> {
            // ??????????????????
            BleIntentService.connect(this, address);
            back(name, address);
        });
        binding.scanList.setAdapter(scanedItemAdapter);
        binding.scanList.setItemAnimator(new FallItemAnimator());
        model.getScanedData().observe(this, scaned -> scanedItemAdapter.addItem(scaned));

        model.getDiscoveryData().observe(this, discovery -> binding.setDiscovery(discovery));
        model.getAmountData().observe(this, amount-> binding.scanAmount.setText(amount.toString()));
        binding.scanAmount.setFactory(() -> {
            TextView textView = new TextView(this);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 72);
            textView.getPaint().setFakeBoldText(true);
            textView.setTextColor(ContextCompat.getColor(this, R.color.blue_dark));
            return textView;
        });

        binding.scanRefresh.setOnRefreshListener(() ->{
            startDiscovery();
            binding.scanRefresh.setRefreshing(false);
        });

        // ??????????????????
        receiver = model.getReceiver();
        this.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        this.registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));



        //????????????????????????
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            model.getBtStateData().setValue(false);
            // TODO: 2021/5/8
            new Thread(() -> bluetoothAdapter.enable()).start();
        }else {
            model.getBtStateData().setValue(true);
        }

        model.getBtStateData().observe(this, btState -> {
            if(btState) this.startDiscovery();
        });

    }

    private void startDiscovery(){
        // ??????????????????
        scanedItemAdapter.clear();
        model.getAmountData().setValue(0);
        model.clearAddressList();
        // ???????????????????????????
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        // ????????????
        bluetoothAdapter.startDiscovery();
        model.getDiscoveryData().setValue(true);
    }

    private void back(String name, String address){
        Intent intent = new Intent();
        intent.putExtra(BleIntentService.EXTRA_NAME, name);
        intent.putExtra(BleIntentService.EXTRA_ADDRESS, address);
        setResult(Activity.RESULT_OK, intent);
        this.finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        this.back(null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            model.getDiscoveryData().setValue(false);
        }
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }
}