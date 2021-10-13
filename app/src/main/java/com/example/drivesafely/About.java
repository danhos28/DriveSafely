package com.example.drivesafely;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;

public class About extends AppCompatActivity {
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        drawerLayout = findViewById(R.id.drawer_layout);
    }
    public void ClickMenu(View view){
        MainActivity.openDrawer(drawerLayout);
    }
    public void ClickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);
    }
    public void ClickHome(View view){
        MainActivity.redirectActivity(this,MainActivity.class);
        finish();
    }
    public void ClickGuide(View view){
        MainActivity.redirectActivity(this,Guide.class);
        finish();
    }
    public void ClickDrowsiness(View view){
        MainActivity.redirectActivity(this, Tips.class);
        finish();
    }
    public void ClickAboutUs(View view){
        //recreate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }
}