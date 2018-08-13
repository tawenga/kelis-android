package com.kelis;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class App extends Application {

    public static String APP_DOMAIN = "https://arcane-reef-22647.herokuapp.com/api/";
    public static StorageReference mStorageRef;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }
}
