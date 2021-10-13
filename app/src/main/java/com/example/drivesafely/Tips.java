package com.example.drivesafely;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;

public class Tips extends AppCompatActivity {

    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        drawerLayout = findViewById(R.id.drawer_layout);
    }
    public void ClickMenu(View view){
        MainActivity.openDrawer(drawerLayout);
    }
    public void ClickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);
    }
    public void ClickHome(View view){
        MainActivity.redirectActivity(this,Home.class);
        finish();
    }
    public void ClickGuide(View view){
        MainActivity.redirectActivity(this,Guide.class);
        finish();
    }
    public void ClickDrowsiness(View view){

        //recreate();
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