package com.kelis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;


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

                if(phoneNumber.isEmpty() || phoneNumber.length() < 10){
                    mPhoneNumberEditText.setError("Please type a correct number");
                }else if (password.isEmpty()){
                    mPasswordEditText.setError("Please type a password");;
                } else {
                    // login(phoneNumber, password);
                }
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
        AndroidNetworking.post(App.APP_DOMAIN + "login")
                .addBodyParameter("username", phoneNumber)
                .addBodyParameter("password", password)
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        //if status ==true
                        try {
                            Log.d("LoginActivity", response.get("status").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
}
