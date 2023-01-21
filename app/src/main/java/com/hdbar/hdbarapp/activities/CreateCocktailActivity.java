package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityCocktailPageBinding;
import com.hdbar.hdbarapp.databinding.ActivityCreateCocktailBinding;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

public class CreateCocktailActivity extends AppCompatActivity {

    private ActivityCreateCocktailBinding binding;

    private PreferenceManager preferenceManager;

    private ImageView cocktailImage;
    private TextView pickImageButton;
    private TextView imageChooseText;
    private EditText cocktailName;
    private EditText cocktailRecipe;
    private Integer SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCocktailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        cocktailName = binding.cocktailName;
        cocktailRecipe = binding.cocktailRecipe;
        cocktailImage = binding.cocktailImage;
        imageChooseText = binding.imageChooseText;
    }

    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        });
        binding.cocktailImage.setOnClickListener(v->chooseImage());
    }

    private void chooseImage(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    cocktailImage.setImageURI(selectedImageUri);
                }
            }
        }
    }
}