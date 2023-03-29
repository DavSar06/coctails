package com.hdbar.hdbarapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityChooseImageBinding;
import com.hdbar.hdbarapp.utilities.AlwaysOnRun;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;


public class ChooseImageActivity extends AppCompatActivity {

    private ActivityChooseImageBinding binding;

    private Uri imageUri;
    private StorageReference storage;
    private FirebaseFirestore database;
    private ArrayList<ImageView> imageViews;
    private ArrayList<Uri> images;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();
        setListeners();
        AlwaysOnRun.AlwaysRun(this);
    }

    private void init(){
        imageViews = new ArrayList<>();
        images = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference("users");
        uid = FirebaseAuth.getInstance().getUid();
        setCurrentImage();
        getImagesContainers();
        for (int i=0;i<18;i++){
            int finalI = i;
            imageViews.get(i).setOnClickListener(v->{
                uploadImage(images.get(finalI));
            });
        }
    }

    private void getImagesContainers(){
        Resources resources = getResources();
        imageViews.add(binding.icon1);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon1))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon1))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon1))
                .build());
        imageViews.add(binding.icon2);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon13))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon13))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon13))
                .build());
        imageViews.add(binding.icon3);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon4))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon4))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon4))
                .build());
        imageViews.add(binding.icon4);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon6))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon6))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon6))
                .build());
        imageViews.add(binding.icon5);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon7))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon7))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon7))
                .build());
        imageViews.add(binding.icon6);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon17))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon17))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon17))
                .build());
        imageViews.add(binding.icon7);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon9))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon9))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon9))
                .build());
        imageViews.add(binding.icon8);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon15))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon15))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon15))
                .build());
        imageViews.add(binding.icon9);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon25))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon25))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon25))
                .build());
        imageViews.add(binding.icon10);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon23))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon23))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon23))
                .build());
        imageViews.add(binding.icon11);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon29))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon29))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon29))
                .build());
        imageViews.add(binding.icon12);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon28))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon28))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon28))
                .build());
        imageViews.add(binding.icon13);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon8))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon8))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon8))
                .build());
        imageViews.add(binding.icon14);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon14))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon14))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon14))
                .build());
        imageViews.add(binding.icon15);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon5))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon5))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon5))
                .build());
        imageViews.add(binding.icon16);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon24))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon24))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon24))
                .build());
        imageViews.add(binding.icon17);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon19))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon19))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon19))
                .build());
        imageViews.add(binding.icon18);
        images.add(new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.cocktail_icon22))
                .appendPath(resources.getResourceTypeName(R.drawable.cocktail_icon22))
                .appendPath(resources.getResourceEntryName(R.drawable.cocktail_icon22))
                .build());
    }

    public void setListeners(){
        binding.imageBack.setOnClickListener(v->finish());
        binding.currentImage.setOnClickListener(v->finish());
        binding.addYourImage.setOnClickListener(v->{
            chooseImage();
        });
    }

    private void setCurrentImage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        storage.getReference(documentSnapshot.get(Constants.KEY_USER_IMAGE).toString()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Glide.with(binding.currentImage).load(task.getResult()).into(binding.currentImage);
                            }
                        });
                    }
                });
    }

    private void chooseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){
            if(result.getData() != null){
                imageUri = result.getData().getData();
                uploadImage(imageUri);
            }
        }
    });

    private void uploadImage(Uri imageUri){
        StorageReference reference = storage.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                database.collection(Constants.KEY_COLLECTION_USERS).document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            storage.child(task.getResult().get(Constants.KEY_USER_IMAGE).toString().replace("users/","")).delete();
                            database.collection(Constants.KEY_COLLECTION_USERS).document(uid).update(Constants.KEY_USER_IMAGE,taskSnapshot.getMetadata().getPath());
                            MainActivity.openSettings = true;
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else {
                            Toast.makeText(ChooseImageActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}