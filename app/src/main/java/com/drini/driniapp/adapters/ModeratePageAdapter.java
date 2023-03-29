package com.drini.driniapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.drini.driniapp.R;
import com.drini.driniapp.databinding.ItemModerateRecyclerViewBinding;
import com.drini.driniapp.listeners.CocktailListener;
import com.drini.driniapp.models.Cocktail;

import java.text.SimpleDateFormat;
import java.util.List;

public class ModeratePageAdapter extends RecyclerView.Adapter<ModeratePageAdapter.CocktailViewHolder>{

    private final List<Cocktail> cocktails;
    private final CocktailListener listener;
    private final Context context;

    public ModeratePageAdapter(List<Cocktail> cocktails, CocktailListener listener, Context context) {
        this.cocktails = cocktails;
        this.listener = listener;
        this.context = context;
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
            binding.cocktailAuthor.setText(cocktail.creator);
            SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            binding.cocktailDate.setText(targetDateFormat.format(cocktail.date));
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
