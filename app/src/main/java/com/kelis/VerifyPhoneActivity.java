package com.kelis;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sinch.verification.Config;
import com.sinch.verification.InitiationResult;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import java.util.ArrayList;
import java.util.List;

public class VerifyPhoneActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "VerifyPhoneActivity";

    private static final String APPLICATION_KEY = "977c2ea9-9060-418e-ac82-f29471a7a977";
    private static final int REQUEST_CODE = 1234;

    private Verification mVerification;
    private boolean mIsVerified;
    private String mPhoneNumber;
    ProgressDialog progressDialog;
    private static final String[] SMS_PERMISSIONS = { Manifest.permission.INTERNET,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        /*Intent intent = getIntent();
        if (intent != null) {
            mPhoneNumber = intent.getStringExtra(EnterPhoneActivity.INTENT_PHONE_NUMBER);
            requestPermissions();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
*/
        findViewById(R.id.verify_input_code_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
                /*String code = ((EditText) findViewById(R.id.input_code_edit_text)).getText().toString();
                if (!code.isEmpty()) {
                    if (mVerification != null) {
                        mVerification.verify(code);
                        showProgress();
                    }
                }*/
            }
        });
    }

    private void requestPermissions() {
        List<String> missingPermissions;
        String methodText;

        missingPermissions = getMissingPermissions(SMS_PERMISSIONS);

        if (missingPermissions.isEmpty()) {
            verify();
        } else {
            if (needPermissionsRationale(missingPermissions)) {
                Toast.makeText(this, "Please accept the permissions", Toast.LENGTH_LONG)
                        .show();
            }
            ActivityCompat.requestPermissions(this,
                    missingPermissions.toArray(new String[missingPermissions.size()]),
                    REQUEST_CODE);
        }
    }

    private void verify() {
        Config config = SinchVerification.config()
                .applicationKey(APPLICATION_KEY)
                .context(getApplicationContext())
                .build();

        VerificationListener listener = new MyVerificationListener();
        mVerification = SinchVerification.createSmsVerification(config, mPhoneNumber, listener);
        mVerification.initiate();

        showProgress();
    }

    private boolean needPermissionsRationale(List<String> permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        verify();
    }

    private List<String> getMissingPermissions(String[] requiredPermissions) {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }

    private void hideProgress() {
        progressDialog.cancel();

    }

    private void showProgress() {
        progressDialog.show();
    }


    class MyVerificationListener implements VerificationListener {

        @Override
        public void onInitiated(InitiationResult result) {
            Log.d(TAG, "Initialized!");
            showProgress();
        }

        @Override
        public void onInitiationFailed(Exception exception) {
            Log.e(TAG, "Verification initialization failed: " + exception.getMessage());

            if (exception instanceof InvalidInputException) {
               goBackToEnterPhone();
            } else if (exception instanceof ServiceErrorException) {
                goBackToEnterPhone();
            } else {
                goBackToEnterPhone();
            }
        }

        @Override
        public void onVerificationFallback() {
            goBackToEnterPhone();
        }


        @Override
        public void onVerified() {
            mIsVerified = true;
            Log.d(TAG, "Verified!");
            goToSignUp();
            hideProgress();
        }

        @Override
        public void onVerificationFailed(Exception exception) {
            if(mIsVerified) {
                return;
            }
        }
    }

    public void goBackToEnterPhone(){
        Toast.makeText(VerifyPhoneActivity.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
        startActivity(new Intent(VerifyPhoneActivity.this, EnterPhoneActivity.class));
    }

    public void goToSignUp(){
        startActivity(new Intent(VerifyPhoneActivity.this, SignUpActivity.class));
    }

}

