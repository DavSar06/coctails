package com.hdbar.hdbarapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hdbar.hdbarapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    TextView registertextview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        registertextview = (TextView) findViewById(R.id.register_main);

        registertextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivityes();
            }
        });


    }



    private void switchActivityes(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
;
    }

}