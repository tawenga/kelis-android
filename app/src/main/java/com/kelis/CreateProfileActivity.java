package com.kelis;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.abdularis.civ.CircleImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.kelis.SignUpActivity.INTENT_USER_ID;

public class CreateProfileActivity extends AppCompatActivity {

    String userId;
    public String username;
    public String courseName;
    String courseYear;
    String courseNameAndYear;
    public String photo;

    EditText mFirstNameEditText;
    EditText mLastNameEditText;
    MaterialSpinner courseNameSpinner;
    MaterialSpinner courseYearSpinner;
    CircleImageView mProfilePhoto;
    Uri filePath;
    ProgressDialog progressDialog;

    String[] courses = {"Gegis", "Gis"};
    String[] fiveYears = {"5", "4", "3", "2", "1"};
    String[] fourYears = {"4", "3", "2", "1"};

    private static final int CHOOSE_PHOTO_REQUEST = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra(INTENT_USER_ID);
            //save user shared prefs
        }

        mProfilePhoto = (CircleImageView) findViewById(R.id.my_photo_image_view);
        mFirstNameEditText = (EditText) findViewById(R.id.first_name_edit_text);
        mLastNameEditText = (EditText) findViewById(R.id.last_name_edit_text);
        courseNameSpinner = (MaterialSpinner) findViewById(R.id.course_name_spinner);
        courseYearSpinner = (MaterialSpinner) findViewById(R.id.course_year_spinner);
        courseNameSpinner.setItems(courses);
        courseYearSpinner.setItems(fourYears);


        courseNameSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(position == 1){
                    courseYearSpinner.setItems(fourYears);
                } else if(position == 0){
                    courseYearSpinner.setItems(fiveYears);
                }
                courseName = item;
            }
        });

        courseYearSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                courseYear = item;
            }
        });

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = mFirstNameEditText.getText().toString().trim();
                String lastName = mLastNameEditText.getText().toString().trim();
                if (firstName.isEmpty()) {
                    mFirstNameEditText.setError("Please type your first name");
                } else if (lastName.isEmpty()){
                    mLastNameEditText.setError("Please type your last name");
                }else if (courseName == null){
                    courseNameSpinner.setError("Please select your course");
                }else if (courseYear == null){
                    courseYearSpinner.setError("Please select your year");
                }else {
                    username = firstName + " " + lastName;
                    courseNameAndYear = courseName + " " + courseYear;
                    uploadPhoto();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    private void choosePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mProfilePhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPhoto() {
        showProgress();
        if (filePath != null) {
            StorageReference sRef = App.mStorageRef.child("profile_photos/" + System.currentTimeMillis() + "." + getFileExtension(filePath));
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                           photo = taskSnapshot.getDownloadUrl().toString();
                           createUserProfile(username, courseNameAndYear, photo);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            exception.printStackTrace();
                        }
                    });
        } else {
            photo = "";
            createUserProfile(username, courseNameAndYear, photo);
        }

    }

    private void hideProgress() {
        progressDialog.cancel();

    }

    private void showProgress() {
        progressDialog.show();
    }

    private void createUserProfile(String username, String course_name_and_year,
                                   String photo){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", Integer.parseInt(userId));
                jsonObject.put("username", username);
                jsonObject.put("course_name_and_year", course_name_and_year);
                jsonObject.put("photo", photo);
                jsonObject.put("thumbs_up", 0);
                jsonObject.put("thumbs_down", 0);
                Log.d("CreateProfile", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            AndroidNetworking.post(App.APP_DOMAIN + "profiles")
                    .addJSONObjectBody(jsonObject)
                    .setTag("create_profile")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgress();
                            saveToPrefs(userId, courseNameAndYear);
                            Intent createProfile = new Intent(CreateProfileActivity.this, MainActivity.class);
                            startActivity(createProfile);
                            finish();
                        }
                        @Override
                        public void onError(ANError error) {
                            hideProgress();
                            error.printStackTrace();
                            Toast.makeText(CreateProfileActivity.this, "Please try again", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
    }

    public void saveToPrefs(String userId, String courseNameAndYear){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", userId);
        editor.putString("course_name_and_year", courseNameAndYear);
        editor.apply();
    }

}

