package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.R;
import com.fireflyest.fiot.bean.Command;
import com.fireflyest.fiot.data.CommandType;
import com.fireflyest.fiot.databinding.ItemCommandRecevieBinding;
import com.fireflyest.fiot.databinding.ItemCommandSendBinding;
import com.fireflyest.fiot.databinding.ItemCommandSystemBinding;
import com.fireflyest.fiot.databinding.ItemDeviceBinding;
import com.fireflyest.fiot.util.AnimationUtils;

import java.util.List;

public class CommandItemAdapter extends RecyclerView.Adapter<CommandItemAdapter.ViewHolder> {

    private final Context context;
    private final List<Command> commands;

    public static final String TAG = CommandItemAdapter.class.getSimpleName();

    public interface OnItemClickListener {
        void onclick(Command command, TextView name);
    }
    public interface OnItemLongClickListener {
        void onLongClick(Command command);
    }

    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public CommandItemAdapter(Context context, List<Command> commands) {
        this.context = context;
        this.commands = commands;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        switch (viewType){
            default:
            case CommandType.SEND: {
                binding = ItemCommandSendBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            }
            case CommandType.RECEIVE:{
                binding = ItemCommandRecevieBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            }
            case CommandType.SYSTEM:{
                binding = ItemCommandSystemBinding.inflate(LayoutInflater.from(context), parent, false);
                break;
            }
        }
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Command command = commands.get(position);
        int viewType = this.getItemViewType(position);
        switch (viewType){
            default:
            case CommandType.SEND: {
                ((ItemCommandSendBinding) holder.binding).setCommand(command);
                break;
            }
            case CommandType.RECEIVE: {
                ((ItemCommandRecevieBinding) holder.binding).setCommand(command);
                break;
            }
            case CommandType.SYSTEM: {
                ((ItemCommandSystemBinding) holder.binding).setCommand(command);
                break;
            }
        }
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void addItem(Command command){
        Log.d(TAG, "addItem -> " + command.toString());
        commands.add(command);
        notifyItemInserted(commands.size() - 1);
    }

    public void updateItem(int index){
        Log.d(TAG, "updateItem -> " + index);
        notifyItemChanged(index);
    }

    public void removeItem(int index){
        Log.d(TAG, "removeItem -> index = " + index);
        commands.remove(index);
        notifyItemRemoved(index);
        if (index != commands.size()) {
            notifyItemRangeChanged(index, commands.size() - index);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Command d = commands.get(position);
        return d.getType();
    }

    @Override
    public int getItemCount() {
        return commands.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public final ViewDataBinding binding;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

}
