package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hdbar.hdbarapp.adapters.ModeratorAdapter;
import com.hdbar.hdbarapp.databinding.ActivityModeratorsBinding;
import com.hdbar.hdbarapp.models.User;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class ModeratorsActivity extends AppCompatActivity {

    private ActivityModeratorsBinding binding;
    private FirebaseFirestore database;
    private List<User> users;
    private ModeratorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModeratorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
    }

    private void init(){
        users = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
        adapter = new ModeratorAdapter(users);
        binding.moderatorsRecyclerView.setAdapter(adapter);
        updateRecyclerView();
    }

    private void listeners(){
        binding.reload.setOnClickListener(v->{
            updateRecyclerView();
        });
    }


    private void updateRecyclerView(){
        binding.moderatorsRecyclerView.setVisibility(View.INVISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setVisibility(View.INVISIBLE);
        getModerators();
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!users.isEmpty()) {
                    adapter.notifyDataSetChanged();
                    binding.moderatorsRecyclerView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }else {
                    binding.moderatorsRecyclerView.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.textErrorMessage.setVisibility(View.VISIBLE);
                }
            }
        }, 500);
    }

    private void getModerators(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.collection(Constants.KEY_COLLECTION_USERS)
                        .whereEqualTo(Constants.KEY_STATUS,Constants.KEY_STATUS_MODERATOR)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    users.clear();
                                    for(DocumentSnapshot document: task.getResult()){
                                        User a = new User();
                                        a.uid = document.getId();
                                        a.name = document.getString(Constants.KEY_USERNAME);
                                        a.email = document.getString(Constants.KEY_EMAIL);
                                        a.status = document.getString(Constants.KEY_STATUS);
                                        a.image = document.getString(Constants.KEY_USER_IMAGE);
                                        users.add(a);
                                    }
                                }else {
                                    Log.d("FCM",task.getException().getMessage());
                                }
                            }
                        });
            }
        }).start();
    }
}