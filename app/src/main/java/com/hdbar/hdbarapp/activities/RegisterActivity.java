package com.hdbar.hdbarapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityRegisterBinding;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private PreferenceManager preferenceManager;
    private TextInputEditText inputPassword, inputUsername,inputEmail,inputConformPassword;
    private TextView confirmButton;
    private MaterialCheckBox areYouOlder18;
    private TextInputLayout inputUsernameLayout,inputEmailLayout,inputPasswordLayout,inputConfirmLayout;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog progressDialog;
    private int canStartInit = 0, confirmCode;


    private boolean t = true;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseUser fuser;
    private FirebaseUser user;
    private FirebaseFirestore database;

    private StorageReference storage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();



    }

    private void init(){
        storage = FirebaseStorage.getInstance().getReference("cocktails");
        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        inputEmail=binding.registerEmail;
        inputPassword=binding.registerPassword;
        inputConformPassword=binding.registerPasswordConfirm;
        confirmButton=binding.registerConfirm;
        areYouOlder18=binding.areYouOlder18;
        inputUsername=binding.registerUsername;
        inputPasswordLayout = binding.registerPasswordLayout;
        inputEmailLayout = binding.registerEmailLayout;
        inputUsernameLayout = binding.registerUsernameLayout;
        inputConfirmLayout = binding.registerPasswordConfirmLayout;
        progressDialog = new ProgressDialog(RegisterActivity.this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }

    private void PerformAuth() {
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String passwordConfirm = inputConformPassword.getText().toString().trim();

        int canStartInit = 0;

        //Username
        if (username.isEmpty() || username.length() < 2){
            inputUsernameLayout.setErrorEnabled(true);
            inputUsernameLayout.setErrorIconDrawable(0);
            inputUsernameLayout.setError("Too short username");
            canStartInit ++;
        }else{
            inputUsernameLayout.setErrorEnabled(false);
            inputUsernameLayout.setError(null);
            canStartInit --;
        }

        //Email
        if(!email.matches(emailPattern) || email.isEmpty()){
            inputEmailLayout.setErrorEnabled(true);
            inputEmailLayout.setErrorIconDrawable(0);
            inputEmailLayout.setError("Write an Email");
            canStartInit ++;
        }else {
            inputEmailLayout.setErrorEnabled(false);
            inputEmailLayout.setError(null);
            canStartInit --;
        }

        //Password
        if(password.isEmpty() || password.length()<8){
            inputPasswordLayout.setErrorEnabled(true);
            inputPasswordLayout.setErrorIconDrawable(0);
            inputPasswordLayout.setError("Too short password");
            canStartInit ++;
        }
        else{
            inputPasswordLayout.setErrorEnabled(false);
            inputPasswordLayout.setError(null);
            canStartInit --;
        }

        //Confirm Email
        if(!password.equals(passwordConfirm) || passwordConfirm.isEmpty() ){
            inputConfirmLayout.setErrorEnabled(true);
            inputConfirmLayout.setErrorIconDrawable(0);
            inputConfirmLayout.setError("Password Confirm Doesn't Match");
            canStartInit ++;
        }
        else{
            inputConfirmLayout.setErrorEnabled(false);
            inputConfirmLayout.setError(null);
            canStartInit --;
        }


        if(!areYouOlder18.isChecked()){
            Log.d("GG","UnChecked");
            areYouOlder18.setButtonTintList(ContextCompat.getColorStateList(RegisterActivity.this, R.color.red));
            canStartInit ++;
        }
        else{
            canStartInit --;
            areYouOlder18.setButtonTintList(ContextCompat.getColorStateList(RegisterActivity.this, R.color.white));
        }


        if(canStartInit == -5)
        {
            //can be a problem with progress dialog
            progressDialog.setMessage("Pleas confirm your email...");
            progressDialog.setTitle("Verification");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        // send verification link

                        FirebaseUser fuser = mAuth.getCurrentUser();
                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Tag", "onFailure: Email not sent " + e.getMessage());
                            }
                        });


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Resources resources = getResources();
                                imageUri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                        .authority(resources.getResourcePackageName(R.drawable.profile_icon_type))
                                        .appendPath(resources.getResourceTypeName(R.drawable.profile_icon_type))
                                        .appendPath(resources.getResourceEntryName(R.drawable.profile_icon_type))
                                        .build();
                                StorageReference reference = storage.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
                                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        String uid = task.getResult().getUser().getUid();
                                        String name = task.getResult().getUser().getEmail();
                                        String userBio = "";
                                        String userImageLink = taskSnapshot.getMetadata().getPath();

                                        HashMap<String,Object> user = new HashMap<>();
                                        user.put(Constants.KEY_USERNAME,username);
                                        user.put(Constants.KEY_EMAIL,email);
                                        user.put(Constants.KEY_USER_BIO,userBio);
                                        user.put(Constants.KEY_USER_IMAGE,userImageLink);
                                        user.put(Constants.KEY_STATUS,Constants.KEY_STATUS_USER);

                                        database.collection(Constants.KEY_COLLECTION_USERS).document(uid).set(user);
                                    }
                                });


                            }
                        }).start();

                        if (fuser.isEmailVerified()){
                            progressDialog.dismiss();
                            sendUserToNextActivity();
                        }

                        sendUserToNextActivity();

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
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Random random = new Random();
                confirmCode = random.nextInt(8999) + 1000;
                String url = "";
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegisterActivity.this,"" + response,Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,""  + error,Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("email", inputEmail.getText().toString());
                        params.put("code",String.valueOf(confirmCode));
                        return super.getParams();
                    }
                };*//*
                requestQueue.add(stringRequest);*/
                PerformAuth();
            }
        });
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        });
        binding.registerCancel.setOnClickListener(v->finish());
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}