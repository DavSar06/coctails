package com.hdbar.hdbarapp.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.databinding.ItemOtherRecyclerViewBinding;
import com.hdbar.hdbarapp.databinding.ItemStepByStepBinding;
import com.hdbar.hdbarapp.models.Cocktail;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.StepViewHolder> {

    private List<String> steps;

    public RecipeAdapter(List<String> steps){
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStepByStepBinding itemStepByStepBinding = ItemStepByStepBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new RecipeAdapter.StepViewHolder(itemStepByStepBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.setStep(steps.get(position), position);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }


    class StepViewHolder extends RecyclerView.ViewHolder {

        ItemStepByStepBinding binding;

        StepViewHolder(ItemStepByStepBinding itemStepByStepBinding) {
            super(itemStepByStepBinding.getRoot());
            binding = itemStepByStepBinding;
        }

        void setStep(String step,Integer stepNum){
            binding.step.setText((stepNum+1)+" "+step);
        }
    }
}
