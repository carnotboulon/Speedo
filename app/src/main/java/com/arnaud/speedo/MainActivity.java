package com.arnaud.speedo;


//import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
//import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.SystemClock;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    int initial_target_velocity = 350;          //[m/min]
    int gps_min_time = 3;               //[s]
    int gps_min_dist = 5;               //[m]
    float odo = 0;
    int max_velocity = 0;
    private boolean time_running = false;

    private Chronometer chronometer;
    private long pause_offset;
    int target_velocity;
    int low_range_velocity, max_range_velocity;
    int min_cursor_speed, max_cursor_speed;
    Location last_location;
    LocationManager lm;

    public void setVelocities(int targetVel){
        target_velocity = targetVel;
        low_range_velocity = (int) Math.round(target_velocity * .7);        //text color
        max_range_velocity = (int) Math.round(target_velocity * 1.3);       //text color

        min_cursor_speed = (int) Math.round(target_velocity * .5);          //cursor min value
        max_cursor_speed = (int) Math.round(target_velocity * 1.5);         //cursor max value

        Log.d("YOYO", "Setting velocities. New Target Speed = " + target_velocity);
    }

    public void updateLocationRequestParameters(int min_time, int min_dist){
        if(lm != null) lm.removeUpdates(this);
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, min_time*1000, min_dist, this);
        this.onLocationChanged(null);
        Log.d("YOYO", "Updating GPS updates parameters. New min time = " + min_time +"[s] + min dist = " + min_dist + "[m].");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVelocities(initial_target_velocity);
        updateLocationRequestParameters(gps_min_time, gps_min_dist);

        chronometer = findViewById(R.id.time);

        SeekBar speed_cursor = this.findViewById(R.id.speedBar);
        speed_cursor.setEnabled(false);

        Button buttonStart = findViewById(R.id.start_button);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!time_running){
                    chronometer.setBase(SystemClock.elapsedRealtime() - pause_offset);
                    chronometer.start();
                    time_running = true;
                }
                Log.d("YOYO", "Start Pushed.");
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
                Log.d("YOYO", "Reset Pushed.");
            }
        });

    }

    //Open the settings page
    public void edit_settings() {
        //Log.d("YOYO", "Opening Settings");
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("target_velocity", target_velocity);
        intent.putExtra("gps_min_time", gps_min_time);
        intent.putExtra("gps_min_dist", gps_min_dist);
        startActivityForResult(intent,1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d("YOYO", "On activity result");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                try{
                    int target_speed = Integer.parseInt(data.getStringExtra("target_speed"));
                    gps_min_time = Integer.parseInt(data.getStringExtra("gps_update_time"));
                    gps_min_dist = Integer.parseInt(data.getStringExtra("gps_update_dist"));
                    setVelocities(target_speed);
                    updateLocationRequestParameters(gps_min_time,gps_min_dist);
                }catch(NumberFormatException e){
                    Log.d("YOYO", "Couldn't parse user inputs.");
                    Toast.makeText(getApplicationContext(), "Couldn't parse your inputs.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Log.d("YOYO","Settings button clicked!");
                edit_settings();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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