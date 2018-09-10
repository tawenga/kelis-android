package com.kelis;

import android.app.Application;
import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class App extends Application {

    public static String APP_DOMAIN = "https://arcane-reef-22647.herokuapp.com/api/";
    public static StorageReference mStorageRef;
    public static DatabaseReference mDatabaseRef;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        AndroidNetworking.initialize(getApplicationContext());
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

}
