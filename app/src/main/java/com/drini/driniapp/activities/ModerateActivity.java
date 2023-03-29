package com.drini.driniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.drini.driniapp.R;
import com.drini.driniapp.adapters.RecipeAdapter;
import com.drini.driniapp.adapters.TagAdapter;
import com.drini.driniapp.databinding.ActivityModerateBinding;
import com.drini.driniapp.models.Cocktail;
import com.drini.driniapp.utilities.AlwaysOnRun;
import com.drini.driniapp.utilities.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ModerateActivity extends AppCompatActivity {

    private ActivityModerateBinding binding;
    private Cocktail cocktail;
    private FirebaseFirestore database;
    private ArrayList<String> tags = new ArrayList<>();
    private ArrayList<SlideModel> slideModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModerateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
        AlwaysOnRun.AlwaysRun(this);
    }

    private void init(){
        database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_TAGS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot snapshot:queryDocumentSnapshots){
                            tags.add(snapshot.get(Constants.KEY_TAG_NAME).toString());
                        }
                    }
                });
        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .document(getIntent().getStringExtra(Constants.KEY_COCKTAIL_ID))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String cocktailName = documentSnapshot.getString(Constants.KEY_COCKTAIL_NAME);
                        String creator = documentSnapshot.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                        String creatorId = documentSnapshot.getString(Constants.KEY_COCKTAIL_CREATOR_ID);
                        ArrayList<String> recipe = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_RECIPE);
                        ArrayList<String> image = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_IMAGE);
                        ArrayList<String> tags = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_TAGS);
                        String rating = documentSnapshot.get(Constants.KEY_COCKTAIL_RATING).toString();
                        String rating_count = documentSnapshot.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                        Date date = documentSnapshot.getDate(Constants.KEY_DATE);
                        cocktail = new Cocktail(documentSnapshot.getId(),cocktailName,recipe,image,rating,creator,rating_count,tags,date);
                        binding.cocktailName.setText(cocktail.name);
                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        for(int i=0;i<cocktail.image.size();i++){
                            storage.getReference(cocktail.image.get(i)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    slideModels.add(new SlideModel(task.getResult().toString(), ScaleTypes.CENTER_CROP));
                                    if(slideModels.size()==cocktail.image.size()){
                                        binding.imageSlider.setImageList(slideModels,ScaleTypes.CENTER_CROP);
                                    }
                                }
                            });
                        }

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
                                                Glide.with(binding.creatorImage).load(task.getResult()).into(binding.creatorImage);
                                            }
                                        });
                                    }
                                });

                        binding.recipeRecyclerView.setAdapter(new RecipeAdapter(recipe,tags));
                        
                        TagAdapter tagAdapter = new TagAdapter(cocktail.tags);
                        binding.tagsRecyclerView.setAdapter(tagAdapter);
                        binding.tagsRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void listeners(){
        binding.imageBack.setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });
        binding.approveBtn.setOnClickListener(v->{
            database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                    .document(cocktail.id)
                    .update(Constants.KEY_STATUS,Constants.KEY_COCKTAIL_STATUS_APPROVED)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            for(int i=0;i<cocktail.tags.size();i++){
                                if(!tags.contains(cocktail.tags.get(i))){
                                    HashMap<String,Object> tag = new HashMap<>();
                                    tag.put(Constants.KEY_TAG_NAME,cocktail.tags.get(i));
                                    database.collection(Constants.KEY_COLLECTION_TAGS)
                                            .add(tag);
                                }
                            }
                            Intent i = new Intent(getApplicationContext(),ModeratePageActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        }
                    });
        });
        binding.denyBtn.setOnClickListener(v->{
            database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                    .document(cocktail.id)
                    .update(Constants.KEY_STATUS,Constants.KEY_COCKTAIL_STATUS_DENIED)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent i = new Intent(getApplicationContext(),ModeratePageActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        }
                    });
        });
    }
}