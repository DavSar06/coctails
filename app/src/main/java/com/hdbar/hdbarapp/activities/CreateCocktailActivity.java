package com.hdbar.hdbarapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityCocktailPageBinding;
import com.hdbar.hdbarapp.databinding.ActivityCreateCocktailBinding;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class CreateCocktailActivity extends AppCompatActivity {

    private ActivityCreateCocktailBinding binding;

    private PreferenceManager preferenceManager;

    private ImageView cocktailImage;
    private TextView pickImageButton;
    private TextView imageChooseText;
    private EditText cocktailName;
    private EditText cocktailRecipe;
    private Integer SELECT_PICTURE = 200;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCocktailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        init();
        listeners();

        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        for (int i = 0; i < 10; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setImageBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.ic_launcher_background));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            layout.addView(imageView);
        }
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        cocktailName = binding.cocktailName;
        cocktailRecipe = binding.cocktailRecipe;
        //cocktailImage = binding.cocktailImage;
        //imageChooseText = binding.imageChooseText;
    }

    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        });
        //binding.cocktailImage.setOnClickListener(v->chooseImage());
        binding.publishRecipe.setOnClickListener(v->publish());
        //binding.imageBack.setOnClickListener(v->finish());
    }

    private void publish(){
        if(!cocktailName.getText().toString().isEmpty() && !cocktailRecipe.getText().toString().isEmpty() && encodedImage != null){
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String,Object> cocktail = new HashMap<>();
            cocktail.put(Constants.KEY_COCKTAIL_NAME,cocktailName.getText().toString());
            cocktail.put(Constants.KEY_COCKTAIL_RECIPE,cocktailRecipe.getText().toString());
            cocktail.put(Constants.KEY_COCKTAIL_IMAGE,encodedImage);
            cocktail.put(Constants.KEY_COCKTAIL_VIDEO,null);
            cocktail.put(Constants.KEY_COCKTAIL_RATING,new Integer(0));
            cocktail.put(Constants.KEY_COCKTAIL_CREATOR_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
            database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                    .add(cocktail)
                    .addOnSuccessListener(documentReference -> {
                        Intent i = new Intent(getApplicationContext(),CocktailPageActivity.class);
                        i.putExtra(Constants.KEY_COCKTAIL_ID,documentReference.getId());
                        startActivity(i);
                        finish();
                    }).addOnFailureListener(exception -> {
                        Log.d("FCM",exception.getMessage());
                    });;
        }else {
            //Avelacnel dzent aveli konkret default nkar
        }
    }

    private void chooseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){
            if(result.getData() != null){
                Uri imageUri = result.getData().getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //binding.cocktailImage.setImageBitmap(bitmap);
                    //binding.imageChooseText.setVisibility(View.GONE);
                    encodedImage = encodeImage(bitmap);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    });
}