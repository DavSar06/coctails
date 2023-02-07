package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.adapters.CocktailsAdapter;
import com.hdbar.hdbarapp.databinding.ActivityMainBinding;
import com.hdbar.hdbarapp.databinding.FragmentSettingsBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    private View decorView;
    private String userName;
    private String userEmail;
    private String userImage;
    public static boolean moderateActivity = false;

    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listeners();
        bottomNavigationBar();

                    View decorView = getWindow().getDecorView();
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color_light));
        }

    }
/*

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    public int hideSystemBars(){
        return */
/*View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN   |*//*

                View.SYSTEM_UI_FLAG_FULLSCREEN  |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

    }
*/

    private void bottomNavigationBar(){

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    Log.d("In","1");
                    break;
                case R.id.custom:
                    replaceFragment(new CreateCocktailFragment());
                    Log.d("In","2");
                    break;
                case R.id.favorite:
                    replaceFragment(new FavoriteFragment());
                    Log.d("In","3");
                    break;
                case  R.id.settings:
                    replaceFragment(new SettingsFragment());
                    Log.d("In","4");
                    break;
                case R.id.moderator:
                    replaceFragment(new ModeratePageFragment());
                    Log.d("In","5");
                    break;
            }

            return true;
        });
    }


    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment).commit();
        moderateActivity = false;
    }

    private void init(){
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        userName = preferenceManager.getString(Constants.KEY_USERNAME);
        userEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        userImage = preferenceManager.getString(Constants.KEY_USER_IMAGE);
        fragmentContainer = binding.fragmentContainer;

        if(moderateActivity){
            replaceFragment(new ModeratePageFragment());
            binding.bottomNavigationView.findViewById(R.id.moderator).performClick();
        } else{
            replaceFragment(new HomeFragment());
        }
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        database.collection(Constants.KEY_COLLECTION_MODERATORS)
//                .document(userId)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if(documentSnapshot.get(Constants.KEY_MODERATOR_STATUS).equals("moderator") || documentSnapshot.get(Constants.KEY_MODERATOR_STATUS).equals("administrator")){
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