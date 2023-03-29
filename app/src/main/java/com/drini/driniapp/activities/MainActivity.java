package com.drini.driniapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.drini.driniapp.R;
import com.drini.driniapp.databinding.ActivityMainBinding;
import com.drini.driniapp.utilities.Constants;
import com.drini.driniapp.utilities.LanguageController;
import com.drini.driniapp.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    private String userName;
    private String userEmail;
    private String userImage;

    private FrameLayout fragmentContainer;

    public static boolean openSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageController.loadLocale(getBaseContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());

        if(openSettings){
            replaceFragment(new SettingsFragment());
        }

        init();
        listeners();
        bottomNavigationBar();
        AlwaysRu(this);
    }

    public static void AlwaysRu(Activity myActivityReference) {
        View decorView = myActivityReference.getWindow().getDecorView();/*
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);*/


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myActivityReference.getWindow().setNavigationBarColor(myActivityReference.getResources().getColor(R.color.background_color_light));
            Window window = myActivityReference.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(myActivityReference.getResources().getColor(R.color.background_color));
        }


        ConnectivityManager cm = (ConnectivityManager) myActivityReference.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null || !info.isConnected())
            Log.d("Ine", "NoConnection");
        else {
            Log.e("Ine", "Connected");    }



        Log.d("TAG","AlwaysOnRun");
    }

    private void bottomNavigationBar(){

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.custom:
                    replaceFragment(new CreateCocktailFragment());
                    break;
                case R.id.favorite:
                    replaceFragment(new FavoriteFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;
            }

            return true;
        });
    }


    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment).commit();
    }

    private void init(){
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        userName = preferenceManager.getString(Constants.KEY_USERNAME);
        userEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        userImage = preferenceManager.getString(Constants.KEY_USER_IMAGE);
        fragmentContainer = binding.fragmentContainer;


//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        database.collection(Constants.KEY_COLLECTION_USERS)
//                .document(userId)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if(documentSnapshot.get(Constants.KEY_STATUS).equals("moderator") || documentSnapshot.get(Constants.KEY_STATUS).equals("administrator")){
//                            binding.bottomNavigationView.findViewById(R.id.moderator).setVisibility(View.VISIBLE);
//                            binding.bottomNavigationView.findViewById(R.id.moderator).setEnabled(true);
//                        }
//                    }
//                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu,menu);
        return true;
    }

    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        });
    }
}