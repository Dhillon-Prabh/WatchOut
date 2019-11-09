package com.example.watchout;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    SeekBar distanceSeekbar;
    TextView tvDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        distanceSeekbar = findViewById(R.id.distance_seekbar);
        tvDistance = findViewById(R.id.distance_selected);
    }

    public void clickBack(View view) {
        this.finish();
    }
}
