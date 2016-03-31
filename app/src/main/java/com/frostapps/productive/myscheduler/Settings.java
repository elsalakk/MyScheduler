package com.frostapps.productive.myscheduler;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Khaled on 2/24/2016.
 */
public class Settings extends Activity {
    private int backPressed = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    SavedData prgData = new SavedData();
    Switch switch1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        switch1= (Switch)(findViewById(R.id.switch1));
        load();
        switch1.setChecked(prgData.Spinable);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prgData.Spinable = true;
                } else {
                    prgData.Spinable = false;
                }
                save();
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
            save();
            finish();
        }
    }
    public void menu(View view) {
        Intent gameMode = new Intent(this, MenuM.class);
        startActivity(gameMode);
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