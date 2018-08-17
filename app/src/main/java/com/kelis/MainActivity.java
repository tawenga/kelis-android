package com.kelis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.claudiodegio.msv.BaseMaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.claudiodegio.msv.SuggestionMaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnSearchViewListener{

    BaseMaterialSearchView searchView;
    SuggestionMaterialSearchView mSearchView;
    String mUserId;
    String mCourseNameYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = (BaseMaterialSearchView) findViewById(R.id.sv);
        String[] arrays = getResources().getStringArray(R.array.query_suggestions);

        mSearchView = (SuggestionMaterialSearchView)searchView;

        mSearchView.setSuggestion(arrays, true);
        mSearchView.setOnSearchViewListener(MainActivity.this);

        retrieveFromPrefs();
        if (!mUserId.isEmpty()) {
            search(mCourseNameYear);
        }else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onSearchViewShown() {

    }

    @Override
    public void onSearchViewClosed() {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // handle text submit and then return true
        if(query != null){

        }
        Log.d("MainActivity", query);
        return false;
    }

    @Override
    public void onQueryTextChange(String newText) {

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_photo) {
            goToMyProfile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToMyProfile(){
        startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
    }

    public void retrieveFromPrefs(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserId = preferences.getString("user_id", "");
        mCourseNameYear= preferences.getString("course_name_and_year", "");
    }

    private void search(String keyword){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(App.APP_DOMAIN + "search")
                .addJSONObjectBody(jsonObject)
                .setTag("search")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // hideProgress();
                        Log.d("MainActivityRes", response.toString());
                    }
                    @Override
                    public void onError(ANError error) {
                       // hideProgress();
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }
}
