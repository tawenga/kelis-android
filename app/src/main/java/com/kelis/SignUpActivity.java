package com.kelis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.kelis.EnterPhoneActivity.INTENT_PHONE_NUMBER;

public class SignUpActivity extends AppCompatActivity {
    EditText mPasswordEditText;
    EditText mPasswordConfirmationEditText;
    String mPhoneNumber;
    public static final String INTENT_USER_ID = "USER_ID";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mPasswordConfirmationEditText = (EditText) findViewById(R.id.password_confirmation_edit_text);
        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPasswordEditText.getText().toString().trim();
                String passwordConfirmation = mPasswordConfirmationEditText.getText().toString().trim();
                if(!password.isEmpty()) {
                    if (password.equals(passwordConfirmation)) {
                        mPasswordEditText.setText(null);
                        mPasswordConfirmationEditText.setText(null);
                        signUp(mPhoneNumber, password);
                    } else {
                        mPasswordConfirmationEditText.setError("Please type the same password");
                    }
                }else {
                    mPasswordEditText.setError("Please type a password");
                }

            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        if (intent != null) {
            mPhoneNumber = intent.getStringExtra(INTENT_PHONE_NUMBER);
        }
    }

    private void hideProgress() {
        progressDialog.cancel();

    }

    private void showProgress() {
        progressDialog.show();
    }

    public void signUp(String phoneNumber, String password){
        //sign up and save user id
        showProgress();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", phoneNumber);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(App.APP_DOMAIN + "users")
                .addJSONObjectBody(jsonObject)
                .setTag("sign_up")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgress();
                        Log.d("SignUpActivity", response.toString());
                        try {
                            String user_id = response.getString("id");
                            Intent createProfile = new Intent(SignUpActivity.this, CreateProfileActivity.class);
                            createProfile.putExtra(INTENT_USER_ID, user_id);
                            startActivity(createProfile);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        mPasswordEditText.setText(null);
                        mPasswordConfirmationEditText.setText(null);
                        hideProgress();
                        Log.d("SignUpActivity", error.toString());
                        Toast.makeText(SignUpActivity.this, "Please try again", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }
}
