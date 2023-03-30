package com.drini.driniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.drini.driniapp.adapters.RecipeAdapter;
import com.drini.driniapp.adapters.TagAdapter;
import com.drini.driniapp.databinding.ActivityCocktailPageBinding;
import com.drini.driniapp.databinding.ActivityProfileBinding;
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

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseFirestore database;
    private String cocktailId;
    private Cocktail cocktail;
    public int how_many_rates;
    private RelativeLayout rating_relative;
    private float numberOfStars,sum;
    private ImageSlider imageSlider;
    private ArrayList<SlideModel> slideModels = new ArrayList<>();


    //rating
    public RatingBar simple_rating;
    public TextView local_rate,hm_rates,rate_btn;
    private Integer rate_i = 0;
    private ArrayList<Float> arrayListRatings;
    private boolean rating_bool = false;
    private float getRatingsFB;
    //-------

    public static boolean refresh = false;

    private boolean isFavorite;
    private String uid;
    private String favoriteId;

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
        cocktailId = getIntent().getStringExtra(Constants.KEY_COCKTAIL_ID);
        database = FirebaseFirestore.getInstance();
        arrayListRatings = new ArrayList<>();


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






        FirebaseStorage storage = FirebaseStorage.getInstance();

        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .document(cocktailId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String cocktailName = documentSnapshot.getString(Constants.KEY_COCKTAIL_NAME);
                    String creator = documentSnapshot.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                    String creatorId = documentSnapshot.getString(Constants.KEY_COCKTAIL_CREATOR_ID);
                    ArrayList<String> image = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_IMAGE);
                    ArrayList<String> tags = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_TAGS);
                    String rating_count = documentSnapshot.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                    ArrayList<String> recipe = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_RECIPE);
                    String rating = documentSnapshot.get(Constants.KEY_COCKTAIL_RATING).toString();
                    Date date = documentSnapshot.getDate(Constants.KEY_DATE);
                    cocktail = new Cocktail(documentSnapshot.getId(),cocktailName,recipe,image,rating,creator,rating_count,tags,date);

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
                                        }
                                    });
                                }
                            });

                    for(int i=0;i<cocktail.image.size();i++){
                        storage.getReference(cocktail.image.get(i)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                slideModels.add(new SlideModel(task.getResult().toString(), ScaleTypes.CENTER_CROP));
                                if(slideModels.size()==cocktail.image.size()){
                                }
                            }
                        });
                    }

                });

    }


    private void listeners(){
        binding.backProfile.setOnClickListener(v->finish());
    }
}

