package com.hdbar.hdbarapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdbar.hdbarapp.databinding.ItemCocktailsRowBinding;
import com.hdbar.hdbarapp.databinding.ItemContainerCocktailBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;

import java.util.List;

public class RowAdapter extends RecyclerView.Adapter<RowAdapter.CocktailViewHolder>{

    private final List<Cocktail> cocktails;
    private final CocktailListener listener;

    public RowAdapter(List<Cocktail> cocktails, CocktailListener listener) {
        this.cocktails = cocktails;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CocktailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerCocktailBinding itemContainerUserBinding = ItemContainerCocktailBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CocktailViewHolder(itemContainerUserBinding);
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

        ItemContainerCocktailBinding binding;

        CocktailViewHolder(ItemContainerCocktailBinding itemContainerCocktailBinding) {
            super(itemContainerCocktailBinding.getRoot());
            binding = itemContainerCocktailBinding;
        }

        void setCocktailData(Cocktail cocktail){
            binding.cocktailName.setText(cocktail.name);
            binding.cocktailRating.setText(cocktail.rating+"");
//            binding.cocktailImage.setImageBitmap(getCocktailImage(cocktail.image));
            binding.getRoot().setOnClickListener(v -> listener.onCocktailClicked(cocktail));
        }
    }

    private Bitmap getCocktailImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
