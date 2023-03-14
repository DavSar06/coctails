package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.adapters.CocktailsAdapter;
import com.hdbar.hdbarapp.adapters.CocktailsSingleAdapter;
import com.hdbar.hdbarapp.databinding.ActivitySearchBinding;
import com.hdbar.hdbarapp.databinding.FragmentFavoriteBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.SearchHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private List<Cocktail> cocktails;
    private FirebaseFirestore database;
    private String uid;
    Integer i;
    Integer adapterStatus = 0;

    private final CocktailListener cocktailListener = new CocktailListener() {
        @Override
        public void onCocktailClicked(Cocktail cocktail) {
            Intent intent = new Intent(binding.getRoot().getContext(), CocktailPageActivity.class);
            intent.putExtra(Constants.KEY_COCKTAIL_ID, cocktail.id);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
    }

    private void init(){
        uid = FirebaseAuth.getInstance().getUid();
        database = FirebaseFirestore.getInstance();
        getCocktails();
    }


    private void listeners(){
        binding.icBack.setOnClickListener(v->finish());
        binding.rowDouble.setOnClickListener(v->{
            if(adapterStatus!=0){
                binding.rowSingle.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.background_color_light), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.rowDouble.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
                binding.textErrorMessage.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                adapterStatus = 0;
                changeAdapter(adapterStatus,cocktails);
            }
        });
        binding.rowSingle.setOnClickListener(v->{
            if(adapterStatus!=1){
                binding.rowSingle.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.rowDouble.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.background_color_light), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
                binding.textErrorMessage.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                adapterStatus = 1;
                changeAdapter(adapterStatus,cocktails);
            }
        });
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Cocktail> searchResult = SearchHelper.searchInCocktails(charSequence.toString(), cocktails);
                changeAdapter(adapterStatus,searchResult);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void getCocktails(){
        cocktails = new LinkedList<>();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setVisibility(View.INVISIBLE);
        binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
        cocktails = new LinkedList<>();
        i = 0;
        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document: queryDocumentSnapshots){
                            i++;
                            String cocktailName = document.getString(Constants.KEY_COCKTAIL_NAME);
                            String creator = document.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                            ArrayList<String> recipe = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_RECIPE);
                            String rating_count = document.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                            ArrayList<String> image = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_IMAGE);
                            ArrayList<String> tags = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_TAGS);
                            String rating = document.get(Constants.KEY_COCKTAIL_RATING).toString();
                            Cocktail a = new Cocktail(document.getId(),cocktailName,recipe,image,rating,creator,rating_count,tags);
                            cocktails.add(a);
                            if(i==queryDocumentSnapshots.size()){
                                changeAdapter(0,cocktails);
                            }
                        }
                    }
                });
    }

    private void changeAdapter(Integer k,List<Cocktail> cocktails){
        if(cocktails.isEmpty()){
            binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textErrorMessage.setVisibility(View.VISIBLE);
        }else {
            if(k==0){
                CocktailsAdapter adapter = new CocktailsAdapter(cocktails,cocktailListener);
                binding.cocktailsRecyclerView.setAdapter(adapter);
            }else{
                CocktailsSingleAdapter adapter = new CocktailsSingleAdapter(cocktails,cocktailListener);
                binding.cocktailsRecyclerView.setAdapter(adapter);
            }
            binding.cocktailsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textErrorMessage.setVisibility(View.INVISIBLE);
        }
    }
}