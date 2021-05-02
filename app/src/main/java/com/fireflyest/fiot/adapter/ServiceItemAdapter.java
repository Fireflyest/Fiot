package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fireflyest.fiot.bean.Service;
import com.fireflyest.fiot.databinding.ItemServiceBinding;

import java.util.List;

public class ServiceItemAdapter extends BaseAdapter {

    private final List<Service> services;
    private final LayoutInflater inflate;

    public ServiceItemAdapter(Context context, List<Service> services) {
        this.services = services;
        this.inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return services.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Service service = services.get(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            ItemServiceBinding binding =  ItemServiceBinding.inflate(inflate, parent, false);
            viewHolder = new ViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = ((ViewHolder) convertView.getTag());
        }

        viewHolder.binding.setService(service);

        return convertView;
    }

    static class ViewHolder{

        public ItemServiceBinding binding;

        public ViewHolder(ItemServiceBinding binding) {
            this.binding = binding;
        }
    }

}
