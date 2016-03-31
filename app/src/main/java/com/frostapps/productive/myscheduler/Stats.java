package com.frostapps.productive.myscheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Khaled on 2/24/2016.
 */
public class Stats extends Activity {
    private int backPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);
    }

    public void onBackPressed() {
        String message = "Press back again to return to main screen.";

        if (backPressed == 0) {
            backPressed++;
            String text = "Press back again to go return to Main Screen!";
            Toast info = new Toast(this);
            info.makeText(this, text, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
    public void menu(View view) {
        Intent gameMode = new Intent(this, MenuM.class);
        startActivity(gameMode);
    }
}
