package com.hdbar.hdbarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hdbar.hdbarapp.R;

public class sbsAdapter extends RecyclerView.ViewHolder {

    Context ctx;
    String [] listSbs;
    ImageView imageView;
    TextView usernamevie;
    LayoutInflater inflater;


    public sbsAdapter(@NonNull View itemView) {
        super(itemView);
    }
}
