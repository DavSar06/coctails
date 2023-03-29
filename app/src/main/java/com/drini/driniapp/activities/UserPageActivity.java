package com.drini.driniapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.drini.driniapp.databinding.ActivityUserPageBinding;
import com.drini.driniapp.utilities.Constants;

public class UserPageActivity extends AppCompatActivity {

    private ActivityUserPageBinding binding;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uid = getIntent().getStringExtra(Constants.KEY_USER_UID);

        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();
    }
}