package com.kelis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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

        if (!hasInternet()) {
            progressBar.setVisibility(View.GONE);
        }else{
            retrieveFromPrefs();
            if (!mUserId.isEmpty()) {
                fetchAllLikes(mUserId);
                fetchAllUnLikes(mUserId);
                fetchClassmates(mCourseNameYear);
                //Fetch all user likes and unlikes and save them locally

            }else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }

        searchView = (BaseMaterialSearchView) findViewById(R.id.sv);
       // String[] arrays = getResources().getStringArray(R.array.query_suggestions);

        mSearchView = (SuggestionMaterialSearchView)searchView;

       // mSearchView.setSuggestion(arrays, true);
        mSearchView.setOnSearchViewListener(MainActivity.this);

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
        mSearchView.closeSearch();
        if(query != null){
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
       // mUserId = preferences.getString("user_id", "");
        //mCourseNameYear= preferences.getString("course_name_and_year", "");
        mUserId = "1";
        mCourseNameYear = "Gegis 4";
    }

    public void fetchClassmates(String courseNameYear){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("my_class", courseNameYear);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(App.APP_DOMAIN + "profiles/myclass")
                .addJSONObjectBody(jsonObject)
                .setTag("fetch classmates")
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
                        progressBar.setVisibility(View.GONE);
                        error.printStackTrace();
                    }
                });
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

    public void fetchAllLikes(String id){
        AndroidNetworking.get(App.APP_DOMAIN + "likes/" + id)
                .setTag("fetch all likes")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray resultsArray = response.getJSONArray("likes");
                            /*userProfiles = new ArrayList<>();
                            for(int i = 0; i < resultsArray.length(); i++){
                                userProfiles.add(new UserProfile(resultsArray.getJSONObject(i)));
                                RVAdapter adapter = new RVAdapter(userProfiles, MainActivity.this);
                                rv.setAdapter(adapter);
                            }*/
                            Log.d("xxxxx", resultsArray.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // hideProgress();
                        error.printStackTrace();
                        Log.d("xxxxxl", error.getErrorDetail());
                    }
                });
    }

    public void fetchAllUnLikes(String id){
        AndroidNetworking.get(App.APP_DOMAIN + "unlikes/" + id)
                .setTag("fetch all unlikes")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray resultsArray = response.getJSONArray("unlikes");
                            /*userProfiles = new ArrayList<>();
                            for(int i = 0; i < resultsArray.length(); i++){
                                userProfiles.add(new UserProfile(resultsArray.getJSONObject(i)));
                                RVAdapter adapter = new RVAdapter(userProfiles, MainActivity.this);
                                rv.setAdapter(adapter);
                            }*/
                            Log.d("xxxxx", resultsArray.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // hideProgress();
                        error.printStackTrace();
                        Log.d("xxxxxunl", error.getErrorDetail());
                    }
                });
    }

    public String formatSearchKeyword(String keyword){
        return TextUtils.join("+", keyword.split(" "));
    }

    public  boolean hasInternet(){
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }

    //Use this for saving retriving likes/unlikes, The use isContains to color thumbs then work on count
    public void saveArrayList(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
