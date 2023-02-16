package com.hdbar.hdbarapp.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityLoginBinding;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;
    TextView registerTextView;


    private EditText inputEmail,inputPassword;
    private ImageView btnGoogle,btnPhone,btnGithub,btnFacebook;
    private TextView buttonLogin;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog progressDialog;


    public static final int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;

    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessToken accessTokenTracker;
    private FirebaseUser mUser;
    private FirebaseUser user;
    private StorageReference storage;
    private Uri imageUri;
    //private CallbackManager mCallbackManger;
    //private LoginButton loginButton;
    //private static final String TAG ="FacebookAuthentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();
        listeners();
        getNetworkClass(LoginActivity.this);
    }

    private void init(){
        storage = FirebaseStorage.getInstance().getReference("cocktails");
        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        inputEmail=binding.Login;
        inputPassword=binding.Password;
        buttonLogin=binding.Loginbtn;
        //loginButton=binding.loginButton;
        //loginButton.setReadPermissions("email","public_profile");
        btnGoogle=binding.google;
        btnPhone = binding.phoneLogin;
        registerTextView = binding.registerMain;

        //loginButton = findViewById(R.id.facebook);

        progressDialog = new ProgressDialog(LoginActivity.this);
        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        //---------------------------------------
     /*   FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManger = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManger, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG,"onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onCancel" + error);

            }
        });



        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
            }
        };*//*
        accessTokenTracker = new AccessTokenTracker(){
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,AccessToken currentAccessToken){
                if (currentAccessToken == null){
                    //mAuth.signOut();
                }
            }
        };*/
    }


/*

    private void handleFacebookToken(AccessToken token) {
        Log.d(TAG,"handleFacebookToken" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d(TAG,"sign in with credential: successful");
                }else{
                    Log.d(TAG,"sign in with credential: failed", task.getException());
                    Toast.makeText(LoginActivity.this,"Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
*/

/*
    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
*/
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        //mAuth.addAuthStateListener(authStateListener);
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

    private void phoneLogIn(){

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

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String name = task.getResult().getUser().getDisplayName();
                                    String email = task.getResult().getUser().getEmail();
                                    String uid = task.getResult().getUser().getUid();
                                    String userBio = "";


                                    database.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(uid)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        if(!task.getResult().exists()){
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
                                                                    HashMap<String,Object> user = new HashMap<>();
                                                                    user.put(Constants.KEY_USERNAME,name);
                                                                    user.put(Constants.KEY_EMAIL,email);
                                                                    user.put(Constants.KEY_USER_BIO,userBio);
                                                                    user.put(Constants.KEY_USER_IMAGE,taskSnapshot.getMetadata().getPath());
                                                                    user.put(Constants.KEY_STATUS,Constants.KEY_STATUS_USER);

                                                                    database.collection(Constants.KEY_COLLECTION_USERS).document(uid).set(user);
                                                                }
                                                            });
                                                        }
                                                    }else {
                                                        Log.d("FCM",task.getException().getMessage());
                                                    }
                                                }
                                            });


                                }
                            }).start();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,"Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //mCallbackManger.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

                preferenceManager.putString(Constants.KEY_USERNAME,account.getDisplayName());
                preferenceManager.putString(Constants.KEY_EMAIL,account.getEmail());
                preferenceManager.putString(Constants.KEY_USER_IMAGE,account.getPhotoUrl().toString());


                Toast.makeText(LoginActivity.this,"Sign in Complete", Toast.LENGTH_SHORT).show();
            }catch (ApiException e){
                Toast.makeText(LoginActivity.this,"Authentication Failed Problems with "  + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("FCM",e.getMessage());
            }
        }
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void performLogin() {

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if(!email.matches(emailPattern) || password.trim().isEmpty() || password.length()<8){
            if(!email.matches(emailPattern)){
                inputEmail.setError("Enter Existing Email");
            }
            if(password.trim().isEmpty()){
                inputPassword.setError("Password field is empty");
            }else if(password.length()<8){
                inputPassword.setError("Password is short (need more than 8 characters)");
            }
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

                        if(task.getException().getMessage().contains("password")){
                            inputPassword.setError("Wrong password");
                        }else {
                            inputEmail.setError("Wrong Email");
                        }
                    }
                }
            });
        }
    }


    public static String getNetworkClass(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
            Log.d("Ine", "NoConnection");
        else {
            Log.e("Ine", "Connected");    }
        return null;
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void switchActivities(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out);
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
                performLogin();
            }
        });

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //some voide
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        });
        requestGoogleSignIn();
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}