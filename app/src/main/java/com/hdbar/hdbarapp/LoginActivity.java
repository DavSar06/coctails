package com.hdbar.hdbarapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.hdbar.hdbarapp.databinding.ActivityLoginBinding;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;
    TextView registerTextView;


    private EditText inputEmail,inputPassword;
    private ImageView btnGoogle,btnTwitter,btnGithub,btnFacebook;
    private TextView buttonLogin;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog progressDialog;


    public static final int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //---binding's-------------------------

        inputEmail=binding.Login;
        inputPassword=binding.Password;
        buttonLogin=binding.Loginbtn;
        btnFacebook=binding.facebook;
        btnGithub=binding.github;
        btnGoogle=binding.google;
        btnTwitter=binding.twitter;
        registerTextView = binding.registerMain;

        //--------------------------------------



        progressDialog = new ProgressDialog(LoginActivity.this);
        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();


        //--------------On Clicks----------------
        listeners();
        //----------------------------------------
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }

    private void  requestGoogleSignIn(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this,gso);


    }

    private void firebaseAuthWithGoogle(String idToken) {

        //getting user credentials with the help of AuthCredential method and also passing user Token Id.
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        //trying to sign in user using signInWithCredential and passing above credentials of user.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,"Authrntication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

                preferenceManager.putString(Constants.KEY_USERNAME,account.getDisplayName());
                preferenceManager.putString(Constants.KEY_EMAIL,account.getEmail());
                preferenceManager.putString(Constants.KEY_USER_IMAGE,account.getPhotoUrl().toString());


                Toast.makeText(LoginActivity.this,"Sign in Complete ", Toast.LENGTH_SHORT).show();
            }catch (ApiException e){

                Toast.makeText(LoginActivity.this,"Authrntication Failed Poblems with "  + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void perforLogin() {

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if(!email.matches(emailPattern)){
            inputEmail.setError("Enter Conntext Email");
        }else if(password.trim().isEmpty()){
            inputEmail.setError("Enter Proper Password"); // input password poxel
        }else if(password.length()<6){
            inputEmail.setError("Enter Proper Password"); // input password poxel
        }else{
            //can be a problem with progress dialog
            progressDialog.setMessage("Pleas Wait For Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();

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

    private void switchActivities(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void listeners(){
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perforLogin();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        requestGoogleSignIn();
    }
}