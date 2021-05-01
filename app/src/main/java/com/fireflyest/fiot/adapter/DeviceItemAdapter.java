package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.data.DeviceType;
import com.fireflyest.fiot.R;
import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.databinding.ItemDeviceBinding;

import java.util.List;

public class DeviceItemAdapter extends RecyclerView.Adapter<DeviceItemAdapter.ViewHolder> {

    private final Context context;
    private final List<Device> devices;
    private final Drawable grayDot;
    private final Drawable blueDot;
    private final Drawable greenDot;

    public static final String TAG = "DeviceItemAdapter";

    public interface OnItemClickListener {
        void onclick(Device device, TextView name);
    }
    public interface OnItemLongClickListener {
        void onLongClick(Device device);
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
        Log.d(TAG, "onBindViewHolder -> " + device.toString());
        // 如果没别称，设置别称
        if(device.getNickname() == null){
            String name = device.getName();
            if(name != null && name.length() > 16) name = String.format("%s...", name.substring(0, 14));
            device.setNickname(name);
        }
        holder.binding.setDevice(device);
        if (device.isConnect()){
            if(device.getType() == DeviceType.NON || device.getType() == DeviceType.LOCAL){
                holder.binding.setDot(blueDot);
            }else {
                holder.binding.setDot(greenDot);
            }
        }else {
            holder.binding.setDot(grayDot);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onclick(device, holder.binding.deviceName);

            }
            Animation clickAnimation;
            if(device.isConnect()){
                clickAnimation = AnimationUtils.loadAnimation(context, R.anim.item_down);
            }else {
                clickAnimation = AnimationUtils.loadAnimation(context, R.anim.item_click);
            }
            v.startAnimation(clickAnimation);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(device);
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

    public void updateItem(int index, Device device){
        Log.d(TAG, "updateItem -> " + device.toString());
        devices.set(index, device);
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
        return d.getType() * (d.isConnect() ? 1 : -1);
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
