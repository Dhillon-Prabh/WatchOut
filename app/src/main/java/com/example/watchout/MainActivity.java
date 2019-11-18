package com.example.watchout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    static final int SETTINGS_REQUEST = 1;
    int distance;
    String alert_option = "sound";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            TextView t = findViewById(R.id.test);
            TextView t2 = findViewById(R.id.test2);
            distance = data.getIntExtra("distance", 0);
            alert_option = data.getStringExtra("alert");
            t.setText(String.valueOf(distance));
            t2.setText(alert_option);
        }
    }
}
