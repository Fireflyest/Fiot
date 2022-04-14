package com.fireflyest.fiot.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fireflyest.fiot.bean.Device;

import java.util.ArrayList;
import java.util.List;

public class CommandViewModel extends ViewModel {

    private final MutableLiveData<List<Device>> deviceListData = new MutableLiveData<>();

    public MutableLiveData<List<Device>> getDeviceListData() {
        return deviceListData;
    }

}
