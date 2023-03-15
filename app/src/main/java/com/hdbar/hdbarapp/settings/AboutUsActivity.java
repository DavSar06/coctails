package com.hdbar.hdbarapp.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.activities.HomeFragment;
import com.hdbar.hdbarapp.activities.MainActivity;
import com.hdbar.hdbarapp.activities.SettingsFragment;
import com.hdbar.hdbarapp.databinding.ActivityAboutUsBinding;
import com.hdbar.hdbarapp.databinding.ActivityMainBinding;
import com.hdbar.hdbarapp.utilities.AlwaysOnRun;

public class AboutUsActivity extends AppCompatActivity {

    private ActivityAboutUsBinding binding;
    private TextView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        back = binding.backAboutUs;
        AlwaysOnRun.AlwaysRun(this);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}