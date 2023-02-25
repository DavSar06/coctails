package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.adapters.ModeratePageAdapter;
import com.hdbar.hdbarapp.databinding.ActivityModeratePageBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class ModeratePageActivity extends AppCompatActivity {

    private ActivityModeratePageBinding binding;
    private List<Cocktail> cocktails;
    private FirebaseFirestore database;
    private ModeratePageAdapter adapter;

    private final CocktailListener cocktailListener = new CocktailListener() {
        @Override
        public void onCocktailClicked(Cocktail cocktail) {
            Intent intent = new Intent(binding.getRoot().getContext(), ModerateActivity.class);
            intent.putExtra(Constants.KEY_COCKTAIL_ID, cocktail.id);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
    };

    private void init(){
        database = FirebaseFirestore.getInstance();
        cocktails = new ArrayList<>();
        adapter = new ModeratePageAdapter(cocktails,cocktailListener);
        binding.cocktailsRecyclerView.setAdapter(adapter);
        updateRecyclerView();
    }

    private void updateRecyclerView(){
        binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);
        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .whereEqualTo(Constants.KEY_STATUS,Constants.KEY_COCKTAIL_STATUS_PENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                String cocktailName = document.getString(Constants.KEY_COCKTAIL_NAME);
                                String creator = document.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                                String recipe = document.get(Constants.KEY_COCKTAIL_RECIPE).toString();
                                ArrayList<String> image = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_IMAGE);
                                String rating = document.get(Constants.KEY_COCKTAIL_RATING).toString();
                                String rating_count = document.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                                Cocktail temp = new Cocktail(document.getId(),cocktailName,recipe,image,rating,creator,rating_count);
                                cocktails.add(temp);
                                adapter = new ModeratePageAdapter(cocktails,cocktailListener);
                                binding.cocktailsRecyclerView.setAdapter(adapter);
                            }
                        }else {
                            Log.d("Exception",task.getException().toString());
                        }
                    }
                });
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if(cocktails.size()==0){
                    binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.textErrorMessage.setVisibility(View.VISIBLE);
                }else {
                    binding.cocktailsRecyclerView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.textErrorMessage.setVisibility(View.INVISIBLE);
                }
            }
        },1000);
    }

    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        });
        binding.icBack.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModeratePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
    }
}