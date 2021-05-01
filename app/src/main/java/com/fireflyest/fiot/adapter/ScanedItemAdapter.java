package com.fireflyest.fiot.adapter;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.R;
import com.fireflyest.fiot.bean.Scaned;
import com.fireflyest.fiot.databinding.ItemScanedBinding;

import java.util.List;

public class ScanedItemAdapter extends RecyclerView.Adapter<ScanedItemAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onclick(String name, String address);
    }

    private final OnItemClickListener clickListener;
    private final Context context;
    private final List<Scaned> scaneds;

    private final Animation clickAnimation;

    public ScanedItemAdapter(Context context, List<Scaned> scaneds, OnItemClickListener clickListener) {
        this.context = context;
        this.scaneds = scaneds;
        this.clickListener = clickListener;
        clickAnimation = AnimationUtils.loadAnimation(context, R.anim.item_down);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScanedBinding binding = ItemScanedBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Scaned scaned = scaneds.get(position);
        holder.binding.setScaned(scaned);

        switch (scaned.getType()){
            case BluetoothClass.Device.Major.PHONE:
                holder.binding.scanedType.setImageResource(R.drawable.ic_phone);
                break;
            case BluetoothClass.Device.Major.COMPUTER:
                holder.binding.scanedType.setImageResource(R.drawable.ic_computer);
                break;
            case BluetoothClass.Device.Major.NETWORKING:
                holder.binding.scanedType.setImageResource(R.drawable.ic_networking);
                break;
            case BluetoothClass.Device.Major.WEARABLE:
                holder.binding.scanedType.setImageResource(R.drawable.ic_wearable);
                break;
            case BluetoothClass.Device.Major.HEALTH:
                holder.binding.scanedType.setImageResource(R.drawable.ic_health);
                break;
            case BluetoothClass.Device.Major.TOY:
                holder.binding.scanedType.setImageResource(R.drawable.ic_toy);
                break;
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                holder.binding.scanedType.setImageResource(R.drawable.ic_audio);
                break;
            case BluetoothClass.Device.Major.PERIPHERAL:
                holder.binding.scanedType.setImageResource(R.drawable.ic_peripheral);
                break;
            case 0x1F00:
                if(scaned.getName().contains("Smart Band")) {
                    holder.binding.scanedType.setImageResource(R.drawable.ic_wearable);
                }else {
                    holder.binding.scanedType.setImageResource(R.drawable.ic_bluetooth);
                }
                break;
            default:
                holder.binding.scanedType.setImageResource(R.drawable.ic_bluetooth);
                break;
        }
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                v.startAnimation(clickAnimation);
                clickListener.onclick(scaned.getName(), scaned.getAddress());
            }
        });
    }

    @Override
    public int getItemCount() {
        return scaneds.size();
    }


    public void addItem(Scaned scaned) {
        scaneds.add(scaned);
        notifyItemInserted(scaneds.size());
    }

    public void clear(){
        scaneds.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public final ItemScanedBinding binding;

        private ViewHolder(@NonNull View view, ItemScanedBinding binding) {
            super(view);
            this.binding = binding;
        }
    }

}
