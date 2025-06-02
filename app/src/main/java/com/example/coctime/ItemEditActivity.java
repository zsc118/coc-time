package com.example.coctime;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ItemEditActivity extends AppCompatActivity {
    RadioButton rad_eps, rad_del, rad_home_building, rad_home_lab, rad_night, rad_other;
    EditText et_pjt, et_time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_edit);

        rad_eps = findViewById(R.id.rad_eps);
        rad_del = findViewById(R.id.rad_del);
        rad_home_building = findViewById(R.id.rad_home_building);
        rad_home_lab = findViewById(R.id.rad_home_lab);
        rad_night = findViewById(R.id.rad_night);
        rad_other = findViewById(R.id.rad_other);
        et_time = findViewById(R.id.et_time);
        et_pjt = findViewById(R.id.et_pjt);

        Intent intent = getIntent();
        byte account = intent.getByteExtra("account", (byte) 0);
        if (account == Item.ACC_DELTA) rad_del.setChecked(true);
        String project = intent.getStringExtra("project");
        et_pjt.setText(project);
        switch (intent.getByteExtra("type", (byte) 0)) {
            case Item.TYPE_HOME_LAB:
                rad_home_lab.setChecked(true);
                break;
            case Item.TYPE_NIGHT:
                rad_night.setChecked(true);
                break;
            case Item.TYPE_OTHER:
                rad_other.setChecked(true);
        }

        findViewById(R.id.btn_ok).setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            returnIntent.putExtra("account", rad_del.isChecked());
            returnIntent.putExtra("project", et_pjt.getText().toString());
            returnIntent.putExtra("time", et_time.getText().toString());
            returnIntent.putExtra("type", rad_home_building.isChecked() ? Item.TYPE_HOME_BUILDING : rad_home_lab.isChecked() ? Item.TYPE_HOME_LAB : rad_night.isChecked() ? Item.TYPE_NIGHT : Item.TYPE_OTHER);
            finish();
        });

        findViewById(R.id.btn_cnl).setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        });
    }
}
