package com.example.coctime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

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
        et_building_level.setText(String.valueOf(intent.getByteExtra("building", (byte) 1) - 1));
        et_lab_level.setText(String.valueOf(intent.getByteExtra("lab", (byte) 1) - 1));
        et_bell_tower.setText(String.valueOf(intent.getByteExtra("bellTower", (byte) 0)));

        findViewById(R.id.btn_set_ok).setOnClickListener(v -> {
            try {
                byte building = Byte.parseByte(et_building_level.getText().toString());
                if (building < 0 || building > 8) throw new Exception();
                byte lab = Byte.parseByte(et_lab_level.getText().toString());
                if (lab < 0 || lab > 12) throw new Exception();
                byte bellTower = Byte.parseByte(et_bell_tower.getText().toString());
                if (bellTower < 0 || bellTower > 32) throw new Exception();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                returnIntent.putExtra("building", (byte) (building + 1));
                returnIntent.putExtra("lab", (byte) (lab + 1));
                returnIntent.putExtra("bellTower", bellTower);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "输入格式错误！", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_set_cancel).setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        });
    }
}
