package com.frostapps.productive.myscheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Chronometer;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by Khaled on 2/24/2016.
 */
public class StartActivity extends Activity {
    private int backPressed = 0;
    Button btnStart, btnStop;
    Chronometer stopWatch;
    Calendar rightNow;
    int startHour;
    int startMin;
    int endMin;
    int endHour;
    int[] retData;
    String event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);
        retData = new int[4];
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        stopWatch = (Chronometer)findViewById(R.id.text_view_time);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightNow = Calendar.getInstance();
                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                stopWatch.setBase(SystemClock.elapsedRealtime());
                stopWatch.start();
                startHour = rightNow.get(Calendar.HOUR_OF_DAY);
                startMin = rightNow.get(Calendar.MINUTE);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightNow = Calendar.getInstance();
                endHour = rightNow.get(Calendar.HOUR_OF_DAY)-startHour;
                endMin = rightNow.get(Calendar.MINUTE);
                retData[0] = startHour;
                retData[1] = endHour;
                retData[2] = startMin;
                retData[3] = endMin;
                btnStop.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                stopWatch.stop();
                startAct();
            }
        });


    }

    public void onBackPressed() {
        String message = "Press back again to return to main screen.";

        if (backPressed == 0) {
            backPressed++;
            String text = "Press back again to go return to Main Screen!";
            Toast info = new Toast(this);
            info.makeText(this, text, Toast.LENGTH_SHORT).show();
        } else {
            close(false);
        }
    }

    public void close(boolean good){
        Intent goingBack = new Intent();
        goingBack.putExtra("DataX", retData);
        goingBack.putExtra("EventX", event);
        goingBack.putExtra("good",good);
        setResult(RESULT_OK, goingBack);
        finish();
    }

    public void startAct(){
            Intent getOptions = new Intent(this, AddActivity.class);
            final int result = 10;
            getOptions.putExtra("mode", 1);
            startActivityForResult(getOptions, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            event = data.getStringExtra("DataName");
        }
        close(true);
    }

    public void menu(View view) {
        Intent gameMode = new Intent(this, MenuM.class);
        startActivity(gameMode);
    }
    public void makeToast(String text){
        Toast info = new Toast(this);
        info.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}