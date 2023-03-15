package com.hdbar.hdbarapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.hdbar.hdbarapp.databinding.ActivityChooseImageBinding;
import com.hdbar.hdbarapp.utilities.AlwaysOnRun;
import com.hdbar.hdbarapp.utilities.Constants;


public class ChooseImageActivity extends AppCompatActivity {

    private ActivityChooseImageBinding binding;

    private Uri imageUri;
    private StorageReference storage;
    private FirebaseFirestore database;
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
        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference("users");
        uid = FirebaseAuth.getInstance().getUid();
        setCurrentImage();
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
                                    finish();
                                }else {
                                    Toast.makeText(ChooseImageActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        }
    });
}