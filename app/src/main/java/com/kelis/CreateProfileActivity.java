package com.kelis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import static com.kelis.SignUpActivity.INTENT_USER_ID;

public class CreateProfileActivity extends AppCompatActivity {

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra(INTENT_USER_ID);
        }
    }


}
