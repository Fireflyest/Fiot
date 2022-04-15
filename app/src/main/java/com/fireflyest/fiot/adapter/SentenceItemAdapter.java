package com.fireflyest.fiot.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.fiot.bean.Sentence;
import com.fireflyest.fiot.databinding.ItemSentenceBinding;

import java.util.List;

public class SentenceItemAdapter extends RecyclerView.Adapter<SentenceItemAdapter.ViewHolder> {

    private final Context context;
    private final List<Sentence> sentences;

    public static final String TAG = "SentenceItemAdapter";

    public interface OnItemClickListener {
        void onclick(Sentence sentence);
    }

    private OnItemClickListener clickListener;

    public SentenceItemAdapter(Context context, List<Sentence> sentences) {
        this.context = context;
        this.sentences = sentences;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSentenceBinding binding = ItemSentenceBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Sentence sentence = sentences.get(position);
        holder.binding.setSentence(sentence);
        // 点击
        holder.itemView.setOnClickListener(v -> {
            holder.binding.sentenceCheck.callOnClick();
        });
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void addItem(Sentence sentence){
        Log.d(TAG, "addItem -> " + sentence.toString());
        sentences.add(sentence);
        notifyItemInserted(sentences.size() - 1);
    }

    public void updateItem(int index){
        Log.d(TAG, "updateItem -> " + index);
        notifyItemChanged(index);
    }

    public void removeItem(int index){
        Log.d(TAG, "removeItem -> index = " + index);
        sentences.remove(index);
        notifyItemRemoved(index);
        if (index != sentences.size()) {
            notifyItemRangeChanged(index, sentences.size() - index);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return sentences.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public final ItemSentenceBinding binding;

        public ViewHolder(@NonNull View itemView, ItemSentenceBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

}
