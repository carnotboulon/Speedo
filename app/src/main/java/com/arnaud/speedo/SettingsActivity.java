package com.arnaud.speedo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d("YOYO", "In settings activity");

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("YOYO", "Menu button has been pressed.");
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("YOYO", "Up button has been pressed.");
                sendResultsUp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //@Override
    public void sendResultsUp() {
        Log.d("YOYO", "Sending results back to parent activity.");
        Toast.makeText(this,"onBackPressed",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();

        EditText target_speed_field = findViewById(R.id.user_target_speed);
        String target_speed = target_speed_field.getText().toString();
        intent.putExtra("target_speed", target_speed);
        Log.d("YOYO", "target_speed from Settings: %s" + target_speed);

        EditText gps_update_time_field = findViewById(R.id.user_gps_update_time);
        String gps_update_time = gps_update_time_field.getText().toString();
        intent.putExtra("gps_update_time", gps_update_time);
        Log.d("YOYO", "gps_update_time from Settings: %s" + gps_update_time);

        EditText gps_update_dist_field = findViewById(R.id.user_gps_update_dist);
        String gps_update_dist = gps_update_dist_field.getText().toString();
        intent.putExtra("gps_update_dist", gps_update_dist);
        Log.d("YOYO", "gps_update_dist from Settings: %s" + gps_update_dist);

        setResult(RESULT_OK, intent);
        finish();
    }


}
