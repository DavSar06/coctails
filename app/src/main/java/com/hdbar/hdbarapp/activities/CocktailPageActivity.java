package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityCocktailPageBinding;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.HashMap;

public class CocktailPageActivity extends AppCompatActivity {

    private ActivityCocktailPageBinding binding;
    private String cocktailId;
    private Cocktail cocktail;
    public int how_many_rates;
    private RelativeLayout rating_relative;
    public RatingBar simple_rating;
    private FirebaseFirestore database;
    private float numberOfStars;

    private boolean isFavorite;
    private String uid;
    private String favoriteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();


        simple_rating.setRating((float) 3);

        simple_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                simple_rating.setEnabled(false);
                how_many_rates++;
                numberOfStars = simple_rating.getRating();

                database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                       .document(cocktailId)
                       .update(Constants.KEY_COCKTAIL_RATING,numberOfStars);
                Log.d("In", String.valueOf(numberOfStars));
                Log.d("In", String.valueOf(how_many_rates));
            }
        });

    }

    private void init(){
        cocktailId = getIntent().getStringExtra(Constants.KEY_COCKTAIL_ID);
        database = FirebaseFirestore.getInstance();
        simple_rating = binding.ratingBar;
        rating_relative = binding.ratingSystemSettings;

        uid = FirebaseAuth.getInstance().getUid();
        database.collection(Constants.KEY_COLLECTION_FAVORITES)
                .whereEqualTo(Constants.KEY_COCKTAIL_ID,cocktailId)
                .whereEqualTo(Constants.KEY_USER_UID,uid)
                .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                if(!task.getResult().isEmpty()){
                                    isFavorite = true;
                                    favoriteId = task.getResult().getDocuments().get(0).getId();
                                }else {
                                    isFavorite = false;
                                }
                            }else {
                                Log.d("FCM",task.getException().getMessage());
                                isFavorite = false;
                            }
                        }
                    });

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFavorite){
                    binding.favorite.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.favorite), android.graphics.PorterDuff.Mode.SRC_IN);;
                }
            }
        }, 500);

        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .document(cocktailId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String cocktailName = documentSnapshot.getString(Constants.KEY_COCKTAIL_NAME);
                    String creator = documentSnapshot.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                    String recipe = documentSnapshot.get(Constants.KEY_COCKTAIL_RECIPE).toString();
                    String image = documentSnapshot.get(Constants.KEY_COCKTAIL_IMAGE).toString();
                    String rating_count = documentSnapshot.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                    String rating = documentSnapshot.get(Constants.KEY_COCKTAIL_RATING).toString();
                    cocktail = new Cocktail(documentSnapshot.getId(),cocktailName,recipe,image,rating,creator,rating_count);
                    binding.cocktailAuthor.setText("Added by: "+cocktail.creator);
                    binding.cocktailName.setText(cocktail.name);
                    binding.cocktailImage.setImageBitmap(getCocktailImage(cocktail.image));
                    binding.cocktailRecipe.setText(cocktail.recipe);
                    binding.ratingBar.setRating(Float.valueOf(cocktail.rating));
                });

    }

    private void listeners(){
        binding.imageBack.setOnClickListener(v->finish());
        binding.favorite.setOnClickListener(v->changeFavoriteStatus());
    }

    private void changeFavoriteStatus(){
        if(!isFavorite){
            HashMap<String,Object> favorite = new HashMap<>();
            favorite.put(Constants.KEY_USER_UID,uid);
            favorite.put(Constants.KEY_COCKTAIL_ID,cocktailId);
            database.collection(Constants.KEY_COLLECTION_FAVORITES)
                    .add(favorite)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                isFavorite = true;
                                favoriteId = task.getResult().getId();
                                binding.favorite.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.favorite), android.graphics.PorterDuff.Mode.SRC_IN);;
                            }else{
                                Log.d("FCM",task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), "Problems occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            database.collection(Constants.KEY_COLLECTION_FAVORITES)
                    .document(favoriteId)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                isFavorite = false;
                                favoriteId = null;
                                binding.favorite.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.background_color_light), android.graphics.PorterDuff.Mode.SRC_IN);;
                            }else {
                                Log.d("FCM",task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), "Error occurred" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private Bitmap getCocktailImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}