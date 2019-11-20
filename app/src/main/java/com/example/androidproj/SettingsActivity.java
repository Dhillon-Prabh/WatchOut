package com.example.androidproj;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    SeekBar distanceSeekbar;
    EditText etDistance;
    RadioButton rb;
    int distance;
    String alert_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent i = getIntent();
        distance = i.getIntExtra("curDistance", 0);
        alert_option = i.getStringExtra("alert");
        if (distance == 0) {
            distance = 750;
        }
        distanceSeekbar = findViewById(R.id.distance_seekbar);
        etDistance = findViewById(R.id.distance_selected);
        etDistance.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDistance.setText(String.valueOf(distance));

        distanceSeekbar.setMax(150);
        distanceSeekbar.setProgress((distance - 500) / 10);
        distanceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                distance = 500 + progress * 10;
                etDistance.setText(String.valueOf(distance));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                etDistance.setText(String.valueOf(distance));
            }
        });

        etDistance.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etDistance.getText().toString())) {
                    return;
                }
                int i = Integer.parseInt(s.toString());
                if (i >= 500 && i <= 2000) {
                    distance = i;
                    distanceSeekbar.setProgress( (i - 500) / 10);
                    etDistance.setSelection(etDistance.getText().length());
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        RadioGroup rg = findViewById(R.id.alertOption);
        switch(alert_option.toLowerCase()) {
            case "":
            case "sound":
                rg.check(R.id.sound_option);
                break;
            case "vibrate":
                rg.check(R.id.vibrate_option);
                break;
            case "text to speech":
                rg.check(R.id.tts_option);
                break;
        }
    }

    public void clickBack(View view) {
        Intent settings = new Intent();
        settings.putExtra("distance", distance);
        settings.putExtra("alert", alert_option);
        setResult(RESULT_OK, settings);
        this.finish();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.vibrate_option:
                if (checked) {
                    rb = findViewById(R.id.vibrate_option);
                    alert_option = rb.getText().toString();
                }
                break;
            case R.id.sound_option:
                if (checked) {
                    rb = findViewById(R.id.sound_option);
                    alert_option = rb.getText().toString();
                }
                break;
            case R.id.tts_option:
                if (checked) {
                    rb = findViewById(R.id.tts_option);
                    alert_option = rb.getText().toString();
                }
                break;
        }
    }
}
