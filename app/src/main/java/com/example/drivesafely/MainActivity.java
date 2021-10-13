package com.example.drivesafely;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static String choiceAlarm;
    public static int timertick,speedlimit;
    public static int unit = 4;
    public static int vib;

    Spinner spinneralarm;
    DrawerLayout drawerLayout;
    EditText timerset,setlimit;
    Button button;
    RadioGroup radioGroupSpeed;
    RadioButton radioButtonKm,radioButtonM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        spinneralarm = findViewById(R.id.spinneralarm);
        timerset = findViewById(R.id.timerText);
        setlimit = findViewById(R.id.setLimit);
        button = findViewById(R.id.start);
        vib = 1;


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(timerset.getText().toString()))
                {
                    timerset.setError("The set timer cannot be empty!");
                }
                else if (TextUtils.isEmpty(setlimit.getText().toString())){
                    setlimit.setError("The speed limit cannot be empty!");
                }
                else {
                    timertick = Integer.valueOf(timerset.getText().toString());
                    speedlimit = Integer.valueOf(setlimit.getText().toString());

                    redirectActivity(MainActivity.this, Detections.class);
                }
            }
        });

        ArrayAdapter<CharSequence> adapterAlarm = ArrayAdapter.createFromResource(this,R.array.alarms, android.R.layout.simple_spinner_item);
        adapterAlarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneralarm.setAdapter(adapterAlarm);
        spinneralarm.setOnItemSelectedListener(this);
    }

    public void CheckButtonUnit(View view){
        boolean checked = ((RadioButton) view).isChecked();
        String str="";
        switch(view.getId()) {
            case R.id.km:
                if (checked)
                    str = "Unit of Speed: km/h";
                    unit = 1;
                break;
            case R.id.m:
                if (checked)
                    str = "Unit of Speed: m/s";
                    unit = 2;
                break;
            case R.id.mi:
                if (checked)
                    str = "Unit of Speed: mi/h";
                unit = 3;
                break;
        }
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    public void CheckButtonVib(View view){
        boolean checked = ((RadioButton) view).isChecked();
        String str="";
        switch(view.getId()) {
            case R.id.vibOn:
                if (checked)
                    str = "Vibration is on";
                vib = 2;
                break;
            case R.id.vibOff:
                if (checked)
                    str = "Vibration is off";
                vib = 3;
                break;
        }
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view){
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view){
        //recreate();
    }

    public void ClickGuide(View view){
        redirectActivity(this,Guide.class);
        finish();
    }

    public void ClickDrowsiness(View view){
        redirectActivity(this, Tips.class);
        finish();
    }

    public void ClickAboutUs(View view){
        redirectActivity(this, About.class);
        finish();
    }

    public static void redirectActivity(Activity activity,Class aClass) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
       choiceAlarm = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(getApplicationContext(),choiceAlarm,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}