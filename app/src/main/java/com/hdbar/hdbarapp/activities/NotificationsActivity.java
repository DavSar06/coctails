package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hdbar.hdbarapp.databinding.ActivityNotificationsBinding;
import com.hdbar.hdbarapp.utilities.AlwaysOnRun;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
        AlwaysOnRun.AlwaysRun(this);
    }

    private void init(){

    }

    private void listeners(){
        binding.icBack.setOnClickListener(v->{
            finish();
        });
    }
}