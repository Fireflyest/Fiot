package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fireflyest.fiot.bean.Characteristic;
import com.fireflyest.fiot.bean.Service;
import com.fireflyest.fiot.databinding.ItemCharacteristicBinding;
import com.fireflyest.fiot.databinding.ItemServiceBinding;

import java.util.List;

public class CharacteristicItemAdapter extends BaseAdapter {

    private final List<Characteristic> characteristics;
    private final LayoutInflater inflate;

    public CharacteristicItemAdapter(Context context, List<Characteristic> characteristics) {
        this.characteristics = characteristics;
        this.inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return characteristics.size();
    }

    @Override
    public Object getItem(int position) {
        return characteristics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Characteristic characteristic = characteristics.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            ItemCharacteristicBinding binding =  ItemCharacteristicBinding.inflate(inflate, parent, false);
            viewHolder = new ViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = ((ViewHolder) convertView.getTag());
        }

        viewHolder.binding.setCharacteristic(characteristic);

        return convertView;
    }

    static class ViewHolder{

        public ItemCharacteristicBinding binding;

        public ViewHolder(ItemCharacteristicBinding binding) {
            this.binding = binding;
        }
    }

}
