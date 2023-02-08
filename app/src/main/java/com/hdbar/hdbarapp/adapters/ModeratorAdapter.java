package com.hdbar.hdbarapp.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ItemModeratorBinding;
import com.hdbar.hdbarapp.listeners.DropdownListener;
import com.hdbar.hdbarapp.models.User;

import java.util.List;

public class ModeratorAdapter extends RecyclerView.Adapter<ModeratorAdapter.UserViewHolder>{

    private final List<User> users;
    private DropdownListener listener;

    public ModeratorAdapter(List<User> users,DropdownListener listener) {
        this.users = users;
        this.listener = listener;
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
            binding.downArrow.setOnClickListener(v->{
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), binding.downArrow);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        listener.onItemClick(menuItem.toString(),user.uid);
                        return false;
                    }
                });
                popupMenu.show();
            });
        }
    }
}
