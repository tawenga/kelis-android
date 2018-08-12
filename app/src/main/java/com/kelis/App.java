package com.kelis;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class App extends Application {

    public static String APP_DOMAIN = "https://arcane-reef-22647.herokuapp.com/api/";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
