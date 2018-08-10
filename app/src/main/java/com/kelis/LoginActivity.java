package com.kelis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mPhoneNumberEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                login(phoneNumber, password);
            }
        });

        findViewById(R.id.new_account_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, EnterPhoneActivity.class));
            }
        });
    }

    public void login(String phoneNumber, String password){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}
