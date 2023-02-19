package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.adapters.CommentAdapter;
import com.hdbar.hdbarapp.databinding.ActivityCocktailPageBinding;
import com.hdbar.hdbarapp.listeners.CommentImageListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.models.Comment;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;
import java.util.Date;
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

    private CommentImageListener listener = new CommentImageListener() {
        @Override
        public void onImageClicked(String userId) {
            Intent i = new Intent(getApplicationContext(),UserPageActivity.class);
            i.putExtra(Constants.KEY_USER_UID,userId);
            startActivity(i);
        }
    };

    ArrayList<Comment> commentsModels = new ArrayList<>();

    private CommentAdapter commentAdapter = new CommentAdapter(commentsModels,listener);

    String fruitList[]  ={"Apple","Bannana", "Appcorit", "Orange","Whater Melon"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageSlider = binding.imageSlider;
        ArrayList<SlideModel> slideModels = new ArrayList<>();


//        Default Images in slider (To change)
        slideModels.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image4, ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels,ScaleTypes.CENTER_CROP);
//

        init();
        listeners();
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
        setRating();
        if (!rating_bool){
            simpleRating();
        }
        ratingsSize();
        binding.commentsRecyclerView.setAdapter(commentAdapter);
        showComments();
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
                    binding.favouriteStar.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.favorite), android.graphics.PorterDuff.Mode.SRC_IN);;
                }
            }
        }, 500);

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

    private void listeners(){
        binding.imageBack.setOnClickListener(v->finish());
        binding.favouriteStar.setOnClickListener(v->changeFavoriteStatus());
        binding.addCommentBtn.setOnClickListener(v->addComment());
    }

    private void addComment(){
        if(binding.inputComment.getText().length()>0) {
            HashMap<String, Object> comment = new HashMap<>();
            comment.put(Constants.KEY_COMMENT_BODY, binding.inputComment.getText().toString());
            comment.put(Constants.KEY_DATE,new Date());
            comment.put(Constants.KEY_COCKTAIL_ID,cocktailId);
            comment.put(Constants.KEY_COMMENTER_ID,uid);
            database.collection(Constants.KEY_COLLECTION_COMMENTS)
                    .add(comment)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            showComments();
                        }
                    });
            binding.inputComment.setText("");
        }
    }

    private void showComments(){
        commentsModels = new ArrayList<>();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.commentsRecyclerView.setVisibility(View.INVISIBLE);
        database.collection(Constants.KEY_COLLECTION_COMMENTS)
                .whereEqualTo(Constants.KEY_COCKTAIL_ID,cocktailId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(snapshot.get(Constants.KEY_COMMENTER_ID).toString())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String comment = snapshot.getString(Constants.KEY_COMMENT_BODY).toString();
                                            Date date = snapshot.getDate(Constants.KEY_DATE);
                                            String uid = snapshot.get(Constants.KEY_COMMENTER_ID).toString();
                                            String commenter = documentSnapshot.get(Constants.KEY_USERNAME).toString();
                                            String commenterImage = documentSnapshot.get(Constants.KEY_USER_IMAGE).toString();
                                            Comment singleComment = new Comment(comment,commenter,date,uid,commenterImage);
                                            commentsModels.add(singleComment);
                                        }
                                    });
                        }
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(commentsModels.size()!=0){
                                    commentAdapter = new CommentAdapter(commentsModels,listener);
                                    binding.commentsRecyclerView.setAdapter(commentAdapter);
                                    binding.progressBar.setVisibility(View.INVISIBLE);
                                    binding.commentsRecyclerView.setVisibility(View.VISIBLE);
                                }else{
                                    binding.progressBar.setVisibility(View.INVISIBLE);
                                    binding.commentsRecyclerView.setVisibility(View.INVISIBLE);
                                }
                            }
                        },500);
                    }
                });
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
                                binding.favouriteStar.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.favorite), android.graphics.PorterDuff.Mode.SRC_IN);;
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
                                binding.favouriteStar.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.background_color_light), android.graphics.PorterDuff.Mode.SRC_IN);;
                            }else {
                                Log.d("FCM",task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), "Error occurred" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}