package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.adapters.ModeratorAdapter;
import com.hdbar.hdbarapp.databinding.ActivityModeratorsBinding;
import com.hdbar.hdbarapp.databinding.ListItemBinding;
import com.hdbar.hdbarapp.listeners.DropdownListener;
import com.hdbar.hdbarapp.models.User;
import com.hdbar.hdbarapp.utilities.Constants;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class ModeratorsActivity extends AppCompatActivity {

    private ActivityModeratorsBinding binding;
    private FirebaseFirestore database;
    private List<User> users;
    private ArrayList<String> allUsers;
    private ModeratorAdapter adapter;
    private ArrayAdapter atvAdapter;

    private String id;

    private DropdownListener listener = new DropdownListener() {
        @Override
        public void onItemClick(String text, String id) {
            if(text.equals("Go to page")){
                Intent i = new Intent(getApplicationContext(),UserPageActivity.class);
                i.putExtra(Constants.KEY_USER_UID,id);
                startActivity(i);
            }else {
                AlertDialog a = new AlertDialog.Builder(binding.moderatorsRecyclerView.getContext(),R.style.DialogeTheme)
                        .setTitle("Removing Moderator")
                        .setMessage("Are you sure you want to set this moderator as user?")
                        .setIcon(R.drawable.ic_alert)
                        .setPositiveButton("I'm sure", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                database.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(id)
                                        .update(Constants.KEY_STATUS,Constants.KEY_STATUS_USER)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(), "Moderator Removed Successfully", Toast.LENGTH_SHORT).show();
                                                    database.collection(Constants.KEY_COLLECTION_USERS)
                                                            .document(id)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    String email = task.getResult().get(Constants.KEY_EMAIL).toString();
                                                                    allUsers.add(email);
                                                                    atvAdapter.add(email);
                                                                }
                                                            });
                                                    (new Handler()).postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            updateRecyclerView();
                                                        }
                                                    },500);
                                                }else {
                                                    Log.d("FCM",task.getException().getMessage());
                                                }
                                            }
                                        });
                            }})
                        .setNegativeButton("Cancel", null).create();
                a.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        a.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        a.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    }
                });
                a.show();
            }
        }
    };

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
        allUsers = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
        adapter = new ModeratorAdapter(users,listener);
        atvAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item,allUsers);
        binding.atv.setAdapter(atvAdapter);
        binding.moderatorsRecyclerView.setAdapter(adapter);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_STATUS,Constants.KEY_STATUS_USER)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document: queryDocumentSnapshots){
                            allUsers.add(document.get(Constants.KEY_EMAIL).toString());
                        }
                        atvAdapter.notifyDataSetChanged();
                    }
        });
        updateRecyclerView();
    }

    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        });
        binding.imageBack.setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });
        binding.reload.setOnClickListener(v->{
            updateRecyclerView();
        });
        binding.addModeratorBtn.setOnClickListener(v->{
            String s = binding.atv.getText().toString();
            if(!s.isEmpty() && allUsers.contains(s)){
                AlertDialog a = new AlertDialog.Builder(this,R.style.DialogeTheme)
                        .setTitle("Adding Moderator")
                        .setMessage("Are you sure you want to set this user as moderator?")
                        .setIcon(R.drawable.ic_alert)
                        .setPositiveButton("I'm sure", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                database.collection(Constants.KEY_COLLECTION_USERS)
                                        .whereEqualTo(Constants.KEY_EMAIL,s)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    id = task.getResult().getDocuments().get(0).getId();
                                                    allUsers.remove(s);
                                                    atvAdapter.remove(s);
                                                    binding.atv.setText("");
                                                }else {
                                                    Log.d("FCM",task.getException().getMessage());
                                                }
                                            }
                                        });
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        database.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(id)
                                                .update(Constants.KEY_STATUS,Constants.KEY_STATUS_MODERATOR);
                                        (new Handler()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateRecyclerView();
                                            }
                                        },500);
                                    }
                                },500);
                            }})
                        .setNegativeButton("Cancel", null).create();
                a.show();
            }else{
                Toast.makeText(getApplicationContext(), "There is no such user", Toast.LENGTH_SHORT).show();
            }
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
        }, 1000);

    }

    private void getModerators(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.collection(Constants.KEY_COLLECTION_USERS)
                        .whereNotEqualTo(Constants.KEY_STATUS,Constants.KEY_STATUS_USER)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    users.clear();
                                    for(DocumentSnapshot document: task.getResult()){
                                        if(!document.getId().equals(FirebaseAuth.getInstance().getUid())){
                                            User a = new User();
                                            a.uid = document.getId();
                                            a.name = document.getString(Constants.KEY_USERNAME);
                                            a.email = document.getString(Constants.KEY_EMAIL);
                                            a.status = document.getString(Constants.KEY_STATUS);
                                            a.image = document.getString(Constants.KEY_USER_IMAGE);
                                            users.add(a);
                                        }
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