package com.hdbar.hdbarapp.glasses_types;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.activities.CocktailPageActivity;
import com.hdbar.hdbarapp.adapters.CocktailsAdapter;
import com.hdbar.hdbarapp.adapters.CocktailsSingleAdapter;
import com.hdbar.hdbarapp.databinding.ActivityHighballBinding;
import com.hdbar.hdbarapp.databinding.ActivitySearchBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.AlwaysOnRun;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.SearchHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HighballActivity extends AppCompatActivity {

    private ActivityHighballBinding binding;
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
        binding = ActivityHighballBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
        AlwaysOnRun.AlwaysRun(this);
        //changeAdapter(adapterStatus,s);

    }


    private void init(){
        uid = FirebaseAuth.getInstance().getUid();
        database = FirebaseFirestore.getInstance();
        getCocktails();
    }

    private void listeners(){
        binding.backHighball.setOnClickListener(v->{
            finish();});
    }


    private void getCocktails(){
        cocktails = new LinkedList<>();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setVisibility(View.INVISIBLE);
        binding.cocktailsRecyclerViewHighBall.setVisibility(View.INVISIBLE);
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
                            Log.d("GG",cocktails.size() + " ");
                        }
                    }
                });
    }


    private void changeAdapter(Integer k,List<Cocktail> cocktails){
        if(cocktails.isEmpty()){
            binding.cocktailsRecyclerViewHighBall.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textErrorMessage.setVisibility(View.VISIBLE);
        }else {
            if(k==0){
                CocktailsAdapter adapter = new CocktailsAdapter(cocktails,cocktailListener);
                binding.cocktailsRecyclerViewHighBall.setAdapter(adapter);
            }else{
                CocktailsSingleAdapter adapter = new CocktailsSingleAdapter(cocktails,cocktailListener);
                binding.cocktailsRecyclerViewHighBall.setAdapter(adapter);
            }
            binding.cocktailsRecyclerViewHighBall.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textErrorMessage.setVisibility(View.INVISIBLE);
        }
    }
}