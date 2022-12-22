package com.hdbar.hdbarapp;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("onCreate: OH MY", String.valueOf(LOG));
    }
}