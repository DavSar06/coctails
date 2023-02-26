package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityEmailConfirmBinding;
import com.hdbar.hdbarapp.databinding.ActivityRegisterBinding;
import com.hdbar.hdbarapp.models.User;

public class EmailConfirmActivity extends AppCompatActivity {

    private ActivityEmailConfirmBinding binding;

    private TextView resendCode,confirm_btn,change_email;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
        AlwaysOnRun.AlwaysRun(this);

    }

    public void resendEmail(){
        Toast.makeText(EmailConfirmActivity.this, "click", Toast.LENGTH_SHORT).show();

        fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EmailConfirmActivity.this, "Verification Email Has been Sent" + fUser, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Tag", "onFailure: Email not sent " + e.getMessage());
            }
        });
    }

    public void confirmBtn(){
        Toast.makeText(EmailConfirmActivity.this, "click", Toast.LENGTH_SHORT).show();
        if (mAuth.getCurrentUser().isEmailVerified()){
            Toast.makeText(EmailConfirmActivity.this, "verifid", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(EmailConfirmActivity.this, "unverifid", Toast.LENGTH_SHORT).show();
        }
    }

    public void goBack(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

    public void init(){
        resendCode = binding.resendCodeConfirm;
        confirm_btn = binding.donateBtn;
        change_email =binding.changeEmailConfirm;
        mAuth=FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
    }

    private void listeners(){
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set cool down
                resendEmail();
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmBtn();
            }
        });

        change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

    }
}