package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.ActivityMainBinding;
import com.hdbar.hdbarapp.utilities.Constants;
import com.hdbar.hdbarapp.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    private String userName;
    private String userEmail;
    private String userImage;

    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());

        init();
        listeners();
        bottomNavigationBar();

        AlwaysOnRun.AlwaysRun(MainActivity.this);
    }

    private void createNotify(){
        String id = "my_idd";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = manager.getNotificationChannel(id);
            if (channel == null){
                channel = new NotificationChannel(id, "channel Title", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("inchvor description");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,200,300,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent = new Intent(this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(null)
                .setContentTitle("Title")
                .setContentText("Your text description")
                .setVibrate(new long[]{100,200,300,340})
                .setAutoCancel(false)
                .setTicker("Notification");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m = NotificationManagerCompat.from(getApplicationContext());
        m.notify(1,builder.build());
    }

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