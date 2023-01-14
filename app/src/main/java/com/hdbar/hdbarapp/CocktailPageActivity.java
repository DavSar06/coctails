package com.hdbar.hdbarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.hdbar.hdbarapp.databinding.ActivityCocktailPageBinding;

public class CocktailPageActivity extends AppCompatActivity {

    private ActivityCocktailPageBinding binding;
    private String cocktailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cocktailId = getIntent().getStringExtra("cocktailId");
        Toast.makeText(getApplicationContext(), cocktailId, Toast.LENGTH_SHORT).show();
    }
}