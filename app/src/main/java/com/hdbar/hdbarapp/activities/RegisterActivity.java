package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityRegisterBinding;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private PreferenceManager preferenceManager;
    private EditText inputName,inputEmail,inputConformPassword;
    TextInputEditText inputPassword;
    private TextView confirmButton;
    private TextInputLayout textInputLayout;
    private CheckBox areYouOlder18;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog progressDialog;


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toast.makeText(RegisterActivity.this,"password", Toast.LENGTH_SHORT).show();
        init();
        listeners();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        inputEmail=binding.registerEmail;
        inputPassword=binding.registerPassword;
        inputConformPassword=binding.registerPasswordConfirm;
        confirmButton=binding.registerConfirm;
        areYouOlder18=binding.areYouOlder18;
        inputName=binding.registerUsername;
        textInputLayout = binding.registerPasswordLayout;
        progressDialog = new ProgressDialog(RegisterActivity.this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }

    private void PerformAuth() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString().trim();
        Toast.makeText(RegisterActivity.this,password, Toast.LENGTH_SHORT);
        String passwordConfirm = inputConformPassword.getText().toString();
        String name = inputName.getText().toString();

        if(!email.matches(emailPattern) || email.isEmpty()){
            inputEmail.setError("Enter Context Email");
        }else if(password.isEmpty() || password.length()<6){
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setErrorIconDrawable(0);
            textInputLayout.setError(" ");
        }else if(!password.isEmpty() || password.length()>6){
            textInputLayout.setErrorEnabled(false);
        }else if(!password.equals(passwordConfirm) ){
            inputConformPassword.setError("Password Confirm Doesn't Match");
        }else if(!areYouOlder18.isChecked()){
            areYouOlder18.setError("Confirm Your Age");
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

    private void listeners(){
        // texapoxel settings binding.registerIconChooseIconToChange.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),ChooseImageActivity.class)));
        binding.registerConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAuth();
            }
        });
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        });
        binding.registerCancel.setOnClickListener(v->finish());
    }
}