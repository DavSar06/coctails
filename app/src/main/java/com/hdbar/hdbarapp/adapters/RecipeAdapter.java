package com.hdbar.hdbarapp.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdbar.hdbarapp.databinding.ItemStepByStepBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.StepViewHolder> {

    private List<String> steps;
    private List<String> tags;

    public RecipeAdapter(List<String> steps, List<String> tags){
        this.steps = steps;
        this.tags = tags;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStepByStepBinding itemStepByStepBinding = ItemStepByStepBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new RecipeAdapter.StepViewHolder(itemStepByStepBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.setStep(steps.get(position),position);
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

        void setStep(String step,Integer position){
            String stepNum = (position+1)+"";
            step = stepNum+" "+step;
            SpannableString finalString = new SpannableString(step);

            String a[] = step.split(" ");
            List<String> charsNormal = Arrays.asList(a);
            List<String> chars = new ArrayList<>();
            int length = 0;
            for(int i=0;i< charsNormal.size();i++){
                chars.add(charsNormal.get(i).toLowerCase());
            }

            for(int i=0;i<charsNormal.size();i++){
                boolean contains = false;
                for(int j=0;j<tags.size();j++){
                    if(chars.get(i).contains(tags.get(j))){
                        contains = true;
                        break;
                    }
                }
                if(contains){
                    finalString.setSpan(new BackgroundColorSpan(Color.argb(50,0,150,255)),length,length+chars.get(i).length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    finalString.setSpan(new StyleSpan(Typeface.ITALIC),length,length+chars.get(i).length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                length+=chars.get(i).length()+1;
            }
            binding.step.setText(finalString);
        }
    }
}
