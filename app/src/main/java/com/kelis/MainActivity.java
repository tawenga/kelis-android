package com.kelis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.claudiodegio.msv.BaseMaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.claudiodegio.msv.SuggestionMaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnSearchViewListener{

    BaseMaterialSearchView searchView;
    SuggestionMaterialSearchView mSearchView;
    String mUserId;
    String mCourseNameYear;

    private List<UserProfile> userProfiles;
    private RecyclerView rv;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        if (hasInternet()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        searchView = (BaseMaterialSearchView) findViewById(R.id.sv);
        String[] arrays = getResources().getStringArray(R.array.query_suggestions);

        mSearchView = (SuggestionMaterialSearchView)searchView;

        mSearchView.setSuggestion(arrays, true);
        mSearchView.setOnSearchViewListener(MainActivity.this);

        retrieveFromPrefs();
        if (!mUserId.isEmpty()) {
            search(formatSearchKeyword(mCourseNameYear));
        }else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        rv=(RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        if(query != null){
            mSearchView.closeSearch();
            if (hasInternet()) {
                progressBar.setVisibility(View.VISIBLE);
                search(formatSearchKeyword(query));
            }
        }
        return true;
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

                        progressBar.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        try {
                            JSONArray resultsArray = response.getJSONArray("user_profiles");
                            userProfiles = new ArrayList<>();
                            for(int i = 0; i < resultsArray.length(); i++){
                                userProfiles.add(new UserProfile(resultsArray.getJSONObject(i)));
                                RVAdapter adapter = new RVAdapter(userProfiles, MainActivity.this);
                                rv.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                       // hideProgress();
                        error.printStackTrace();
                    }
                });
    }

    public String formatSearchKeyword(String keyword){
        return TextUtils.join("+", keyword.split(" "));
    }

    public  boolean hasInternet(){
            try {
                InetAddress inetAddress = InetAddress.getByName("google.com");
                return  !inetAddress.equals("");
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return false;
            }

    }
}
