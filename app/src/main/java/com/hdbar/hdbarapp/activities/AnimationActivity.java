package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.utilities.AlwaysOnRun;

public class AnimationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);


        MotionLayout motionLayout = findViewById(R.id.motionLayout);

        motionLayout.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }
        });

        AlwaysRu(this);
    }

    public static void AlwaysRu(Activity myActivityReference) {
        View decorView = myActivityReference.getWindow().getDecorView();/*
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);*/


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myActivityReference.getWindow().setNavigationBarColor(myActivityReference.getResources().getColor(R.color.black));
            Window window = myActivityReference.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(myActivityReference.getResources().getColor(R.color.black));
        }


        ConnectivityManager cm = (ConnectivityManager) myActivityReference.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null || !info.isConnected())
            Log.d("Ine", "NoConnection");
        else {
            Log.e("Ine", "Connected");    }



        Log.d("TAG","AlwaysOnRun");
    }
}