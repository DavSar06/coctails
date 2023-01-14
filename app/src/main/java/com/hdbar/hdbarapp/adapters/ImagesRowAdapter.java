package com.hdbar.hdbarapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdbar.hdbarapp.databinding.ItemContainerCocktailBinding;
import com.hdbar.hdbarapp.databinding.ItemContainerImageBinding;
import com.hdbar.hdbarapp.databinding.ItemImageRowBinding;
import com.hdbar.hdbarapp.listeners.ImageListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.models.ProfileImage;

import java.util.List;

public class ImagesRowAdapter extends RecyclerView.Adapter<ImagesRowAdapter.ImageViewHolder>{

    private List<ProfileImage> profileImages;
    private ImageListener listener;

    public ImagesRowAdapter(List<ProfileImage> profileImages, ImageListener listener) {
        this.profileImages = profileImages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerImageBinding itemImageRowBinding = ItemContainerImageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ImagesRowAdapter.ImageViewHolder(itemImageRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.setRowData(profileImages.get(position));
    }

    @Override
    public int getItemCount() {
        return profileImages.size();
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {

        ItemContainerImageBinding binding;

        ImageViewHolder(ItemContainerImageBinding itemContainerImageBinding) {
            super(itemContainerImageBinding.getRoot());
            binding = itemContainerImageBinding;
        }

        void setRowData(ProfileImage profileImage){
//            binding.imageView.setImageBitmap(getProfileImage(profileImage.src));
            binding.imageView.setBackgroundColor(Color.BLACK);
            binding.getRoot().setOnClickListener(v -> listener.onImageClicked(profileImage));
        }
    }

    private Bitmap getProfileImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
