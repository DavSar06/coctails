package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityModerateBinding;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;

public class ModerateActivity extends AppCompatActivity {

    private ActivityModerateBinding binding;
    private Cocktail cocktail;
    private FirebaseFirestore database;

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
        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .document(getIntent().getStringExtra(Constants.KEY_COCKTAIL_ID))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String cocktailName = documentSnapshot.getString(Constants.KEY_COCKTAIL_NAME);
                        String creator = documentSnapshot.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                        String recipe = documentSnapshot.get(Constants.KEY_COCKTAIL_RECIPE).toString();
                        String image = documentSnapshot.get(Constants.KEY_COCKTAIL_IMAGE).toString();
                        String rating = documentSnapshot.get(Constants.KEY_COCKTAIL_RATING).toString();
                        String rating_count = documentSnapshot.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                        cocktail = new Cocktail(documentSnapshot.getId(),cocktailName,recipe,image,rating,creator,rating_count);
                        binding.cocktailAuthor.setText("Added by: "+cocktail.creator);
                        binding.cocktailName.setText(cocktail.name);
                        binding.cocktailImage.setImageBitmap(getCocktailImage(cocktail.image));
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
                    .update(Constants.KEY_COCKTAIL_STATUS,Constants.KEY_COCKTAIL_STATUS_APPROVED)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            MainActivity.moderateActivity = true;
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        }
                    });
        });
        binding.denyBtn.setOnClickListener(v->{
            database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                    .document(cocktail.id)
                    .update(Constants.KEY_COCKTAIL_STATUS,Constants.KEY_COCKTAIL_STATUS_DENIED)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            MainActivity.moderateActivity = true;
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        }
                    });
        });
    }

    private Bitmap getCocktailImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}