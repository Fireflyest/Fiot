package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.R;
import com.fireflyest.fiot.bean.Line;
import com.fireflyest.fiot.databinding.ItemLineBinding;

import java.util.List;

public class LineItemAdapter extends RecyclerView.Adapter<LineItemAdapter.ViewHolder> {

    private final Context context;
    private final List<Line> lines;
    private final Drawable lightIcon;
    private final Drawable protectIcon;
    private final Drawable videoIcon;
    private final Drawable environmentIcon;

    public interface OnItemClickListener {
        void onclick(Line line);
    }

    private LineItemAdapter.OnItemClickListener clickListener;

    public LineItemAdapter(Context context, List<Line> lines) {
        this.context = context;
        this.lines = lines;
        lightIcon = ContextCompat.getDrawable(context, R.drawable.ic_light);
        protectIcon = ContextCompat.getDrawable(context, R.drawable.ic_protect);
        videoIcon = ContextCompat.getDrawable(context, R.drawable.ic_video);
        environmentIcon = ContextCompat.getDrawable(context, R.drawable.ic_environment);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLineBinding binding = ItemLineBinding.inflate(LayoutInflater.from(context), parent, false);
        return new LineItemAdapter.ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Line line = lines.get(position);

        holder.binding.setName(line.getName());

        switch (line.getName()){
            case "光线":
                holder.binding.setIcon(lightIcon);
                break;
            case "安防":
                holder.binding.setIcon(protectIcon);
                break;
            case "音视":
                holder.binding.setIcon(videoIcon);
                break;
            case "环境":
                holder.binding.setIcon(environmentIcon);
                break;
            default:
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onclick(line);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public final ItemLineBinding binding;

        public ViewHolder(@NonNull View itemView, ItemLineBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

}
