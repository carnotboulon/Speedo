package com.arnaud.speedo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d("YOYO", "In settings activity");
        Intent intent = getIntent();
        int target_speed = intent.getIntExtra("target_velocity",0);
        int gps_min_time = intent.getIntExtra("gps_min_time", 0);
        int gps_min_dist = intent.getIntExtra("gps_min_dist", 0);

        EditText target_speed_ET = findViewById(R.id.user_target_speed);
        target_speed_ET.setText(String.valueOf(target_speed), TextView.BufferType.EDITABLE);
        EditText gps_min_time_ET = findViewById(R.id.user_gps_update_time);
        gps_min_time_ET.setText(String.valueOf(gps_min_time), TextView.BufferType.EDITABLE);
        EditText gps_min_dist_ET = findViewById(R.id.user_gps_update_dist);
        gps_min_dist_ET.setText(String.valueOf(gps_min_dist), TextView.BufferType.EDITABLE);

        Log.d("YOYO", "target_speed = " + target_speed);
        Log.d("YOYO", "gps_min_time = " + gps_min_time);
        Log.d("YOYO", "gps_min_dist = " + gps_min_dist);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Log.d("YOYO", "Up button has been pressed.");
                sendResultsUp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //@Override
    public void sendResultsUp() {
        //Log.d("YOYO", "Sending results back to parent activity.");
        //Toast.makeText(this,"onBackPressed",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();

        EditText target_speed_field = findViewById(R.id.user_target_speed);
        String target_speed = target_speed_field.getText().toString();
        intent.putExtra("target_speed", target_speed);

        EditText gps_update_time_field = findViewById(R.id.user_gps_update_time);
        String gps_update_time = gps_update_time_field.getText().toString();
        intent.putExtra("gps_update_time", gps_update_time);

        EditText gps_update_dist_field = findViewById(R.id.user_gps_update_dist);
        String gps_update_dist = gps_update_dist_field.getText().toString();
        intent.putExtra("gps_update_dist", gps_update_dist);

        setResult(RESULT_OK, intent);
        finish();
    }


}
