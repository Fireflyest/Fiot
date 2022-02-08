package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.bean.Setting;
import com.fireflyest.fiot.data.SettingType;
import com.fireflyest.fiot.databinding.ItemSettingLineBinding;
import com.fireflyest.fiot.databinding.ItemSettingSwitchBinding;
import com.fireflyest.fiot.databinding.ItemSettingTextBinding;
import com.fireflyest.fiot.databinding.ItemSettingTitleBinding;
import com.fireflyest.fiot.util.ToastUtil;

import java.util.List;

public class SettingItemAdapter extends RecyclerView.Adapter<SettingItemAdapter.ViewHolder> {

    private final Context context;
    private final List<Setting> settings;

    public static final String TAG = SettingItemAdapter.class.getSimpleName();

    public interface OnItemEditListener {
        void onEdit(Setting setting);
    }

    private OnItemEditListener editListener;

    public SettingItemAdapter(Context context, List<Setting> settings) {
        this.context = context;
        this.settings = settings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        switch (viewType){
            default:
            case SettingType.TITLE: {
                binding = ItemSettingTitleBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            }
            case SettingType.LINE: {
                binding = ItemSettingLineBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            }
            case SettingType.TEXT:{
                binding = ItemSettingTextBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            }
            case SettingType.SWITCH:{
                binding = ItemSettingSwitchBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            }
//            case SettingType.NAVIGATION:{
//                binding = ItemSettingNavigationBinding.inflate(LayoutInflater.from(context), parent, false);
//                break;
//            }
        }
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Setting setting = settings.get(position);
        int viewType = this.getItemViewType(position);
        Log.d(TAG, "onBindViewHolder -> " + setting.toString());
        Log.d(TAG, "position -> " + position);

        switch (viewType){
            default:
            case SettingType.TITLE: {
                ItemSettingTitleBinding binding = (ItemSettingTitleBinding) holder.binding;
                binding.setSetting(setting);
                break;
            }
            case SettingType.LINE: {
                ItemSettingLineBinding binding = (ItemSettingLineBinding) holder.binding;
                binding.setSetting(setting);
                break;
            }
            case SettingType.TEXT: {
                ItemSettingTextBinding binding = (ItemSettingTextBinding) holder.binding;
                binding.setSetting(setting);
                // 可修改
                if (setting.isEnable()){
                    holder.itemView.setOnClickListener(v ->{
                        final EditText editText = new EditText(context);
                        editText.setText(setting.getStringValue());
                        AlertDialog.Builder dialog =  new AlertDialog.Builder(context)
                                .setTitle("修改" + setting.getName()).setView(editText)
                                .setPositiveButton("确定", (d, which) -> {
                                    String after = editText.getText().toString();
                                    // 判断是否修改
                                    if(!setting.getStringValue().equals(after)){
                                        setting.setStringValue(after);
                                        if (editListener != null) editListener.onEdit(setting);
                                        updateItem(position);
                                    }
                                })
                                .setNegativeButton("取消", (d, which) -> editText.getText());

                        dialog.show();
                    });
                }
                break;
            }
            case SettingType.SWITCH: {
                ItemSettingSwitchBinding binding = (ItemSettingSwitchBinding) holder.binding;
                binding.setSetting(setting);
                break;
            }
        }
    }

    public void setEditListener(OnItemEditListener editListener) {
        this.editListener = editListener;
    }


    public void addItem(Setting setting){
        Log.d(TAG, "addItem -> " + setting.toString());
        settings.add(setting);
        notifyItemInserted(settings.size() - 1);

        int preIndex = settings.size() - 2;
        if (preIndex >= 0)notifyItemChanged(preIndex);

    }

    public void updateItem(int index){
        Log.d(TAG, "updateItem -> " + index);
        notifyItemChanged(index);
    }

    public void removeItem(int index){
        Log.d(TAG, "removeItem -> index = " + index);
        settings.remove(index);
        notifyItemRemoved(index);
        if (index != settings.size()) {
            notifyItemRangeChanged(index, settings.size() - index);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Setting s = settings.get(position);
        return s.getType();
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public final ViewDataBinding binding;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

}
