package com.arnaud.speedo;


//import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.SystemClock;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private Chronometer chronometer;
    private boolean time_running = false;
    private long pause_offset;

    int target_velocity = (int) 350;
    int low_range_velocity = (int) Math.round(target_velocity * .7);
    int max_range_velocity = (int) Math.round(target_velocity * 1.3);
    int max_velocity = (int) 0;

    int min_cursor_speed = (int) Math.round(target_velocity * .5);
    int max_cursor_speed = (int) Math.round(target_velocity * 1.5);

    float odo = 0;
    Location last_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
        this.onLocationChanged(null);

        chronometer = findViewById(R.id.time);

        SeekBar speed_cursor = this.findViewById(R.id.speedBar);
        speed_cursor.setEnabled(false);

        Button buttonStart = findViewById(R.id.start_button);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("YOYO", "Start Pushed.");
                Log.d("YOYO", String.format(Locale.getDefault(), "%.3f", odo));
                if(!time_running){
                    chronometer.setBase(SystemClock.elapsedRealtime() - pause_offset);
                    chronometer.start();
                    time_running = true;
                }
            }
        });

        Button buttonStop = findViewById(R.id.stop_button);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time_running){
                    chronometer.stop();
                    pause_offset = SystemClock.elapsedRealtime() - chronometer.getBase();
                    time_running = false;
                }
                Log.d("YOYO", "Stop Pushed.");
                Log.d("YOYO", String.format(Locale.getDefault(), "%.3f", odo));
            }
        });

        final TextView odo_view = this.findViewById(R.id.odo);
        final TextView max_speed = this.findViewById(R.id.max_speed);
        Button buttonReset = findViewById(R.id.reset_button);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.stop();
                pause_offset = 0;
                time_running = false;
                odo = 0;
                last_location = null;
                max_speed.setText(String.format(Locale.getDefault(), "%03d", 0));
                odo_view.setText(String.format(Locale.getDefault(), "%.3f", 0.));
                chronometer.setBase(SystemClock.elapsedRealtime() - pause_offset);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onLocationChanged(Location location) {
        TextView avg_speed = this.findViewById(R.id.avg_speed);
        TextView max_speed = this.findViewById(R.id.max_speed);
        TextView odo_view = this.findViewById(R.id.odo);

        if(location == null){
            avg_speed.setText("---");
        }else{

            //Get speed
            int currentSpeed = Math.round(location.getSpeed() * 60);
            if(currentSpeed > max_velocity) max_velocity = currentSpeed;
            //Update Speed fields
            avg_speed.setText(String.format(Locale.getDefault(), "%03d", currentSpeed));
            max_speed.setText(String.format(Locale.getDefault(),"%03d", max_velocity));
            //Update style based on new speeds
            if(currentSpeed > max_range_velocity || currentSpeed < low_range_velocity) {
                avg_speed.setTextColor(Color.rgb(216,27,96));
            }else{
                avg_speed.setTextColor(Color.rgb(0,133,119));
            }
            //update progress
            int speed_percentage = (int) Math.round(100.*(currentSpeed - min_cursor_speed)/(max_cursor_speed - min_cursor_speed));
            SeekBar speed_cursor = this.findViewById(R.id.speedBar);
            speed_cursor.setProgress(speed_percentage, true);
            speed_cursor.setEnabled(false);

            //Get location
            if (last_location != null && time_running) odo += location.distanceTo(last_location)/1000.;
            odo_view.setText(String.format(Locale.getDefault(), "%.3f", odo));
            last_location = location;
            Log.d("YOYO", String.format(Locale.getDefault(), "%.3f", odo));
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

}