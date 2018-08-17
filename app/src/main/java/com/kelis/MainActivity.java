package com.kelis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.claudiodegio.msv.BaseMaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.claudiodegio.msv.SuggestionMaterialSearchView;

public class MainActivity extends AppCompatActivity implements OnSearchViewListener{

    BaseMaterialSearchView searchView;
    SuggestionMaterialSearchView mSearchView;
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
        mSearchView.setOnSearchViewListener(MainActivity.this); // this class implements OnSearchViewListener
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
        String userId = preferences.getString("user_id", "");
        String courseNameAndYear = preferences.getString("course_name_and_year", "");
    }
}
