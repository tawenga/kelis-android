package com.kelis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sinch.verification.Logger;
import com.sinch.verification.SinchVerification;


public class EnterPhoneActivity extends AppCompatActivity {
    public static final String INTENT_PHONE_NUMBER = "PHONE_NUMBER";
    private EditText mPhoneNumberEditText;
    private String phoneNumber;

    static {
        // Provide an external logger
        SinchVerification.setLogger(new Logger() {
            @Override
            public void println(int priority, String tag, String message) {
                // forward to logcat
                android.util.Log.println(priority, tag, message);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter_phone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPhoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);

        findViewById(R.id.verify_phone_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = mPhoneNumberEditText.getText().toString().trim();
                if(phoneNumber.isEmpty() || phoneNumber.length() < 10){
                    mPhoneNumberEditText.setError("Please type a correct number");
                }else {
                    String internationalizedPhoneNumber = formatPhoneNumber(phoneNumber);
                    goToVerifyPhone(internationalizedPhoneNumber);

                }
            }
        });
    }

    private void goToVerifyPhone(String phoneNumber) {
        Intent verification = new Intent(this, VerifyPhoneActivity.class);
        verification.putExtra(INTENT_PHONE_NUMBER, phoneNumber);
        startActivity(verification);
    }

    private String formatPhoneNumber(String phoneNumber){
        String phoneNumberWithoutBegginingZero = phoneNumber.substring(1, phoneNumber.length());
        return "+254" + phoneNumberWithoutBegginingZero;
    }
}

