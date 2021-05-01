package com.fireflyest.fiot.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireflyest.fiot.ControlActivity;
import com.fireflyest.fiot.R;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.databinding.FragmentControlNormalBinding;
import com.fireflyest.fiot.model.ControlViewModel;

public class ControlNormalFragment extends Fragment {

    private static final String TAG = "ControlNormalFragment";

    private ControlViewModel model;

    private FragmentControlNormalBinding binding;

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
        if (device != null) {

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(ControlActivity.EXTRA_DEVICE, device);
        super.onSaveInstanceState(outState);
    }


}