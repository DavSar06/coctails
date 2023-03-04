package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
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
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        listeners();

        if(mAuth.getCurrentUser().isEmailVerified()){
            nextActivity();
        }

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

    public void init(){
        mAuth=FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
    }

    private void listeners(){
        binding.resendCodeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set cool down
                resendEmail();
            }
        });

        binding.checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.getCurrentUser().reload();
                if (mAuth.getCurrentUser().isEmailVerified()){
                    Toast.makeText(EmailConfirmActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                    nextActivity();
                }else{
                    Toast.makeText(EmailConfirmActivity.this, "Not Verified", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.signOut.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });

    }

    private void nextActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}