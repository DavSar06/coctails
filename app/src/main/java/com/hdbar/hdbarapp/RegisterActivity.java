package com.hdbar.hdbarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hdbar.hdbarapp.databinding.ActivityRegisterBinding;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private PreferenceManager preferenceManager;
    private EditText inputEmail,inputPassword,inputConformPassword;
    private TextView confirmButton;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        inputEmail=findViewById(R.id.register_email);
        inputPassword=findViewById(R.id.register_password);
        inputConformPassword=findViewById(R.id.register_password_confirm);
        confirmButton=findViewById(R.id.register_confirm);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerforAuth();
            }
        });


        setListeners();
    }

    private void PerforAuth() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String passwordConfirm = inputConformPassword.getText().toString();

        if(!email.matches(emailPattern)){
            inputEmail.setError("Enter Conntext Email");
        }else if(password.isEmpty() || password.length()<6){
            inputEmail.setError("Enter Proper Password");
        }else if(!password.equals(passwordConfirm)){
            inputEmail.setError("Password Not match Both field  ");
        }else{
            //can be a problem with progress dialog
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void setListeners(){
        binding.registerIconChooseIconToChange.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),ChooseImageActivity.class)));
    }
}