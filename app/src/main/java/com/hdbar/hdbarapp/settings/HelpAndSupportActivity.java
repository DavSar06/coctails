package com.hdbar.hdbarapp.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.activities.MainActivity;
import com.hdbar.hdbarapp.databinding.ActivityAboutUsBinding;
import com.hdbar.hdbarapp.databinding.ActivityHelpAndSupportBinding;

public class HelpAndSupportActivity extends AppCompatActivity {


    private ActivityHelpAndSupportBinding binding;
    private TextView back,email,chat_to_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpAndSupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        back = binding.backAboutUs;
        email = binding.emailHelpAndSupport;
        chat_to_us = binding.chatToUsHelpAndSupport;

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        chat_to_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+GjIdcEIbjC43NDcy"));
                startActivity(intent);
            }
        });
    }

    protected void sendEmail() {
        Log.i("Send email", "");

        String[] TO = {"hddevelopers0@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(HelpAndSupportActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}