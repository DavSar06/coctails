package com.hdbar.hdbarapp.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.activities.MainActivity;
import com.hdbar.hdbarapp.databinding.ActivityAboutUsBinding;
import com.hdbar.hdbarapp.databinding.ActivityHelpAndSupportBinding;

public class HelpAndSupportActivity extends AppCompatActivity {


    private ActivityHelpAndSupportBinding binding;
    private TextView back;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpAndSupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        back = binding.backAboutUs;



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}