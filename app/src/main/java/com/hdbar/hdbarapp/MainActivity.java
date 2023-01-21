package com.hdbar.hdbarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hdbar.hdbarapp.adapters.CocktailsAdapter;
import com.hdbar.hdbarapp.databinding.ActivityMainBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private PreferenceManager preferenceManager;

    private List<Cocktail> cocktails;

    private List<List<Cocktail>> Cocktails = new LinkedList<>();

    private final CocktailListener cocktailListener = new CocktailListener() {
        @Override
        public void onCocktailClicked(Cocktail cocktail) {
            Intent intent = new Intent(binding.getRoot().getContext(), CocktailPageActivity.class);
            intent.putExtra("cocktailId",cocktail.id);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        cocktails = new LinkedList<>();

        for(int i=0;i<100;i++){
            Cocktail a = new Cocktail("id"+i,"Test","123",1);
            cocktails.add(a);
        }

        binding.cocktailsRecyclerView.setAdapter(new CocktailsAdapter(cocktails,cocktailListener));
        binding.cocktailsRecyclerView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.INVISIBLE);



        String userName = preferenceManager.getString(Constants.KEY_USERNAME);
        String userEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        String userPhotoUrl = preferenceManager.getString(Constants.KEY_USER_IMAGE);

    }


    //For log out


}