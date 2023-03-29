package com.drini.driniapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drini.driniapp.databinding.ItemTagBinding;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder>{
    private ArrayList<String> tags;

    public TagAdapter(ArrayList<String> tags){
        this.tags = tags;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTagBinding itemTagBinding = ItemTagBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new TagAdapter.TagViewHolder(itemTagBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.setTag(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        ItemTagBinding binding;

        TagViewHolder(ItemTagBinding itemTagBinding) {
            super(itemTagBinding.getRoot());
            binding = itemTagBinding;
        }

        void setTag(String tag){
            binding.tag.setText(tag);
        }
    }
}
