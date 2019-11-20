package com.example.androidproj;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    static final int SETTINGS_REQUEST = 1;
    int distance;
    String alert_option = "sound";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void clickSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("curDistance", distance);
        i.putExtra("alert", alert_option);
        startActivityForResult(i, SETTINGS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_REQUEST && resultCode == RESULT_OK && data != null) {
            distance = data.getIntExtra("distance", 0);
            alert_option = data.getStringExtra("alert");
        }
    }
      
    public void viewMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
