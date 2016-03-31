package com.frostapps.productive.myscheduler;

/**
 * Created by Khaled on 1/22/2016.
 **/
import business.Sched;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.frostapps.productive.myscheduler.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AddActivity extends Activity {
    int mode = 0;
    int backPressed = 0;
    private FrameLayout frame;
    private PieChart chart;
    private float[] yData;
    private String[] xData;
    private String[] retData = new String[8];
    private Sched sched;
    int get;
    String eventName;// only returns if mode = 1
    SavedData prgData = new SavedData();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sched = new Sched();
        load();
        get = 0;
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mode = extras.getInt("mode");
        }
        if(mode == 0) {
            yData = sched.getY(get, 0);
            xData = sched.getX(get, 0);
        }
        else{
            yData = sched.getY(6, 0);
            xData = sched.getX(6, 0);
        }
        setContentView(R.layout.add_activity);

        frame = (FrameLayout) findViewById(R.id.chartFrame);
        chart = new PieChart(this);
        chart.setCenterTextSize(20);


        // add pie chart to main layout
        frame.addView(chart);
        // configure pie chart
        chart.setUsePercentValues(false);
        chart.setDescription("");

        // enable hole and configure
        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(40);
        chart.setTransparentCircleRadius(10);
        if(mode == 1){
            chart.setCenterText("Activity");
        }
        else{
            chart.setCenterText("Start Time");
        }
        // enable rotation of the chart by touch
        chart.setRotationAngle(0);
        chart.setRotationEnabled(prgData.Spinable);

        // set a chart value selected listener
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected

                if (((String) xData[e.getXIndex()]).equals("More")) {
                    // go to more menue for future
                }
                if(mode == 0) {
                    retData[get] = ((String) xData[e.getXIndex()]);
                    if (get < 7) {
                        get++;
                        if (get == 0 || get == 1 || get == 2|| get == 4||get == 6 || get == 7) {
                            yData = sched.getY(get, 0);
                            xData = sched.getX(get, 0);
                        } else {
                            int val = (timeString(retData[0], retData[2]));
                            if(val == 24){
                                val = 0;
                            }
                            if (get == 5 && val < 12 && val > timeString(retData[3], "am")) {
                                retData[5] = "pm";
                                get++;
                                yData = sched.getY(get, 0);
                                xData = sched.getX(get, 0);
                            } else if (get == 5 && val < 12 || get == 3) {
                                yData = sched.getY(get, val);
                                xData = sched.getX(get,val);
                            } else if (get == 5 && val >= 12) {
                                if (retData[3].equals("12:00")) {
                                    retData[5] = "am";
                                } else {
                                    retData[5] = "pm";
                                }
                                get++;
                                yData = sched.getY(get, 0);
                                xData = sched.getX(get, 0);
                            }
                        }
                        addData();
                        if (get == 3) {
                            chart.setCenterText("End time");
                        }

                        if (get == 6) {
                            chart.setCenterText("Activity");
                        }
                        if (get == 7) {
                            chart.setCenterText("Recurrence");
                        }
                    } else {
                        close(true);
                    }
                }
                else{
                    eventName= ((String) xData[e.getXIndex()]);
                    close2();
                }
                if (e == null)
                    return;

                //Insert what happens on click
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // add data
        addData();
        Legend l = chart.getLegend();
        l.setEnabled(false);
    }
    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < yData.length; i++)
            yVals1.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "data");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.GRAY);
        data.setDrawValues(false);

        chart.setData(data);
        // undo all highlights
        chart.highlightValues(null);

        // update pie chart
        chart.invalidate();
    }
    public void makeToast(String text){
        Toast info = new Toast(this);
        info.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    public void onBackPressed() {
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
        goingBack.putExtra("good",good);
        goingBack.putExtra("Data",retData);
        setResult(RESULT_OK, goingBack);
        finish();
    }
    public void close2(){
        Intent goingBack = new Intent();
        goingBack.putExtra("DataName",eventName);
        setResult(RESULT_OK, goingBack);
        finish();
    }
    public void menu(View view) {
        Intent gameMode = new Intent(this, MenuM.class);
        startActivity(gameMode);
    }
    private int timeString(String hour, String amPm){
        int x = 0;
        if(hour.equals("12:00") &&amPm.equals("am")){
            x = 24;
        }
        else if(hour.equals("1:00") &&amPm.equals("am")){
            x = 1;
        }
        else if(hour.equals("2:00") &&amPm.equals("am")){
            x = 2;
        }
        else if(hour.equals("3:00") &&amPm.equals("am")){
            x = 3;
        }
        else if(hour.equals("4:00") &&amPm.equals("am")){
            x = 4;
        }
        else if(hour.equals("5:00") &&amPm.equals("am")){
            x = 5;
        }
        else if(hour.equals("6:00") &&amPm.equals("am")){
            x = 6;
        }
        else if(hour.equals("7:00") &&amPm.equals("am")){
            x = 7;
        }
        else if(hour.equals("8:00") &&amPm.equals("am")){
            x = 8;
        }
        else if(hour.equals("9:00") &&amPm.equals("am")){
            x = 9;
        }
        else if(hour.equals("10:00") &&amPm.equals("am")){
            x = 10;
        }
        else if(hour.equals("11:00") &&amPm.equals("am")){
            x = 11;
        }
        else if(hour.equals("12:00") &&amPm.equals("pm")){
            x = 12;
        }
        else if(hour.equals("1:00") &&amPm.equals("pm")){
            x = 13;
        }
        else if(hour.equals("2:00") &&amPm.equals("pm")){
            x = 14;
        }
        else if(hour.equals("3:00") &&amPm.equals("pm")){
            x = 15;
        }
        else if(hour.equals("4:00") &&amPm.equals("pm")){
            x = 16;
        }
        else if(hour.equals("5:00") &&amPm.equals("pm")){
            x = 17;
        }
        else if(hour.equals("6:00") &&amPm.equals("pm")){
            x = 18;
        }
        else if(hour.equals("7:00") &&amPm.equals("pm")){
            x = 19;
        }
        else if(hour.equals("8:00") &&amPm.equals("pm")){
            x = 20;
        }
        else if(hour.equals("9:00") &&amPm.equals("pm")){
            x = 21;
        }
        else if(hour.equals("10:00") &&amPm.equals("pm")){
            x = 22;
        }
        else if(hour.equals("11:00") &&amPm.equals("pm")){
            x = 23;
        }
        return x;
    }public void save(){
        verifyStoragePermissions(this);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+ "/myScheduler/shcedDatabase.ser"))));
            oos.writeObject(prgData);
            oos.flush();
            oos.close();
        }
        catch(IOException r){
            r.printStackTrace();
        }
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    public void load(){
        try {
            File dir = new File(Environment.getExternalStorageDirectory().getPath(),"myScheduler");
            if(!dir.exists()){
                dir.mkdir();
            }

            File database = new File(Environment.getExternalStorageDirectory().getPath()+"/myScheduler/shcedDatabase.ser");
            if (database.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(database)));
                prgData = (SavedData) ois.readObject();
                ois.close();
            }
            else{
                database.createNewFile();
            }
        }
        catch(Exception e){
            prgData = new SavedData();
            e.printStackTrace();
        }
    }


}
