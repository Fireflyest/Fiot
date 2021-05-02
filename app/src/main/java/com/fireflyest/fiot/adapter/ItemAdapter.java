package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter<B, D extends ViewDataBinding> extends RecyclerView.Adapter<ItemAdapter.ViewHolder<D>> {

    private final OnClickListener<B> clickListener;
    private final Context context;
    private final List<B> bs;

     public interface OnClickListener<B>{
        void onclick(B b);
    }

    public ItemAdapter(Context context, List<B> bs, OnClickListener<B> clickListener) {
        this.context = context;
        this.bs = bs;
        this.clickListener =clickListener;
    }

    static class ViewHolder<D extends ViewDataBinding> extends RecyclerView.ViewHolder{

         public D d;

        public ViewHolder(@NonNull View itemView, D d) {
            super(itemView);
            this.d = d;
        }
    }

    @NonNull
    @Override
    public ViewHolder<D> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        D d = D.inflate(LayoutInflater.from(context), parent, false);
//        return new ViewHolder<D>(binding.getRoot(), binding);
        return null;
    }

    @Override
    public int getItemCount() {
        return bs.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        B b = bs.get(position);
//        holder.d.set
    }

}
