package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityCocktailPageBinding;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;

public class CocktailPageActivity extends AppCompatActivity {

    private ActivityCocktailPageBinding binding;
    private String cocktailId;
    private Cocktail cocktail;
    public int how_many_rates;
    private RelativeLayout rating_relative;
    public RatingBar simple_rating;
    private FirebaseFirestore database;
    private float numberOfStars;

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