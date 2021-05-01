package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.bean.Characteristic;
import com.fireflyest.fiot.databinding.ItemCharacteristicBinding;

import java.util.List;

public class CharacteristicItemAdapter extends RecyclerView.Adapter<CharacteristicItemAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCharacteristicBinding binding = ItemCharacteristicBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Characteristic characteristic = characteristics.get(position);
        holder.binding.setCharacteristic(characteristic);
    }

    @Override
    public int getItemCount() {
        return characteristics.size();
    }

    public interface OnItemClickListener {
        void onclick();
    }

    private final Context context;
    private final OnItemClickListener clickListener;
    private List<Characteristic> characteristics;

    public CharacteristicItemAdapter(Context context, List<Characteristic> characteristics, OnItemClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        this.characteristics = characteristics;
    }

    public void setCharacteristics(List<Characteristic> characteristics) {
        this.characteristics = characteristics;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public final ItemCharacteristicBinding binding;

        private ViewHolder(@NonNull View view, ItemCharacteristicBinding binding) {
            super(view);
            this.binding = binding;
        }
    }


}
