package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.hdbar.hdbarapp.databinding.ActivityChooseImageBinding;


public class ChooseImageActivity extends AppCompatActivity {

    private ActivityChooseImageBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        setListeners();
    }

    public void setListeners(){
        binding.imageBack.setOnClickListener(v->finish());
    }
}