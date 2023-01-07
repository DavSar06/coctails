package com.hdbar.hdbarapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdbar.hdbarapp.databinding.ItemCocktailsRowBinding;
import com.hdbar.hdbarapp.databinding.ItemImageRowBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.listeners.ImageListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.models.ProfileImage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.RowViewHolder> {

    private HashMap<String, List<ProfileImage>> images;
    private List<String> categories;

    public ImagesAdapter(HashMap<String, List<ProfileImage>> images) {
        this.images = images;
        categories = new LinkedList<>();
        images.forEach((k,v)->{
            categories.add(k);
        });
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageRowBinding itemImageRowBinding = ItemImageRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ImagesAdapter.RowViewHolder(itemImageRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        holder.setRowData(images.get(categories.get(position)));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class RowViewHolder extends RecyclerView.ViewHolder {

        ItemImageRowBinding binding;

        RowViewHolder(ItemImageRowBinding itemImageRowBinding) {
            super(itemImageRowBinding.getRoot());
            binding = itemImageRowBinding;
        }

        void setRowData(List<ProfileImage> images){
            binding.categoryName.setText(images.get(0).category);
            binding.imageRowRecyclerView.setAdapter(new ImagesRowAdapter(images, new ImageListener() {
                @Override
                public void onImageClicked(ProfileImage profileImage) {
                    Toast.makeText(itemView.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                }
            }));
            binding.imageRowRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
