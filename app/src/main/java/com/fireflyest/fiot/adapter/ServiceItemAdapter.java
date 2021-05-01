package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.bean.Service;
import com.fireflyest.fiot.databinding.ItemServiceBinding;

import java.util.List;

public class ServiceItemAdapter extends RecyclerView.Adapter<ServiceItemAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceBinding binding = ItemServiceBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = services.get(position);
        holder.binding.setService(service);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public interface OnItemClickListener {
        void onclick();
    }

    private final Context context;
    private final OnItemClickListener clickListener;
    private List<Service> services;

    public ServiceItemAdapter(Context context, List<Service> services, OnItemClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        this.services = services;
    }


    public void setServices(List<Service> services) {
        this.services = services;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public final ItemServiceBinding binding;

        private ViewHolder(@NonNull View view, ItemServiceBinding binding) {
            super(view);
            this.binding = binding;
        }
    }


}
