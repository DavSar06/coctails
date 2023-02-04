package com.hdbar.hdbarapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
            binding.cocktailImage.setImageBitmap(getCocktailImage(cocktail.image));
            binding.getRoot().setOnClickListener(v -> listener.onCocktailClicked(cocktail));
        }
    }

    private Bitmap getCocktailImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
