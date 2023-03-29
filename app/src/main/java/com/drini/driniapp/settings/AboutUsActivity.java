package com.drini.driniapp.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.drini.driniapp.R;
import com.drini.driniapp.databinding.ActivityAboutUsBinding;
import com.drini.driniapp.databinding.ActivityMainBinding;
import com.drini.driniapp.utilities.AlwaysOnRun;

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
                overridePendingTransition(R.anim.left_to_right_in,R.anim.left_to_right_out);
            }
        });

    }

}