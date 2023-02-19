package com.hdbar.hdbarapp.adapters;


import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.databinding.ItemContainerCocktailBinding;
import com.hdbar.hdbarapp.databinding.ItemOtherRecyclerViewBinding;
import com.hdbar.hdbarapp.databinding.ItemTopOfTenBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;

import java.util.List;
public class OthersAdapter extends RecyclerView.Adapter<OthersAdapter.CocktailViewHolder>{

    private final List<Cocktail> cocktails;
    private final CocktailListener listener;

    public OthersAdapter(List<Cocktail> cocktails, CocktailListener listener) {
            this.cocktails = cocktails;
            this.listener = listener;
    }

    @NonNull
    @Override
    public CocktailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOtherRecyclerViewBinding itemOtherRecyclerViewBinding = ItemOtherRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new CocktailViewHolder(itemOtherRecyclerViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailViewHolder holder, int position) {
            holder.setCocktailData(cocktails.get(position));
            }

    @Override
    public int getItemCount() {
            return cocktails.size();
            }

    class CocktailViewHolder extends RecyclerView.ViewHolder {

        ItemOtherRecyclerViewBinding binding;

        CocktailViewHolder(ItemOtherRecyclerViewBinding itemOtherRecyclerViewBinding) {
            super(itemOtherRecyclerViewBinding.getRoot());
            binding = itemOtherRecyclerViewBinding;
        }

        void setCocktailData(Cocktail cocktail){
            binding.topOfWeekName.setText(cocktail.name);
            binding.topOfWeekAuthor.setText(cocktail.creator);
            FirebaseStorage storage = FirebaseStorage.getInstance();

            storage.getReference(cocktail.image).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(binding.topOfWeekImage).load(task.getResult()).into(binding.topOfWeekImage);
                }
            });

            binding.getRoot().setOnClickListener(v -> listener.onCocktailClicked(cocktail));
        }
    }
}