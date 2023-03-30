package com.drini.driniapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.drini.driniapp.R;
import com.drini.driniapp.adapters.CocktailsAdapter;
import com.drini.driniapp.adapters.CocktailsSingleAdapter;
import com.drini.driniapp.databinding.ActivityGlassTypeBinding;
import com.drini.driniapp.listeners.CocktailListener;
import com.drini.driniapp.models.Cocktail;
import com.drini.driniapp.utilities.AlwaysOnRun;
import com.drini.driniapp.utilities.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GlassTypeActivity extends AppCompatActivity {

    private ActivityGlassTypeBinding binding;
    private List<Cocktail> cocktails;
    private FirebaseFirestore database;
    private String uid;

    private String glassType;
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
        binding = ActivityGlassTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        init();
        listeners();
        AlwaysOnRun.AlwaysRun(this);

    }


    private void init(){
        glassType = getIntent().getStringExtra(Constants.KEY_GLASS_TYPE);
        uid = FirebaseAuth.getInstance().getUid();
        database = FirebaseFirestore.getInstance();
        binding.type.setText(glassType.substring(0, 1).toUpperCase() + glassType.substring(1));
        getCocktails();
    }

    private void listeners(){
        binding.backHighball.setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.left_to_right_in,R.anim.left_to_right_out);
        });
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
    }


    private void getCocktails(){
        cocktails = new LinkedList<>();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setVisibility(View.INVISIBLE);
        binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
        cocktails = new LinkedList<>();
        i = 0;
        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .whereArrayContains(Constants.KEY_COCKTAIL_TAGS,glassType)
                .whereEqualTo(Constants.KEY_STATUS,"approved")
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
                            Date date = document.getDate(Constants.KEY_DATE);
                            Cocktail a = new Cocktail(document.getId(),cocktailName,recipe,image,rating,creator,rating_count,tags,date);
                            cocktails.add(a);
                            if(i==queryDocumentSnapshots.size()){
                                changeAdapter(0,cocktails);
                            }
                        }
                        if(queryDocumentSnapshots.size()==0){
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.textErrorMessage.setVisibility(View.VISIBLE);
                            binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
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