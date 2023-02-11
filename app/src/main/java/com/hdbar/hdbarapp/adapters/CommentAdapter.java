package com.hdbar.hdbarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.models.Comments;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    Context context;
    ArrayList<Comments> commentsModels;

    public CommentAdapter (Context context, ArrayList<Comments> commentsModels){
        this.context = context;
        this.commentsModels = commentsModels;
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_cocktails_row,parent, false);
        return new CommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {

        //holder.tv_comment.setText(commentsModels.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return commentsModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_comment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_comment = itemView.findViewById(R.id.tv_comments);
        }
    }
}
