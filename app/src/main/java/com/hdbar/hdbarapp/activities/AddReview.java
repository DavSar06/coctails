package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityAddReviewBinding;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.AlwaysOnRun;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddReview extends AppCompatActivity {


    private ActivityAddReviewBinding binding;
    private TextView back,cocktailName,publisherName,ratingBarTV,postBtn;
    private RatingBar ratingBar;
    private EditText inputComment;
    private FirebaseFirestore database;
    private SwitchCompat isPrivate;
    private ShapeableImageView imageView;
    private Cocktail cocktail;
    private boolean isprivate = false;
    private String cocktailId;
    private String userId;
    private boolean isAnyThingChanged = false;
    private float numOfStarts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listener();
        AlwaysOnRun.AlwaysRun(this);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingBarTV.setText(ratingBar.getRating() + "");
            }
        });

    }



    private void posting() {
        addComment();
        setRating();

        CocktailPageActivity.refresh = true;

        //finishing
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }


    private void init(){
        back = binding.backAboutUs;
        inputComment = binding.inputComment;
        isPrivate = binding.privateSwitch;
        database = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();
        publisherName = binding.ratePublisherName;
        imageView = binding.reviewImageCocktail;
        cocktailName =  binding.rateCocktailName;
        cocktailId = getIntent().getStringExtra(Constants.KEY_COCKTAIL_ID);

        postBtn = binding.donateBtn;

        //rating-----------------------------------
        ratingBar=binding.ratingBarRate;
        ratingBarTV=binding.ratingWithNumber;
        //-----------------------------------------


        FirebaseStorage storage = FirebaseStorage.getInstance();

        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .document(cocktailId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String coctailName = documentSnapshot.getString(Constants.KEY_COCKTAIL_NAME);
                        String creator = documentSnapshot.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                        ArrayList<String> image = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_IMAGE);
                        ArrayList<String> tags = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_TAGS);
                        String rating_count = documentSnapshot.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                        ArrayList<String> recipe = (ArrayList<String>) documentSnapshot.get(Constants.KEY_COCKTAIL_RECIPE);
                        String rating = documentSnapshot.get(Constants.KEY_COCKTAIL_RATING).toString();
                        Date date = documentSnapshot.getDate(Constants.KEY_DATE);
                        cocktail = new Cocktail(documentSnapshot.getId(),coctailName,recipe,image,rating,creator,rating_count,tags,date);


                        //------------------------------------------



                        storage.getReference(cocktail.image.get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(binding.reviewImageCocktail).load(uri).into(binding.reviewImageCocktail);
                            }
                        });

                        //--------------------------------------------

                        publisherName.setText(creator);
                        cocktailName.setText(coctailName);
                    }
                });


    }


    private void setRating(){
                database.collection(Constants.KEY_COLLECTION_RATINGS)
                        .whereEqualTo(Constants.KEY_USER_UID,userId)
                        .whereEqualTo(Constants.KEY_COCKTAIL_ID,cocktailId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                numOfStarts = ratingBar.getRating();

                                if (task.isSuccessful()){
                                    if (!task.getResult().getDocuments().isEmpty()){
                                        task.getResult().getDocuments().get(0);
                                        database.collection(Constants.KEY_COLLECTION_RATINGS)
                                                .document(task.getResult().getDocuments().get(0).getId())
                                                .update(Constants.KEY_COCKTAIL_RATING,numOfStarts);
                                    }else {

                                        HashMap<String,Object> rating_hm = new HashMap<>();
                                        rating_hm.put(Constants.KEY_USER_UID,userId);
                                        rating_hm.put(Constants.KEY_COCKTAIL_ID,cocktailId);
                                        rating_hm.put(Constants.KEY_COCKTAIL_RATING,numOfStarts);
                                        database.collection(Constants.KEY_COLLECTION_RATINGS).add(rating_hm);
                                    }
                                }
                            }
                        });
    }

    private void addComment(){
        if(binding.inputComment.getText().length()>0) {
            HashMap<String, Object> comment = new HashMap<>();
            comment.put(Constants.KEY_COMMENT_BODY, binding.inputComment.getText().toString());
            comment.put(Constants.KEY_DATE,new Date());
            comment.put(Constants.KEY_COCKTAIL_ID,cocktailId);
            if (isprivate){
                comment.put(Constants.KEY_COMMENTER_ID,"EDwdFoxrDAUHAKJgmUqvW7YgMEM2");
            }else{
                comment.put(Constants.KEY_COMMENTER_ID,userId);
            }
            database.collection(Constants.KEY_COLLECTION_COMMENTS)
                    .add(comment);
            binding.inputComment.setText("");
        }
    }



    private void listener() {
        back.setOnClickListener(view -> discardDialogBox());
        switchIsChanged();
        setInputComment();
        ratingChanged();
        postBtn.setOnClickListener(view -> posting());

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                setRating();
            }
        });
    }

    private void switchIsChanged(){
        isPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isAnyThingChanged)
                    isprivate = false;
                else
                    isprivate = true;

                isAnyThingChanged = !isAnyThingChanged;
            }
        });
    }


    private void discardDialogBox(){
        new AlertDialog.Builder(this, R.style.DialogeTheme)
                .setTitle("Discard your edit?")
                .setMessage("Are you sure you want to discard your edit? Any changes you've made will be lost.")
                .setPositiveButton("Positive", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        Log.d("GG","Discard");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("GG","Cancel");
                    }
                })
                .show();

    }




    private void setInputComment(){



    }

    private void ratingChanged(){

    }
}