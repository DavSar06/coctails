package com.hdbar.hdbarapp.activities;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdbar.hdbarapp.databinding.FragmentCreateCocktailBinding;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateCocktailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateCocktailFragment extends Fragment {

    private FragmentCreateCocktailBinding binding;

    private PreferenceManager preferenceManager;

    private ImageView cocktailImage;
    private TextView pickImageButton;
    private TextView imageChooseText;
    private EditText cocktailName;
    private EditText cocktailRecipe;
    private Integer SELECT_PICTURE = 200;
    private String encodedImage;


    public CreateCocktailFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static CreateCocktailFragment newInstance() {
        CreateCocktailFragment fragment = new CreateCocktailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCreateCocktailBinding.inflate(getLayoutInflater());

        init();
        listeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        });
        binding.cocktailImage.setOnClickListener(v->{
            chooseImage();
        });
        binding.publishRecipe.setOnClickListener(v->publish());
    }

    private void init(){
        preferenceManager = new PreferenceManager(getActivity());
        cocktailName = binding.cocktailName;
        cocktailRecipe = binding.cocktailRecipe;
        cocktailImage = binding.cocktailImage;
        imageChooseText = binding.imageChooseText;
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
            cocktail.put(Constants.KEY_COCKTAIL_HOW_MANY_RATES,new Integer(0));
            cocktail.put(Constants.KEY_COCKTAIL_STATUS,Constants.KEY_COCKTAIL_STATUS_PENDING);
            cocktail.put(Constants.KEY_COCKTAIL_CREATOR_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
            cocktail.put(Constants.KEY_COCKTAIL_CREATOR_NAME, FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                    .add(cocktail)
                    .addOnSuccessListener(documentReference -> {
                        Intent i = new Intent(getActivity(),CocktailPageActivity.class);
                        i.putExtra(Constants.KEY_COCKTAIL_ID,documentReference.getId());
                        startActivity(i);
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
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    binding.cocktailImage.setImageBitmap(bitmap);
                    binding.imageChooseText.setVisibility(View.GONE);
                    encodedImage = encodeImage(bitmap);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    });
}