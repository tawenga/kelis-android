package com.kelis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {
    EditText mPhoneNumberEditText;
    EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mPhoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
    }
}
