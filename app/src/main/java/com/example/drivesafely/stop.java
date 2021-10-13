package com.example.drivesafely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

public class stop extends AppCompatActivity {
    private static final String TAG = "StopActivity";
    int distanceTravelled;
    Button CloseBtn;
    TextView distanceTr,time,avgSpeed,sleepingCount,speedingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

        distanceTr = findViewById(R.id.textViewDistance);
        time = findViewById(R.id.textViewTime);
        avgSpeed = findViewById(R.id.avgspeed);
        sleepingCount = findViewById(R.id.TextViewSleep);
        speedingCount = findViewById(R.id.speedCountText);
        CloseBtn = findViewById(R.id.closeButton);

        distanceTravelled = (int) Detections.Dtraveled;
        Log.i(TAG, "distance travelled: "+ distanceTravelled);
        distanceTr.setText( distanceTravelled + " meters");
        time.setText( Detections.timeElps );
        sleepingCount.setText(String.valueOf(Detections.sleepCount));
        speedingCount.setText(String.valueOf(Detections.speedCount));
        avgSpeed();

        CloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        getWindow().setAttributes(params);
    }
    public void avgSpeed(){
        if (MainActivity.unit == 1 || MainActivity.unit == 4){
            float AvgKm = Detections.avgSpeed * 3.6f;
            int avgSpdKm = (int) AvgKm;
            avgSpeed.setText( avgSpdKm + " km/h");
        }
        else if (MainActivity.unit == 2){
            avgSpeed.setText( Detections.avgSpeed + " m/s");
        }
        else if (MainActivity.unit == 3){
            float AvgMi =  Detections.avgSpeed * 2.237f;
            int avgSpdMi = (int) AvgMi;
            avgSpeed.setText( avgSpdMi + " mi/h");
        }

    }
}