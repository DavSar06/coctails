package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityModerateBinding;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class ModerateActivity extends AppCompatActivity {

    private ActivityModerateBinding binding;
    private Cocktail cocktail;
    private FirebaseFirestore database;
    private ArrayList<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModerateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
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
                        String recipe = documentSnapshot.get(Constants.KEY_COCKTAIL_RECIPE).toString();
                        ArrayList<String> image = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_IMAGE);
                        ArrayList<String> tags = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_TAGS);
                        String rating = documentSnapshot.get(Constants.KEY_COCKTAIL_RATING).toString();
                        String rating_count = documentSnapshot.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                        cocktail = new Cocktail(documentSnapshot.getId(),cocktailName,recipe,image,rating,creator,rating_count,tags);
                        binding.cocktailAuthor.setText("Added by: "+cocktail.creator);
                        binding.cocktailName.setText(cocktail.name);
                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        storage.getReference(cocktail.image.get(0)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Glide.with(binding.cocktailImage).load(task.getResult()).into(binding.cocktailImage);
                            }
                        });
                        binding.cocktailRecipe.setText(cocktail.recipe);
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