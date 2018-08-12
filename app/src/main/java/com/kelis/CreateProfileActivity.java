package com.kelis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.abdularis.civ.CircleImageView;
import com.jaredrummler.materialspinner.MaterialSpinner;

import static com.kelis.SignUpActivity.INTENT_USER_ID;

public class CreateProfileActivity extends AppCompatActivity {

    String userId;
    MaterialSpinner courseNameSpinner;
    MaterialSpinner courseYearSpinner;
    CircleImageView mProfilePhoto;

    String[] courses = {"Gegis", "Gis"};
    String[] fiveYears = {"5", "4", "3", "2", "1"};
    String[] fourYears = {"4", "3", "2", "1"};
    String[] twoYears = {"2", "1"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra(INTENT_USER_ID);
        }

        mProfilePhoto = (CircleImageView) findViewById(R.id.my_photo_image_view);
        courseNameSpinner = (MaterialSpinner) findViewById(R.id.course_name_spinner);
        courseYearSpinner = (MaterialSpinner) findViewById(R.id.course_year_spinner);
        courseNameSpinner.setItems(courses);
        courseYearSpinner.setItems(fiveYears);

        courseNameSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
            }
        });

        courseYearSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

            }
        });
    }


}
