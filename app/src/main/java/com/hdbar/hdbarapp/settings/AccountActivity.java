package com.hdbar.hdbarapp.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.activities.MainActivity;
import com.hdbar.hdbarapp.databinding.ActivityAccountBinding;
import com.hdbar.hdbarapp.databinding.ActivityHelpAndSupportBinding;
import com.hdbar.hdbarapp.databinding.ActivityLanguagesBinding;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;
    private TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
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