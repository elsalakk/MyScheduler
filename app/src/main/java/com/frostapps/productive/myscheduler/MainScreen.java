package com.frostapps.productive.myscheduler;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class MainScreen extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private Toolbar toolbar;
    private Button add;
    private Button start;
    private CaldroidFragment caldroidFragment;
    private WeekView mWeekView;
    private Date prevDate;
    SavedData prgData;
    private  ArrayList<Date> selectedDay;
    private int startTime;
    int startMin;
    int endMin;
    private int endTime;
    private String eventName;
    private String eventOccourance;
    private int day;
    private int month;
    private int year;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private void setCustomResourceForDates() {
        Calendar cal = Calendar.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        prgData = new SavedData();
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Log.w("LOG_TAG", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }
        setContentView(R.layout.main_activity);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        selectedDay= new ArrayList<Date>();
        selectedDay.add(new Date());
        prevDate = null;

        add = (Button) findViewById(R.id.addbutton);
        start = (Button) findViewById(R.id.startbutton);
        caldroidFragment = new CaldroidFragment();

        final TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Month");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Month");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Week");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Week");
        host.addTab(spec);

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

                setTabColor(host);
            }
        });
        setTabColor(host);

        mWeekView = (WeekView) findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setEmptyViewLongPressListener(this);

        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        } else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            caldroidFragment.setArguments(args);
            setCustomResourceForDates();

            //Draw out the month view
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.calendar1, caldroidFragment);
            t.commit();

            // Setup listener
            final CaldroidListener listener = new CaldroidListener() {

                @Override
                public void onSelectDate(Date date, View view) {
                    selectedDay.set(0,date);
                    month = caldroidFragment.getMonth();
                    year = caldroidFragment.getYear();
                    day = Integer.parseInt(date.toString().split("\\s+")[2]);
                    if(prevDate != null) {
                        caldroidFragment.clearDisableDates();
                    }
                    caldroidFragment.setDisableDates(selectedDay);

                    prevDate = date;
                    caldroidFragment.refreshView();
                }

                @Override
                public void onChangeMonth(int month, int year) {
                }

                @Override
                public void onLongClickDate(Date date, View view) {
                }

                @Override
                public void onCaldroidViewCreated() {
                }

            };
            caldroidFragment.setCaldroidListener(listener);
            final TextView textView = (TextView) findViewById(R.id.textview);
        }
        load();
    }
    public void setTabColor(TabHost tabhost) {

        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++)
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#3c9996")); //unselected

        if(tabhost.getCurrentTab()==0)
            tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FF13DCD6")); //1st tab selected
        else
            tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FF13DCD6")); //2nd tab selected
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
    }
    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }
    public void onStart(View view) {
        save();
        Calendar rightNow = Calendar.getInstance();
        month = rightNow.get(Calendar.MONTH)+1;;
        day = rightNow.get(Calendar.DAY_OF_MONTH);
        year = rightNow.get(Calendar.YEAR);
        Intent gameMode = new Intent(this, StartActivity.class);
        start.setAlpha(0.8f);
        add.setAlpha(0.4f);
        final int result = 12;
        startActivityForResult(gameMode, result);

    }
    public void onAdd(View view) {
        save();
        if(prevDate != null) {
            add.setAlpha(0.8f);
            start.setAlpha(0.4f);
            Intent gameMode = new Intent(this, AddActivity.class);
            final int result = 10;
            startActivityForResult(gameMode, result);
        }
        else{
            makeToast("Please select a day first");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String[] dataX = data.getStringArrayExtra("Data");
        int[] dataY = data.getIntArrayExtra("DataX");
        boolean good = data.getBooleanExtra("good",false);
        String name = data.getStringExtra("EventX");
        //for(int x = 0; x<dataX.length;x++){
            //makeToast(dataX[x]);
        //}
        if(good) {
            switch (requestCode) {
                case (10): {
                    prgData.dates.add(prevDate);
                    setDays();
                    try {
                        startTime = timeString(dataX[0], dataX[2]);
                        startMin = Integer.parseInt((dataX[1].split(":"))[1]);
                        if (startTime == 24) {
                            startTime = 0;
                        }
                        endTime = timeString(dataX[3], dataX[5]) - startTime;
                        endMin = Integer.parseInt((dataX[4].split(":"))[1]);
                        eventName = dataX[6];
                        eventOccourance = dataX[7];
                        prgData.events.add(new Event(startTime, endTime, eventName, eventOccourance, day, month, year, startMin, endMin));
                        prgData.eventCounter++;
                        mWeekView.notifyDatasetChanged();
                    } catch (Exception e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                }
                break;
                case (12): {
                    startTime = dataY[0];
                    endTime = dataY[1];
                    startMin = dataY[2];
                    endMin = dataY[3];
                    eventName = name;
                    eventOccourance = "Once";
                    prgData.eventCounter++;
                    mWeekView.notifyDatasetChanged();
                    prgData.events.add(new Event(startTime, endTime, eventName, eventOccourance, day, month, year, startMin, endMin));
                }
                break;
            }
            save();
        }
    }
    private void setDays(){
        for(int x =0; x< prgData.dates.size();x++) {
            caldroidFragment.setSelectedDate(prgData.dates.get(x));
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
                setDays();
                mWeekView.notifyDatasetChanged();
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

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> eventsWeek = new ArrayList<WeekViewEvent>();
        Calendar startTime;
        Calendar endTime;

        for (int x = 0; x < prgData.eventCounter; x++) {
            if(prgData.events.get(x).month == newMonth) {
                startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, prgData.events.get(x).startTime);
                startTime.set(Calendar.MINUTE, prgData.events.get(x).startMin);
                startTime.set(Calendar.DAY_OF_MONTH, prgData.events.get(x).day);
                startTime.set(Calendar.MONTH, prgData.events.get(x).month - 1);
                startTime.set(Calendar.YEAR, prgData.events.get(x).year);
                endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR, prgData.events.get(x).endTime);
                endTime.set(Calendar.MINUTE, prgData.events.get(x).endMin);
                endTime.set(Calendar.MONTH, prgData.events.get(x).month - 1);
                WeekViewEvent event = new WeekViewEvent(prgData.eventCounter, prgData.events.get(x).eventName, startTime, endTime);
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                event.setColor(color);
                eventsWeek.add(event);
            }
        }


        return eventsWeek;
    }
    public void makeToast(String text){
        Toast info = new Toast(this);
        info.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    public void menu(View view) {
        save();
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
    }
    public void save(){
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
}
class Event implements Serializable {
    public int startTime;
    public int endTime;
    public String eventName;
    public String eventOccourance;
    public int day;
    public int month;
    public int year;
    public int startMin;
    public int endMin;
    public Event(int startTime,int endTime,String eventName,String eventOccourance,int day,int month,int year,int startMin, int endMin){
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventName = eventName;
        this.eventOccourance = eventOccourance;
        this.day = day;
        this.month = month;
        this.year = year;
        this.startMin = startMin;
        this.endMin = endMin;
    }
}
class SavedData implements Serializable{
    ArrayList<Event> events;
    ArrayList<Date> dates;
    int eventCounter;
    boolean Spinable;
    public SavedData(){
        eventCounter = 0;
        events = new ArrayList<Event>();
        dates = new  ArrayList<Date>();
        Spinable = true;
    }
}