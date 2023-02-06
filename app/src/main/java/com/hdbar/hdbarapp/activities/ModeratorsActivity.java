package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hdbar.hdbarapp.databinding.ActivityModeratorsBinding;

public class ModeratorsActivity extends AppCompatActivity {

    private ActivityModeratorsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModeratorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
    }

    private void init(){

    }

    private void listeners(){

    }
}