package com.drini.driniapp.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.drini.driniapp.R;
import com.drini.driniapp.activities.AnimationActivity;
import com.drini.driniapp.utilities.AlwaysOnRun;
import com.drini.driniapp.databinding.ActivityLanguagesBinding;
import com.drini.driniapp.utilities.Constants;
import com.drini.driniapp.utilities.LanguageController;
import com.drini.driniapp.utilities.PreferenceManager;

public class LanguagesActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String currentLang;
    private ActivityLanguagesBinding binding;
    private TextView back;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        AlwaysOnRun.AlwaysRun(LanguagesActivity.this);



        init();
        listeners();
    }

    private void init(){
        progressDialog = new ProgressDialog(getApplicationContext());
        preferenceManager = new PreferenceManager(getApplicationContext());
        currentLang = preferenceManager.getString(Constants.KEY_LANGUAGE);
        if(currentLang.equals("")){
            binding.english.setEnabled(false);
            binding.englishTick.setVisibility(View.VISIBLE);
        }
        if(currentLang.equals("ru")){
            binding.russian.setEnabled(false);
            binding.russianTick.setVisibility(View.VISIBLE);
        }
    }

    private void listeners(){
        binding.backAboutUs.setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.left_to_right_in,R.anim.left_to_right_out);
        });
        binding.english.setOnClickListener(v->{
            binding.englishTick.setVisibility(View.VISIBLE);
            binding.russianTick.setVisibility(View.INVISIBLE);
            LanguageController.setLocale("",getBaseContext());
            goToMain();
        });
        binding.russian.setOnClickListener(v->{
            binding.englishTick.setVisibility(View.INVISIBLE);
            binding.russianTick.setVisibility(View.VISIBLE);
            LanguageController.setLocale("ru",getBaseContext());
            goToMain();
        });
    }

    private void goToMain(){
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), AnimationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        },100);
    }
}