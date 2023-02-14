package com.hdbar.hdbarapp.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.activities.ChooseImageActivity;
import com.hdbar.hdbarapp.activities.MainActivity;
import com.hdbar.hdbarapp.databinding.ActivityAccountBinding;
import com.hdbar.hdbarapp.databinding.ActivityHelpAndSupportBinding;
import com.hdbar.hdbarapp.databinding.ActivityLanguagesBinding;
import com.hdbar.hdbarapp.utilities.Constants;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;
    private TextView back;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





        init();
        listeners();
    }

    public void init(){
        database = FirebaseFirestore.getInstance();
        back = binding.backAboutUs;
        setUserData();
    }

    private void setUserData(){
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            binding.accountNickname.setHint(task.getResult().get(Constants.KEY_USERNAME).toString());
                            binding.accountType.setText("User type: "+task.getResult().get(Constants.KEY_STATUS).toString().toUpperCase());
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReference(task.getResult().get(Constants.KEY_USER_IMAGE).toString()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Glide.with(binding.userImage).load(task.getResult()).into(binding.userImage);
                                }
                            });
                        }
                        else {
                            Log.e("Exception", task.getException().getMessage());
                        }
                    }
                });
    }

    public void listeners(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChooseImageActivity.class);
                startActivity(intent);
            }
        });
    }
}