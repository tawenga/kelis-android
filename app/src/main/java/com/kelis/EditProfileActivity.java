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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.abdularis.civ.CircleImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class EditProfileActivity extends AppCompatActivity {

    String mUserId;
    public String photo;
    public String mCurrentPhoto;
    UserProfile userProfile;

    CircleImageView mProfilePhoto;
    Uri filePath;
    ProgressDialog progressDialog;

    private static final int CHOOSE_PHOTO_REQUEST = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this,MainActivity.class));
            }
        });

        mProfilePhoto = (CircleImageView) findViewById(R.id.my_photo_image_view);

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(true);

        retrieveFromPrefs();
        if(!mCurrentPhoto.isEmpty()){
            fetchCurrentPhoto(mCurrentPhoto);
        }

        getUserProfile(mUserId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                uploadPhoto();
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
                            photo = taskSnapshot.getDownloadUrl().toString();
                            if (userProfile != null){
                                editUserProfile(photo);
                            }else {
                                Toast.makeText(EditProfileActivity.this, "You need internet to change photo", Toast.LENGTH_LONG)
                                        .show();
                            }


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
            //Do nothing
        }

    }

    private void hideProgress() {
        progressDialog.cancel();

    }

    private void showProgress() {
        progressDialog.show();
    }

    private void editUserProfile(String photo){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userProfile.getUserId());
            jsonObject.put("username", userProfile.getUsername());
            jsonObject.put("course_name_and_year", userProfile.getCourseNameAndYear());
            jsonObject.put("photo", photo);
            jsonObject.put("thumbs_up", userProfile.getThumbsUp());
            jsonObject.put("thumbs_down", userProfile.getThumbsDown());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.put(App.APP_DOMAIN + "profiles/" + userProfile.getUserId())
                .addJSONObjectBody(jsonObject)
                .setTag("edit_profile")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgress();
                        saveToPrefs(userProfile.getPhoto());
                        Toast.makeText(EditProfileActivity.this, "Your photo has been changed", Toast.LENGTH_LONG)
                                .show();
                    }
                    @Override
                    public void onError(ANError error) {
                        hideProgress();
                        error.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, "Please try again", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void getUserProfile(String userId){
        AndroidNetworking.get(App.APP_DOMAIN + "profiles/" + userId)
                .setTag("get_profile")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgress();
                        userProfile = new UserProfile(response);
                    }
                    @Override
                    public void onError(ANError error) {
                        hideProgress();
                        error.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, "You need internet to change photo", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    public void fetchCurrentPhoto(String url){
        Picasso.get().load(url)
                .placeholder(R.drawable.user_100)
                .into(mProfilePhoto);
    }

    public void saveToPrefs(String photo){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("photo", photo);
        editor.apply();
    }

    public void retrieveFromPrefs(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserId = preferences.getString("user_id", "");
        mCurrentPhoto = preferences.getString("photo", "");
    }

}
