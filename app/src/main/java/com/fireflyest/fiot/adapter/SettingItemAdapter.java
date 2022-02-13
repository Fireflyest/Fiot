package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.R;
import com.fireflyest.fiot.bean.Setting;
import com.fireflyest.fiot.data.SettingType;
import com.fireflyest.fiot.databinding.ItemSettingEditBinding;
import com.fireflyest.fiot.databinding.ItemSettingLineBinding;
import com.fireflyest.fiot.databinding.ItemSettingListBinding;
import com.fireflyest.fiot.databinding.ItemSettingNumberBinding;
import com.fireflyest.fiot.databinding.ItemSettingPasswordBinding;
import com.fireflyest.fiot.databinding.ItemSettingSwitchBinding;
import com.fireflyest.fiot.databinding.ItemSettingTextBinding;
import com.fireflyest.fiot.databinding.ItemSettingTitleBinding;

import java.util.ArrayList;
import java.util.List;

public class SettingItemAdapter extends RecyclerView.Adapter<SettingItemAdapter.ViewHolder> {

    private final Context context;
    private final List<Setting> settings;

    public static final String TAG = SettingItemAdapter.class.getSimpleName();

    private final ArrayAdapter<String> wifiListAdapter;

    public interface OnItemEditListener {
        void onEdit(Setting setting);
    }

    private OnItemEditListener editListener;

    public SettingItemAdapter(Context context, List<Setting> settings) {
        this.context = context;
        this.settings = settings;

        List<String> wifiList = new ArrayList<>();
        wifiListAdapter = new ArrayAdapter<>(context, R.layout.item_spinner, wifiList);
        //第三步：设置下拉列表下拉时的菜单样式
        wifiListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    public ArrayAdapter<String> getWifiListAdapter() {
        return wifiListAdapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        switch (viewType){
            default:
            case SettingType.TITLE:
                binding = ItemSettingTitleBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            case SettingType.LINE:
                binding = ItemSettingLineBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            case SettingType.TEXT:
                binding = ItemSettingTextBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            case SettingType.LIST:
            case SettingType.WIFI:
                binding = ItemSettingListBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            case SettingType.SWITCH:
                binding = ItemSettingSwitchBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            case SettingType.EDIT_TEXT:
                binding = ItemSettingEditBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            case SettingType.PASSWORD:
                binding = ItemSettingPasswordBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            case SettingType.EDIT_NUMBER:
                binding = ItemSettingNumberBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
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
//                if (setting.isEnable()){
//                    holder.itemView.setOnClickListener(v ->{
//                        final EditText editText = new EditText(context);
//                        editText.setText(setting.getStringValue());
//                        AlertDialog.Builder dialog =  new AlertDialog.Builder(context)
//                                .setTitle("修改" + setting.getName()).setView(editText)
//                                .setPositiveButton("确定", (d, which) -> {
//                                    String after = editText.getText().toString();
//                                    // 判断是否修改
//                                    if(!setting.getStringValue().equals(after)){
//                                        setting.setStringValue(after);
//                                        if (editListener != null) editListener.onEdit(setting);
//                                        updateItem(position);
//                                    }
//                                })
//                                .setNegativeButton("取消", (d, which) -> editText.getText());
//
//                        dialog.show();
//                    });
//                }
                break;
            }
            case SettingType.SWITCH: {
                ItemSettingSwitchBinding binding = (ItemSettingSwitchBinding) holder.binding;
                binding.setSetting(setting);
                break;
            }
            case SettingType.WIFI: {
                ItemSettingListBinding binding = (ItemSettingListBinding) holder.binding;
                binding.setSetting(setting);
                wifiListAdapter.clear();
                if (setting.getStringValue() != null) {
                    wifiListAdapter.add(setting.getStringValue());
                }
                //第四步：将适配器添加到下拉列表上
                binding.settingSpinner.setAdapter(wifiListAdapter);
                binding.settingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        setting.setStringValue(wifiListAdapter.getItem(position));
                        if (editListener != null) editListener.onEdit(setting);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
                break;
            }
            case SettingType.EDIT_TEXT: {
                ItemSettingEditBinding binding = (ItemSettingEditBinding) holder.binding;
                binding.setSetting(setting);
                binding.settingEdit.setEnabled(setting.isEnable());
                // 点击
                if (setting.isEnable()) holder.itemView.setOnClickListener(v ->{
                    binding.settingEdit.requestFocus();
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(binding.settingEdit, 0);
                });
                // 修改
                binding.settingEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        String after = s.toString();
                        setting.setStringValue(after);
                        if (editListener != null) editListener.onEdit(setting);
                    }
                });
                break;
            }
            case SettingType.PASSWORD: {
                ItemSettingPasswordBinding binding = (ItemSettingPasswordBinding) holder.binding;
                binding.setSetting(setting);
                binding.settingEdit.setEnabled(setting.isEnable());
                // 点击
                if (setting.isEnable()) holder.itemView.setOnClickListener(v ->{
                    binding.settingEdit.requestFocus();
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(binding.settingEdit, 0);
                });
                // 修改
                binding.settingEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        String after = s.toString();
                        setting.setStringValue(after);
                        if (editListener != null) editListener.onEdit(setting);
                    }
                });
                break;
            }
            case SettingType.EDIT_NUMBER: {
                ItemSettingNumberBinding binding = (ItemSettingNumberBinding) holder.binding;
                binding.setSetting(setting);
                binding.settingEdit.setEnabled(setting.isEnable());
                // 点击
                if (setting.isEnable()) holder.itemView.setOnClickListener(v ->{
                    binding.settingEdit.requestFocus();
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(binding.settingEdit, 0);
                });
                // 修改
                binding.settingEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        String after = s.toString();
                        setting.setStringValue(after);
                        if (editListener != null) editListener.onEdit(setting);
                    }
                });
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
