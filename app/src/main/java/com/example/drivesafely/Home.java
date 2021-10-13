package com.example.drivesafely;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import static com.example.drivesafely.MainActivity.openDrawer;

public class Home extends AppCompatActivity {
    DrawerLayout drawerLayout;
    CardView process;
    CardView guide;
    CardView tips;
    CardView aboutUs;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        process = findViewById(R.id.process);
        guide = findViewById(R.id.guide);
        tips = findViewById(R.id.tips);
        aboutUs = findViewById(R.id.aboutUs);

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(Home.this,MainActivity.class);
            }
        });
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(Home.this,Guide.class);
            }
        });
        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(Home.this,Tips.class);
            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(Home.this,About.class);
            }
        });
    }

    public void ClickMenu(View view){
        MainActivity.openDrawer(drawerLayout);
    }
    public void ClickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);
    }
    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }
        else{
            Toast.makeText(getBaseContext(),"Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    public void ClickHome(View view){

        //recreate();
    }
    public void ClickGuide(View view){
        MainActivity.redirectActivity(this,Guide.class);
        finish();
    }
    public void ClickDrowsiness(View view){

        MainActivity.redirectActivity(this,Tips.class);
    }
    public void ClickAboutUs(View view){
        MainActivity.redirectActivity(this,About.class);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }
}