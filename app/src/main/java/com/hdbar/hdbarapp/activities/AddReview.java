package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.hdbar.hdbarapp.adapters.TagAdapter;
import com.hdbar.hdbarapp.databinding.ActivityAddReviewBinding;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.settings.PrivacyPolicyActivity;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;

public class AddReview extends AppCompatActivity {


    private ActivityAddReviewBinding binding;
    private TextView back,cocktailName,publisherName;
    private RatingBar ratingBar;
    private EditText inputComment;
    private FirebaseFirestore database;
    private SwitchCompat isPrivate;
    private ShapeableImageView imageView;
    private Cocktail cocktail;
    private String cocktailId;
    private String uid;
    private String userId;
    private boolean isAnyThingChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listener();
        AlwaysOnRun.AlwaysRun(this);

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



    private void init(){
        back = binding.backAboutUs;
        ratingBar = binding.ratingBarRate;
        inputComment = binding.inputComment;
        isPrivate = binding.privateSwitch;
        database = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();
        publisherName = binding.ratePublisherName;
        imageView = binding.reviewImageCocktail;
        cocktailName =  binding.rateCocktailName;
        cocktailId = getIntent().getStringExtra(Constants.KEY_COCKTAIL_ID);

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
                        String recipe = documentSnapshot.get(Constants.KEY_COCKTAIL_RECIPE).toString();
                        String rating = documentSnapshot.get(Constants.KEY_COCKTAIL_RATING).toString();
                        cocktail = new Cocktail(documentSnapshot.getId(),coctailName,recipe,image,rating,creator,rating_count,tags);

                        publisherName.setText(creator);
                        cocktailName.setText(coctailName);
                    }
                });


    }


    private void listener() {
        back.setOnClickListener(view -> discardDialogBox());
        switchIsChanged();
        setInputComment();
        ratingChanged();



    }

    private void switchIsChanged(){
        isPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isAnyThingChanged)
                    Log.d("In", "false");
                else
                    Log.d("In", "true");

                isAnyThingChanged = !isAnyThingChanged;
            }
        });
    }



    private void setInputComment(){



    }

    private void ratingChanged(){

    }
}