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
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.drini.driniapp.R;
import com.drini.driniapp.adapters.CommentAdapter;
import com.drini.driniapp.adapters.RecipeAdapter;
import com.drini.driniapp.adapters.TagAdapter;
import com.drini.driniapp.databinding.ActivityCocktailPageBinding;
import com.drini.driniapp.listeners.CommentImageListener;
import com.drini.driniapp.models.Cocktail;
import com.drini.driniapp.models.Comment;
import com.drini.driniapp.utilities.AlwaysOnRun;
import com.drini.driniapp.utilities.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class CocktailPageActivity extends AppCompatActivity {

    private ActivityCocktailPageBinding binding;
    private String cocktailId;
    private Cocktail cocktail;
    public int how_many_rates;
    private RelativeLayout rating_relative;
    private FirebaseFirestore database;

    private String creatorId;

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

    private CommentImageListener listener = new CommentImageListener() {
        @Override
        public void onImageClicked(String userId) {
            Intent i = new Intent(getApplicationContext(),UserPageActivity.class);
            i.putExtra(Constants.KEY_USER_UID,userId);
            startActivity(i);
        }
    };

    private Comparator<Comment> comparator = new Comparator<Comment>() {
        @Override
        public int compare(Comment comment, Comment t1) {
            return t1.date.compareTo(comment.date);
        }
    };

    ArrayList<Comment> commentsModels = new ArrayList<>();

    private CommentAdapter commentAdapter = new CommentAdapter(commentsModels,listener);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
        AlwaysOnRun.AlwaysRun(this);
    }



    private void init(){
        cocktailId = getIntent().getStringExtra(Constants.KEY_COCKTAIL_ID);
        database = FirebaseFirestore.getInstance();
        simple_rating=binding.ratingBar;
        local_rate = binding.ratingWithNumber;
        hm_rates = binding.howManyRates;
        rate_i = 0;
        rate_btn = binding.addReviewBtn;
        arrayListRatings = new ArrayList<>();

        binding.commentsRecyclerView.setNestedScrollingEnabled(false);
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
                    String creatorId = documentSnapshot.getString(Constants.KEY_COCKTAIL_CREATOR_ID);
                    ArrayList<String> image = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_IMAGE);
                    ArrayList<String> tags = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_TAGS);
                    String rating_count = documentSnapshot.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                    ArrayList<String> recipe = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_RECIPE);
                    String rating = documentSnapshot.get(Constants.KEY_COCKTAIL_RATING).toString();
                    Date date = documentSnapshot.getDate(Constants.KEY_DATE);
                    cocktail = new Cocktail(documentSnapshot.getId(),cocktailName,recipe,image,rating,creator,rating_count,tags,date);

                    this.creatorId = creatorId;

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

                    for(int i=0;i<cocktail.image.size();i++){
                        storage.getReference(cocktail.image.get(i)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                slideModels.add(new SlideModel(task.getResult().toString(),ScaleTypes.CENTER_CROP));
                                if(slideModels.size()==cocktail.image.size()){
                                    binding.imageSlider.setImageList(slideModels,ScaleTypes.CENTER_CROP);
                                }
                            }
                        });
                    }

                    TagAdapter tagAdapter = new TagAdapter(cocktail.tags);
                    binding.tagsRecyclerView.setAdapter(tagAdapter);
                    binding.tagsRecyclerView.setVisibility(View.VISIBLE);

                    binding.cocktailName.setText(cocktail.name);

                    binding.recipeRecyclerView.setAdapter(new RecipeAdapter(recipe,tags));

                    String formatted = String.format("%.1f", Float.valueOf(cocktail.rating));

                    binding.ratingBar.setRating(Float.valueOf(cocktail.rating));
                    hm_rates.setText(cocktail.rating_count+"");
                    local_rate.setText(formatted);
                });

    }

    private void listeners(){
        binding.imageBack.setOnClickListener(v->finish());
        binding.favouriteStar.setOnClickListener(v->changeFavoriteStatus());
        rate_btn.setOnClickListener(view -> addReview());
        binding.creatorImage.setOnClickListener(v->goToUserPage());
    }

    private void goToUserPage(){
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra(Constants.KEY_COCKTAIL_CREATOR_ID,creatorId);
        startActivity(intent);
    }

    private void addReview(){
        Intent intent = new Intent(getApplicationContext(), AddReview.class);
        intent.putExtra(Constants.KEY_COCKTAIL_ID,cocktailId);
        startActivityForResult(intent,0);
        //getActivity().overridePendingTransition(R.anim.right_to_left_in,R.anim.right_to_left_out);
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
                                commentsModels.sort(comparator);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(refresh){
            onRestart();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}