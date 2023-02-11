package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.LoadBundleTask;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.adapters.CommentAdapter;
import com.hdbar.hdbarapp.adapters.sbsAdapter;
import com.hdbar.hdbarapp.databinding.ActivityCocktailPageBinding;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.models.Comments;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class CocktailPageActivity extends AppCompatActivity {

    private ActivityCocktailPageBinding binding;
    private String cocktailId;
    private Cocktail cocktail;
    public int how_many_rates;
    private RelativeLayout rating_relative;
    private FirebaseFirestore database;
    private float numberOfStars;
    private ImageSlider imageSlider;


    //rating
    public RatingBar simple_rating;
    public TextView local_rate,hm_rates;
    private Integer rate_i = 0;
    private ArrayList<Float> arrayListRatings;
    private boolean rating_bool = false;
    private float getRatingsFB;
    //-------


    private boolean isFavorite;
    private String uid;
    private String favoriteId;

    ArrayList<Comments> commentsModels = new ArrayList<>();

    String fruitList[]  ={"Apple","Bannana", "Appcorit", "Orange","Whater Melon"};

    RecyclerView RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageSlider = binding.imageSlider;
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image4, ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels,ScaleTypes.CENTER_CROP);
/*
        RecyclerView = (RecyclerView) findViewById(R.id.sbs_list);
        sbsAdapter sbsAdapter = new sbsAdapter(getApplicationContext(),fruitList);

        RecyclerView.setAdapter(sbsAdapter);*/

//transform to init
;
        init();
        setRating();
        if (!rating_bool){
            simpleRating();
        }
//        listeners();



        RecyclerView recyclerView = findViewById(R.id.sbs_list);
        CommentAdapter adapter = new CommentAdapter(this,commentsModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        setUpCommntModels();

        rate_i = 0;
        //Log.i("R", String.valueOf(rate_i));

    }

    private void setRating(){

        database.collection(Constants.KEY_COLLECTION_RATINGS)
                .whereEqualTo(Constants.KEY_USER_UID,uid)
                .whereEqualTo(Constants.KEY_COCKTAIL_ID,cocktailId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if (!task.getResult().isEmpty()){
                                simple_rating.setRating(Float.parseFloat(task.getResult().getDocuments().get(0).get(Constants.KEY_COCKTAIL_RATING).toString()));
                                simple_rating.setIsIndicator(false);
                                rating_bool = true;
                                Log.i("R", "rating seted");
                            }
                        }
                    }
                });
    }

    private void setUpCommntModels(){

        String[] comments = {"fijasldfj","fosajdoifjsa","dfasdfpsof","fmsapfo"};

        for (int i = 0; i < comments.length;i++){
            commentsModels.add(new Comments(comments[i]));
        }

    }

    private void simpleRating(){
        simple_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate_i++;

                if (rate_i == 2){
                    numberOfStars = simple_rating.getRating();
                    database.collection(Constants.KEY_COLLECTION_RATINGS)
                            .whereEqualTo(Constants.KEY_USER_UID,uid)
                            .whereEqualTo(Constants.KEY_COCKTAIL_ID,cocktailId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        if (task.getResult().isEmpty()){
                                            HashMap<String,Object> rating_hm = new HashMap<>();
                                            rating_hm.put(Constants.KEY_USER_UID,uid);
                                            rating_hm.put(Constants.KEY_COCKTAIL_ID,cocktailId);
                                            rating_hm.put(Constants.KEY_COCKTAIL_RATING,numberOfStars);
                                            database.collection(Constants.KEY_COLLECTION_RATINGS).add(rating_hm);
                                            simple_rating.setIsIndicator(false);
                                            ratingsSize();
                                            Log.i("Y", "off");
                                            Log.i("Y", rate_i + "");
                                        }
                                        else {
                                            simple_rating.setIsIndicator(true);
                                            rate_i--;
                                            Log.i("Y", "on");
                                            Log.i("Y", rate_i + "");
                                        }
                                    }
                                }
                            });

                }
            }
        });
    }

    private void ratingsSize(){

        database.collection(Constants.KEY_COLLECTION_RATINGS)
                .whereEqualTo(Constants.KEY_COCKTAIL_ID,cocktailId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if (!task.getResult().isEmpty()){
                                float sum = 0;
                                for (DocumentSnapshot snapshot:task.getResult()){
                                    getRatingsFB = Float.parseFloat(snapshot.get(Constants.KEY_COCKTAIL_RATING).toString());
                                    arrayListRatings.add(getRatingsFB);
                                    sum += getRatingsFB;
                                    //Log.e("Y", y + "");
                                }
                                local_rate.setText(String.format("%.01f",sum/arrayListRatings.size() )+ "");
                                hm_rates.setText(arrayListRatings.size() + "");
                            }

                        }
                    }
                });

    }

    private void init(){
        cocktailId = getIntent().getStringExtra(Constants.KEY_COCKTAIL_ID);
        database = FirebaseFirestore.getInstance();
        simple_rating=binding.ratingBar;
        local_rate = binding.ratingWithNumber;
        hm_rates = binding.howManyRates;
        rate_i = 0;
        arrayListRatings = new ArrayList<>();

        ratingsSize();

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




/*
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFavorite){
                    binding.favorite.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.favorite), android.graphics.PorterDuff.Mode.SRC_IN);;
                }
            }
        }, 500);*/

        FirebaseStorage storage = FirebaseStorage.getInstance();

        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .document(cocktailId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String cocktailName = documentSnapshot.getString(Constants.KEY_COCKTAIL_NAME);
                    String creator = documentSnapshot.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                    String image = documentSnapshot.get(Constants.KEY_COCKTAIL_IMAGE).toString();
                    String rating_count = documentSnapshot.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                    String recipe = documentSnapshot.get(Constants.KEY_COCKTAIL_RECIPE).toString();
                    String rating = documentSnapshot.get(Constants.KEY_COCKTAIL_RATING).toString();
                    cocktail = new Cocktail(documentSnapshot.getId(),cocktailName,recipe,image,rating,creator,rating_count);
                    //binding.cocktailAuthor.setText("Added by: "+cocktail.creator);
                    binding.cocktailName.setText(cocktail.name);/*
                    storage.getReference(cocktail.image).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Glide.with(binding.cocktailImage).load(task.getResult()).into(binding.cocktailImage);
                        }
                    });*//*
                    binding.cocktailImage.setImageBitmap(getCocktailImage(cocktail.image));
                    binding.cocktailRecipe.setText(cocktail.recipe);*/
                    binding.ratingBar.setRating(Float.valueOf(cocktail.rating));
                });

    }
/*
    private void listeners(){
        binding.imageBack.setOnClickListener(v->finish());
        binding.favorite.setOnClickListener(v->changeFavoriteStatus());
    }*/
/*

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

*/

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