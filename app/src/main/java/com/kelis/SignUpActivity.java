package com.kelis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {
    EditText mPasswordEditText;
    EditText mPasswordConfirmationEditText;
    String mPhoneNumber; //Get from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mPasswordConfirmationEditText = (EditText) findViewById(R.id.password_confirmation_edit_text);

        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPasswordEditText.getText().toString().trim();
                String passwordConfirmation = mPasswordConfirmationEditText.getText().toString().trim();
               // signUp(mPhoneNumber, password);
                signUp("", password);
            }
        });
    }

    public void signUp(String phoneNumber, String password){
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
    }
}
