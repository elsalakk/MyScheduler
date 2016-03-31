package com.frostapps.productive.myscheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Khaled on 2/24/2016.
 */
public class MenuM extends Activity {
    private int backPressed = 0;
    private ImageButton schedule;
    private ImageButton stats;
    private ImageButton competition;
    private ImageButton menu;
    private ImageButton settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        schedule = (ImageButton)findViewById(R.id.schedButton);
        stats = (ImageButton)findViewById(R.id.statsButton);
        competition = (ImageButton)findViewById(R.id.compButton);
        settings = (ImageButton)findViewById(R.id.settingsButton);
    }

    public void onBackPressed() {
        String message = "Press back again to return to main screen.";

        if (backPressed == 0) {
            backPressed++;
            String text = "Press back again to go return to Main Screen!";
            Toast info = new Toast(this);
            info.makeText(this, text, Toast.LENGTH_SHORT).show();
        } else {
            //Intent gameMode = new Intent(this, MainScreen.class);
            //startActivity(gameMode);
            finish();
        }
    }
    public void schedule(View view) {
        Intent gameMode = new Intent(this, MainScreen.class);
        startActivity(gameMode);
        finish();
    }

    public void stats(View view) {
        Intent gameMode = new Intent(this, Stats.class);
        startActivity(gameMode);
        finish();
    }

    public void competition(View view) {
        Intent gameMode = new Intent(this, Competition.class);
        startActivity(gameMode);
        finish();
    }
    public void settings(View view) {
        Intent gameMode = new Intent(this, Settings.class);
        startActivity(gameMode);
        finish();
    }

}