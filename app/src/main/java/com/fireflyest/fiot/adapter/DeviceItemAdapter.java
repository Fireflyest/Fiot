package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.R;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.data.DeviceType;
import com.fireflyest.fiot.databinding.ItemDeviceBinding;
import com.fireflyest.fiot.model.MainViewModel;
import com.fireflyest.fiot.util.AnimationUtils;

import java.util.List;

public class DeviceItemAdapter extends RecyclerView.Adapter<DeviceItemAdapter.ViewHolder> {

    private final Context context;
    private final List<Device> devices;
    private final Drawable grayDot;
    private final Drawable blueDot;
    private final Drawable greenDot;

    public static final String TAG = "DeviceItemAdapter";

    public interface OnItemClickListener {
        void onclick(Device device, View background);
    }
    public interface OnItemLongClickListener {
        void onLongClick(Device device, View background);
    }

    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public DeviceItemAdapter(Context context, List<Device> devices) {
        this.context = context;
        this.devices = devices;
        grayDot = ContextCompat.getDrawable(context, R.drawable.ic_dot_gray);
        blueDot = ContextCompat.getDrawable(context, R.drawable.ic_dot_blue);
        greenDot = ContextCompat.getDrawable(context, R.drawable.ic_dot_green);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeviceBinding binding = ItemDeviceBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Device device = devices.get(position);
        final String address = device.getAddress();
        Log.d(TAG, "onBindViewHolder -> " + device.toString());
        // 如果没别称，设置别称
        if(device.getNickname() == null){
            String name = device.getName();
            if(name != null && name.length() > 15) name = String.format("%s...", name.substring(0, 13));
            device.setNickname(name);
        }
        // 展示设备
        if (device.getType() == DeviceType.NON){
            device.setState(device.getAddress());
        }
        holder.binding.setDevice(device);
        // 连接状态
        int state = MainViewModel.getConnectState(device.getAddress());
        if(state == 1){
            holder.binding.setDot(blueDot);
        }else if(state == 2 && (device.getType() & DeviceType.REMOTE) != 0){
            holder.binding.setDot(greenDot);
        }else{
            holder.binding.setDot(grayDot);
        }
        // 点击
        holder.itemView.setOnClickListener(v -> {
            if(MainViewModel.getConnectState(address) == 0){
                AnimationUtils.click(v);
            }
            if (clickListener != null) {
                clickListener.onclick(device, holder.binding.deviceBackground);
            }
        });
        // 长按
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(device, holder.binding.deviceBackground);
            }
            return true;
        });
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void addItem(Device device){
        Log.d(TAG, "addItem -> " + device.toString());
        devices.add(device);
        notifyItemInserted(devices.size() - 1);
    }

    public void updateItem(int index){
        Log.d(TAG, "updateItem -> " + index);
        notifyItemChanged(index);
    }

    public void removeItem(int index){
        Log.d(TAG, "removeItem -> index = " + index);
        devices.remove(index);
        notifyItemRemoved(index);
        if (index != devices.size()) {
            notifyItemRangeChanged(index, devices.size() - index);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Device d = devices.get(position);
        return d.getType() * (MainViewModel.getConnectState(d.getAddress()) != 0 ? 1 : -1);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public final ItemDeviceBinding binding;

        public ViewHolder(@NonNull View itemView, ItemDeviceBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

}
