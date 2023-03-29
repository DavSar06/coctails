package com.drini.driniapp.utilities;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageController {

    public static void setLocale(String language,Context context){
        PreferenceManager preferenceManager = new PreferenceManager(context);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
        preferenceManager.putString(Constants.KEY_LANGUAGE,language);
    }

    public static void loadLocale(Context context){
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String language = preferenceManager.getString(Constants.KEY_LANGUAGE);
        setLocale(language,context);
    }
}
