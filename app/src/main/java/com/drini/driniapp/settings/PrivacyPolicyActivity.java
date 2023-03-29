package com.drini.driniapp.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.drini.driniapp.R;
import com.drini.driniapp.databinding.ActivityLanguagesBinding;
import com.drini.driniapp.databinding.ActivityPrivacyPolicyBinding;

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
                overridePendingTransition(R.anim.left_to_right_in,R.anim.left_to_right_out);
            }
        });
    }
}