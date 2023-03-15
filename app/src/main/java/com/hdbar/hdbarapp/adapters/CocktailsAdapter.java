package com.hdbar.hdbarapp.adapters;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdbar.hdbarapp.databinding.ItemCocktailsRowBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;

import java.util.LinkedList;
import java.util.List;

public class CocktailsAdapter extends RecyclerView.Adapter<CocktailsAdapter.RowViewHolder> {

    private List<List<Cocktail>> cocktails;
    private CocktailListener cocktailListener;
    private final Integer size = 2;
    private Integer k = 0;

    public CocktailsAdapter(List<Cocktail> cocktails,CocktailListener cocktailListener) {
        this.cocktailListener = cocktailListener;
        this.cocktails = new LinkedList<>();
        int k=-1;
        //int size = 3;
        List<Cocktail> temp = new LinkedList<>();
        for(int i=0;i<cocktails.size();i++){
            if(i%size==0){
                k++;
                this.cocktails.add(new LinkedList<>());
            }
            this.cocktails.get(k).add(cocktails.get(i));
        }
//        System.out.println(Cocktails);
        if((k+1)*size<cocktails.size()){
            this.cocktails.add(new LinkedList<>());
            k++;
            for(int i=(k+1)*size;i<cocktails.size();i++){
                this.cocktails.get(k).add(cocktails.get(i));
            }
        }
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCocktailsRowBinding itemCocktailsRowBinding = ItemCocktailsRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CocktailsAdapter.RowViewHolder(itemCocktailsRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        holder.setRowData(cocktails.get(position));
    }

    @Override
    public int getItemCount() {
        return cocktails.size();
    }

    class RowViewHolder extends RecyclerView.ViewHolder {

        ItemCocktailsRowBinding binding;

        RowViewHolder(ItemCocktailsRowBinding itemCocktailsRowBinding) {
            super(itemCocktailsRowBinding.getRoot());
            binding = itemCocktailsRowBinding;
        }

        void setRowData(List<Cocktail> cocktails){
            binding.rowRecyclerView.setAdapter(new RowAdapter(cocktails, cocktailListener));
            binding.rowRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
