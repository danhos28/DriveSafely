package com.example.drivesafely;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class splash extends AppCompatActivity {
    Thread timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        timer = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(splash.this, Home.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}
