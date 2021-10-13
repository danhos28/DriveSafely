package com.example.drivesafely;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.nitri.gauge.Gauge;

public class Detections extends AppCompatActivity implements LocationListener {
    private static final String TAG = "MainActivity";
    private LocationManager lm;
    private int counter = 0;
    private int timer;
    private Timer TimeCount;
    private TimerTask timerTask;
    private Handler handler = new Handler();


    int PERMISSION_ALL = 1;

    //speedometer
    int CumulativeSpeed = 0;
    int divider = 0;
    public static int avgSpeed;
    int CurrentSpeed;

    public static String timeElps;
    public static int sec, sleepCount, speedCount, Dtraveled;
    Vibrator vibrator;

    MediaPlayer player, player2;
    TextView textView, textView3;
    String alarmtype;
    Button stopbtn;

    //distance
    FusedLocationProviderClient fusedLocationProviderClient;
    Location location, location2;
    public static double distance;

    //elapsed time
    String currStartTime, currEndTime;
    Date startDate, endDate;

    //For looking logs
    ArrayAdapter adapter;
    ArrayList<String> list = new ArrayList<>();

    //camera
    ImageView imageView;
    SurfaceView cameraPreview;
    CameraSource cameraSource;
    //permissions
    String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detections);
        ImageView helpView = (ImageView) findViewById(R.id.help);
        ImageView backView = (ImageView) findViewById(R.id.back);
        helpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(Detections.this, pop.class);
            }
        });
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(Detections.this, MainActivity.class);
                finish();
            }
        });
        //Check permissions
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            Toast.makeText(this, "Please restart application!", Toast.LENGTH_LONG).show();
        } else {
            alarmtype = MainActivity.choiceAlarm;
            if (alarmtype.equals("alarm-1")) {
                player = MediaPlayer.create(this, R.raw.alarm);
                player2 = MediaPlayer.create(this, R.raw.alarm);
            } else if (alarmtype.equals("alarm-2")) {
                player = MediaPlayer.create(this, R.raw.alarm2);
                player2 = MediaPlayer.create(this, R.raw.alarm2);
            } else if (alarmtype.equals("alarm-3")) {
                player = MediaPlayer.create(this, R.raw.alarm3);
                player2 = MediaPlayer.create(this, R.raw.alarm3);
            } else if (alarmtype.equals("alarm-4")) {
                player = MediaPlayer.create(this, R.raw.alarm4);
                player2 = MediaPlayer.create(this, R.raw.alarm4);
            } else if (alarmtype.equals("alarm-5")) {
                player = MediaPlayer.create(this, R.raw.alarm5);
                player2 = MediaPlayer.create(this, R.raw.alarm5);
            }
            //fusedlocation
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            //get current time
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            currStartTime = dateFormat.format(c.getTime());

            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            imageView = findViewById(R.id.imageView);
            textView = findViewById(R.id.textView);
            textView3 = findViewById(R.id.textView3);
            stopbtn = findViewById(R.id.stop);

            timer = MainActivity.timertick * 7;
            sleepCount = 0;
            speedCount = 0;
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            cameraPreview = findViewById(R.id.camera_preview);
            getStartLocation();
            createCameraSource();
            getSpeed();

            //--------------------------stop button-------------------------------------------------

            stopbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getEndLocation();
                    stopTimer();
                    getElapsedTime();
                    MainActivity.redirectActivity(Detections.this, stop.class);
                }
            });

        }
    }

    public void getElapsedTime() {
        Calendar c2 = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        currEndTime = dateFormat.format(c2.getTime());

        startDate = null;
        try {
            startDate = dateFormat.parse(currStartTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        endDate = null;
        try {
            endDate = dateFormat.parse(currEndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = endDate.getTime() - startDate.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        sec = (int) seconds;
        Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                + " hours: " + hours);
        getDurationString(seconds);
    }

    private String getDurationString(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        timeElps = twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private String twoDigitString(long number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }


    private void getStartLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //initialize location
                location = task.getResult();
            }
        });
    }

    private void getEndLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //initialize location
                location2 = task.getResult();

                distance = location.distanceTo(location2);
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void vibrate(){
        if (MainActivity.vib == 1 || MainActivity.vib == 2) {
            vibrator.vibrate(2000);
        }
    }

    private void stopTimer(){
        if(TimeCount != null){
            avgSpeed = CumulativeSpeed / divider;
            Dtraveled = CumulativeSpeed;
            TimeCount.cancel();
            TimeCount.purge();
        }
    }
    private void startTimer(){
        Dtraveled = 0;
        TimeCount = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run(){
                        divider += 1;
                        CumulativeSpeed += location.getSpeed();
                        Log.e("cumulativeSpeed: ", " speed: " + CumulativeSpeed + " divider: " + divider);
                    }
                });
            }
        };
        TimeCount.schedule(timerTask, 1000, 1000);
    }
    private void getSpeed() {
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (lm != null) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection!", Toast.LENGTH_SHORT).show();
    }
    public void stopLoc(){
        lm.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        DigitSpeedView digitSpeedView = (DigitSpeedView) findViewById(R.id.digit_speed_view);
        float nCurrentSpeed = location.getSpeed();
        float nCurrentSpeedKm = nCurrentSpeed * 3.6f;
        float nCurrentSpeedMile = nCurrentSpeedKm * 0.62f;
        int CurrSpeedM = (int) nCurrentSpeed;
        int CurrSpeedKm = (int) nCurrentSpeedKm;
        int CurrSpeedMile = (int) nCurrentSpeedMile;
        startTimer();
        if (location==null){
            digitSpeedView.updateSpeed(0);
        }
        else {
            if (MainActivity.unit == 1 || MainActivity.unit == 4) {
                CurrentSpeed = CurrSpeedKm;
            }
            else if(MainActivity.unit == 2){
                CurrentSpeed = CurrSpeedM;
            }
            else if(MainActivity.unit == 3){
                CurrentSpeed = CurrSpeedMile;
            }
            digitSpeedView.updateSpeed(CurrentSpeed);
                if(CurrentSpeed >= MainActivity.speedlimit){
                    if(!player2.isPlaying()){
                        player2.start();
                        speedCount += 1;
                        textView3.setText("You are exceeding the speed limit!");
                        vibrate();
                    }
                }
                else{
                    if (player2.isPlaying()) {
                        player2.pause();
                        player2.seekTo(0);
                        textView3.setText("-,-");
                    }
                }
            }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        
    }
    //----------------------------------------------------------------------------------------------
    public void createCameraSource() {

        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerFactory()).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(15.0f)
                .build();


        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(Detections.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Detections.this, new String[]{Manifest.permission.CAMERA}, 1);
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    cameraSource.start(cameraPreview.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (cameraSource != null) {
                    try {
                        if (ActivityCompat.checkSelfPermission(Detections.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        cameraSource.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                stopLoc();
                stopTimer();
                if (cameraSource != null ) {
                    cameraSource.release();
                }
                if (player2 != null ) {
                    player2.release();
                }
                if (player != null) {
                    player.release();
                    finish();
                    Toast.makeText(Detections.this, "Process Stopped", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //This class will use google vision api to detect eyes
    private class EyesTracker extends Tracker<Face> {

        private final float THRESHOLD = 0.75f;

        private EyesTracker() {
        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {

            if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
                Log.i(TAG, "Eyes Detected");
                counter = 0;
                showStatus("Eyes Open");
                showImage(R.drawable.eyeopen);

                if (player.isPlaying()) {
                    player.pause();
                    player.seekTo(0);
                }
            } else {
                counter += 1;
                Log.i(TAG, "Timer" + counter);
                showImage(R.drawable.eyeclose);
                showStatus("Eyes Closed");
                if (!player.isPlaying()) {
                    if (counter > timer) {
                        player.start();
                        sleepCount += 1;
                        Log.i(TAG, "sleepCount: "+ sleepCount);
                        vibrate();
                    }
                }
            }
        }
        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);

            Log.i(TAG, "No Face Detected");
            showStatus("No Face Detected!");
            showImage(R.drawable.noeye);

            if (player.isPlaying()) {
                player.pause();
                player.seekTo(0);
            }
        }
        @Override
        public void onDone() {
            super.onDone();
        }
    }


    public void showStatus ( final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(message);
            }
        });
    }

    public void showImage ( final Integer resource){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resource);
            }
        });
    }
    private class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

        private FaceTrackerFactory() {

        }

        @Override
        public Tracker<Face> create(Face face) {
            return new EyesTracker();
        }
    }

}

