package com.hdbar.hdbarapp.activities;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.ContentResolver;
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

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.FragmentCreateCocktailBinding;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreateCocktailFragment extends Fragment {

    private FragmentCreateCocktailBinding binding;

    private PreferenceManager preferenceManager;

    private EditText cocktailName;
    private EditText cocktailRecipe;
    private Integer PICK_IMAGE_MULTIPLE = 1;

    private FirebaseFirestore database;

    private List<String> cocktailTags;
    private List<String> cocktailTagsFinal;
    private ArrayList<String> tags = new ArrayList<>();

    private ArrayList<Uri> imageUri = new ArrayList<>();
    private ArrayList<String> finalImages = new ArrayList<>();
    private ArrayList<SlideModel> slideModels = new ArrayList<>();
    private StorageReference storage;


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
        binding.imageSlider.setOnClickListener(v->{
            chooseImage();
        });
        binding.publishRecipe.setOnClickListener(v->publish());
    }

    private void init(){
        preferenceManager = new PreferenceManager(getActivity());
        cocktailName = binding.cocktailName;
        cocktailRecipe = binding.cocktailRecipe;
        storage = FirebaseStorage.getInstance().getReference("cocktails");
        database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_TAGS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot snapshot:queryDocumentSnapshots){
                            tags.add(snapshot.get(Constants.KEY_TAG_NAME).toString());
                        }
                        ArrayAdapter<String> tagArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, tags);
                        binding.tagsACTV.setAdapter(tagArrayAdapter);
                        binding.tagsACTV.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                    }
                });
    }

    private void publish(){
        if(!cocktailName.getText().toString().isEmpty() && !cocktailRecipe.getText().toString().isEmpty() && imageUri != null && !binding.tagsACTV.getText().toString().isEmpty()){
            binding.publishRecipe.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            for(int i=0;i<imageUri.size();i++){
                StorageReference reference = storage.child(System.currentTimeMillis()+"."+getFileExtension(imageUri.get(i)));
                reference.putFile(imageUri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        finalImages.add(taskSnapshot.getMetadata().getPath());
                        if(finalImages.size()==imageUri.size()){
                            String tags = binding.tagsACTV.getText().toString();
                            cocktailTags = Arrays.asList(tags.split(","));
                            int changes = -1;
                            while(changes != 0){
                                changes = 0;
                                cocktailTagsFinal = new ArrayList<>();
                                for (int i = 0; i < cocktailTags.size(); i++) {
                                    if (cocktailTags.get(i).startsWith(" ")) {
                                        String a = cocktailTags.get(i).replaceFirst(" ", "");
                                        cocktailTagsFinal.add(a);
                                        changes++;
                                    }
                                }
                                cocktailTags = cocktailTagsFinal;
                            }
                            HashMap<String,Object> cocktail = new HashMap<>();
                            cocktail.put(Constants.KEY_COCKTAIL_NAME,cocktailName.getText().toString());
                            cocktail.put(Constants.KEY_COCKTAIL_RECIPE,cocktailRecipe.getText().toString());
                            cocktail.put(Constants.KEY_COCKTAIL_IMAGE,finalImages);
                            cocktail.put(Constants.KEY_COCKTAIL_VIDEO,null);
                            cocktail.put(Constants.KEY_COCKTAIL_TAGS,cocktailTags);
                            cocktail.put(Constants.KEY_COCKTAIL_RATING,new Integer(0));
                            cocktail.put(Constants.KEY_COCKTAIL_HOW_MANY_RATES,new Integer(0));
                            cocktail.put(Constants.KEY_STATUS,Constants.KEY_COCKTAIL_STATUS_PENDING);
                            cocktail.put(Constants.KEY_COCKTAIL_CREATOR_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            cocktail.put(Constants.KEY_COCKTAIL_CREATOR_NAME, preferenceManager.getString(Constants.KEY_USERNAME));
                            database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                                    .add(cocktail)
                                    .addOnSuccessListener(documentReference -> {
                                        binding.progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "Cocktail is waiting to be approved!", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(exception -> {
                                        Log.d("FCM",exception.getMessage());
                                    });

                        }
                    }
                });
            }
        }else{
            Toast.makeText(getActivity(), "Fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImage.launch(Intent.createChooser(intent,"Select Picture"));
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){
            if(result.getData().getClipData() != null){
                imageUri.clear();
                ClipData mClipData = result.getData().getClipData();
                for(int i=0;i<mClipData.getItemCount();i++){
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    imageUri.add(uri);
                    slideModels.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_CROP));
                }
                binding.imageSlider.setImageList(slideModels);
                binding.imageChooseText.setVisibility(View.INVISIBLE);
            }else if(result.getData() != null){
                imageUri.clear();
                Uri uri = result.getData().getData();
                imageUri.add(uri);
                slideModels.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_CROP));
                binding.imageSlider.setImageList(slideModels);
                binding.imageChooseText.setVisibility(View.INVISIBLE);
            }
        }
    });

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}