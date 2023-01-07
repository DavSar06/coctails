package com.hdbar.hdbarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hdbar.hdbarapp.adapters.CocktailsAdapter;
import com.hdbar.hdbarapp.databinding.ActivityMainBinding;
import com.hdbar.hdbarapp.models.Cocktail;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private List<Cocktail> cocktails;

    private List<List<Cocktail>> Cocktails = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cocktails = new LinkedList<>();

        for(int i=0;i<100;i++){
            Cocktail a = new Cocktail("id"+i,"Test","123",1);
            cocktails.add(a);
        }



        binding.cocktailsRecyclerView.setAdapter(new CocktailsAdapter(cocktails));
        binding.cocktailsRecyclerView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.INVISIBLE);
    }
}