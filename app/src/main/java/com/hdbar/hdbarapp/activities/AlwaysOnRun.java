package com.hdbar.hdbarapp.activities;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hdbar.hdbarapp.R;

public class AlwaysOnRun {

    public static void AlwaysRun(Activity myActivityReference) {
        View decorView = myActivityReference.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myActivityReference.getWindow().setNavigationBarColor(myActivityReference.getResources().getColor(R.color.background_color_light));
            Window window = myActivityReference.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(myActivityReference.getResources().getColor(R.color.background_color));
        }

        Log.d("TAG","AlwaysOnRun");
    }
}
