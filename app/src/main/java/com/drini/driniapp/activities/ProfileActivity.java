package com.drini.driniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.drini.driniapp.R;
import com.drini.driniapp.adapters.CocktailsAdapter;
import com.drini.driniapp.adapters.RecipeAdapter;
import com.drini.driniapp.adapters.TagAdapter;
import com.drini.driniapp.databinding.ActivityCocktailPageBinding;
import com.drini.driniapp.databinding.ActivityProfileBinding;
import com.drini.driniapp.listeners.CocktailListener;
import com.drini.driniapp.models.Cocktail;
import com.drini.driniapp.utilities.AlwaysOnRun;
import com.drini.driniapp.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseFirestore database;
    private String creatorId;

    Integer i;
    LinkedList<Cocktail> cocktails;

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
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
        AlwaysOnRun.AlwaysRun(this);

    }



    private void init(){
        creatorId = getIntent().getStringExtra(Constants.KEY_COCKTAIL_CREATOR_ID);
        database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(creatorId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        storage.getReference(documentSnapshot.getString(Constants.KEY_USER_IMAGE)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Glide.with(binding.userImage).load(task.getResult()).into(binding.userImage);
                            }
                        });
                    }
                });

        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .whereEqualTo(Constants.KEY_COCKTAIL_CREATOR_ID,creatorId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            cocktails = new LinkedList<>();
                            i = 0;
                            for(DocumentSnapshot snapshot: task.getResult()){
                                i++;
                                Log.i("test",snapshot.get(Constants.KEY_COCKTAIL_CREATOR_NAME)+"");
                                database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                                        .document(snapshot.getId())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot document) {
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
                                                if(i==task.getResult().size()){
                                                    CocktailsAdapter adapter = new CocktailsAdapter(cocktails,cocktailListener);
                                                    binding.recyclerviewYourCocktails.setAdapter(adapter);
                                                    binding.accountNickname.setText(creator);
                                                }
                                            }
                                        });

                            }
                        }
                    }
                });



    }


    private void listeners(){
        binding.backProfile.setOnClickListener(v->finish());
    }
}

