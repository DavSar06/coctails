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
import com.hdbar.hdbarapp.databinding.ItemCommentBinding;
import com.hdbar.hdbarapp.listeners.CommentImageListener;
import com.hdbar.hdbarapp.models.Comment;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    ArrayList<Comment> commentsModels;
    private final CommentImageListener listener;

    public CommentAdapter (ArrayList<Comment> commentsModels,CommentImageListener listener){
        this.commentsModels = commentsModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding itemCommentBinding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CommentAdapter.MyViewHolder(itemCommentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {
        holder.setComment(commentsModels.get(position));
    }

    @Override
    public int getItemCount() {
        return commentsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ItemCommentBinding binding;

        public MyViewHolder(@NonNull ItemCommentBinding itemCommentBinding) {
            super(itemCommentBinding.getRoot());
            binding = itemCommentBinding;
        }

        public void setComment(Comment comment){
            binding.comment.setText(comment.comment);
            binding.commenter.setText(comment.commenter);
            FirebaseStorage storage = FirebaseStorage.getInstance();

            storage.getReference(comment.uImage).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(binding.image).load(task.getResult()).into(binding.image);
                }
            });
            binding.image.setOnClickListener(v->listener.onImageClicked(comment.uid));
        }
    }
}
