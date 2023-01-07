package com.hdbar.hdbarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hdbar.hdbarapp.databinding.ActivityRegisterBinding;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        setListeners();
    }

    public void setListeners(){
        binding.registerIconChooseIconToChange.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),ChooseImageActivity.class)));
    }
}