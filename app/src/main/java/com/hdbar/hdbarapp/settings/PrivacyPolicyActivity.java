package com.hdbar.hdbarapp.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.activities.MainActivity;
import com.hdbar.hdbarapp.databinding.ActivityLanguagesBinding;
import com.hdbar.hdbarapp.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private ActivityPrivacyPolicyBinding binding;
    private TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        back = binding.backAboutUs;


        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://publuu.com/flip-book/98200/268888"));
        startActivity(intent);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}