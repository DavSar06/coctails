package com.drini.driniapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.drini.driniapp.databinding.ActivityNotificationsBinding;
import com.drini.driniapp.utilities.AlwaysOnRun;

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