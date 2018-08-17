package com.kelis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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


public class LoginActivity extends AppCompatActivity {
    EditText mPhoneNumberEditText;
    EditText mPasswordEditText;
    ProgressDialog progressDialog;

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
                    mPhoneNumberEditText.setError("Please type your correct number");
                }else if (password.isEmpty()){
                    mPasswordEditText.setError("Please type a password");;
                } else {
                    login(phoneNumber, password);
                }
            }
        });

        findViewById(R.id.new_account_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, EnterPhoneActivity.class));
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    private void hideProgress() {
        progressDialog.cancel();

    }

    private void showProgress() {
        progressDialog.show();
    }

    public void login(String phoneNumber, String password){
        showProgress();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", formatPhoneNumber(phoneNumber));
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(App.APP_DOMAIN + "login")
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgress();
                        try {
                            boolean loginStatus = response.getBoolean("status");
                            if (loginStatus){
                                String userId = response.getString("id");
                                getUserProfile(userId);
                            }else {
                                hideProgress();
                                Toast.makeText(LoginActivity.this, "Please try again", Toast.LENGTH_LONG)
                                        .show();
                            }

                        } catch (JSONException e) {
                            hideProgress();
                            Toast.makeText(LoginActivity.this, "Please try again", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        hideProgress();
                        Log.d("loginxxx", error.toString());
                        clearEditText();
                        error.printStackTrace();
                    }
                });
    }

    public void getUserProfile(final String userId){
        AndroidNetworking.get(App.APP_DOMAIN + "profiles/" + userId)
                .setTag("get user profile")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            hideProgress();
                            Log.d("loginxxx", response.toString());
                            String courseNameAndYear = response.getString("course_name_and_year");
                            saveToPrefs(userId, courseNameAndYear);
                            Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(goToMain);
                            finish();

                        } catch (JSONException e) {
                            clearEditText();
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Please try again", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        clearEditText();
                        error.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Please try again", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    public void clearEditText(){
        mPasswordEditText.setText(null);
    }

    public void saveToPrefs(String userId, String courseNameAndYear){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", userId);
        editor.putString("course_name_and_year", courseNameAndYear);
        editor.apply();
    }

    private String formatPhoneNumber(String phoneNumber){
        String phoneNumberWithoutBegginingZero = phoneNumber.substring(1, phoneNumber.length());
        return "+254" + phoneNumberWithoutBegginingZero;
    }
}
