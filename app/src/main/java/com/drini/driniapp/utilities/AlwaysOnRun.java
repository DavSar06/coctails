package com.drini.driniapp.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.drini.driniapp.R;

public class AlwaysOnRun {

    public static void AlwaysRun(Activity myActivityReference) {
        View decorView = myActivityReference.getWindow().getDecorView();/*
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);*/


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myActivityReference.getWindow().setNavigationBarColor(myActivityReference.getResources().getColor(R.color.background_color));
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
}
