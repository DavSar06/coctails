package com.hdbar.hdbarapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.databinding.ItemModerateRecyclerViewBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;

import java.util.List;

public class ModeratePageAdapter extends RecyclerView.Adapter<ModeratePageAdapter.CocktailViewHolder>{

    private final List<Cocktail> cocktails;
    private final CocktailListener listener;

    public ModeratePageAdapter(List<Cocktail> cocktails, CocktailListener listener) {
        this.cocktails = cocktails;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModeratePageAdapter.CocktailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemModerateRecyclerViewBinding itemModerateRecyclerViewBinding = ItemModerateRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ModeratePageAdapter.CocktailViewHolder(itemModerateRecyclerViewBinding);
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

        ItemModerateRecyclerViewBinding binding;

        CocktailViewHolder(ItemModerateRecyclerViewBinding itemModerateRecyclerViewBinding) {
            super(itemModerateRecyclerViewBinding.getRoot());
            binding = itemModerateRecyclerViewBinding;
        }

        void setCocktailData(Cocktail cocktail){
            binding.cocktailName.setText(cocktail.name);
            binding.cocktailAuthor.setText("Added by: "+cocktail.creator);
            FirebaseStorage storage = FirebaseStorage.getInstance();

            storage.getReference(cocktail.image.get(0)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(binding.cocktailImage).load(task.getResult()).into(binding.cocktailImage);
                }
            });
            binding.getRoot().setOnClickListener(v -> listener.onCocktailClicked(cocktail));
        }
    }
}
