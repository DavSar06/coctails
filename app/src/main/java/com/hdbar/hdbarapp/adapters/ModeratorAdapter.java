package com.hdbar.hdbarapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ItemModeratorBinding;
import com.hdbar.hdbarapp.models.User;

import java.util.List;

public class ModeratorAdapter extends RecyclerView.Adapter<ModeratorAdapter.UserViewHolder>{

    private final List<User> users;

    public ModeratorAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ModeratorAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemModeratorBinding itemModeratorBinding = ItemModeratorBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ModeratorAdapter.UserViewHolder(itemModeratorBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position),position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemModeratorBinding binding;

        UserViewHolder(ItemModeratorBinding itemModeratorBinding) {
            super(itemModeratorBinding.getRoot());
            binding = itemModeratorBinding;
        }

        void setUserData(User user,Integer position){
            if(position%2==1){
                binding.getRoot().setBackgroundResource(R.color.background_color);
            }
            binding.userName.setText(user.name);
            binding.userEmail.setText(user.email);
        }
    }
}
