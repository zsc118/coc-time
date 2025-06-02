package com.example.coctime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    EditText et_building_level, et_lab_level, et_bell_tower;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        et_building_level = findViewById(R.id.et_building_level);
        et_lab_level = findViewById(R.id.et_lab_level);
        et_bell_tower = findViewById(R.id.et_bell_tower);

        Intent intent = getIntent();
        et_building_level.setText(String.valueOf(intent.getByteExtra("building", (byte) 0)));
        et_lab_level.setText(String.valueOf(intent.getByteExtra("lab", (byte) 0)));
        et_bell_tower.setText(String.valueOf(intent.getByteExtra("bellTower", (byte) 0)));

        findViewById(R.id.btn_set_ok).setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            returnIntent.putExtra("building", Byte.parseByte(et_building_level.getText().toString()));
            returnIntent.putExtra("lab", Byte.parseByte(et_lab_level.getText().toString()));
            returnIntent.putExtra("bellTower", Byte.parseByte(et_bell_tower.getText().toString()));
            finish();
        });

        findViewById(R.id.btn_set_cancel).setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        });
    }
}
