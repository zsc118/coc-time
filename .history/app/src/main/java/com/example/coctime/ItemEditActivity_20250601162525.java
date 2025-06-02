package com.example.coctime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ItemEditActivity extends AppCompatActivity {
    RadioButton rad_eps, rad_del;
    EditText et_pjt, et_time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_edit);

        rad_eps = findViewById(R.id.rad_eps);
        rad_del = findViewById(R.id.rad_del);
        et_time = findViewById(R.id.et_time);
        et_pjt = findViewById(R.id.et_pjt);

        Intent intent = getIntent();
        byte account = intent.getByteExtra("account", (byte) 0);
        if (account == Item.ACC_DELTA) rad_del.setChecked(true);
        String project = intent.getStringExtra("project");
        et_pjt.setText(project);

        findViewById(R.id.btn_ok).setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            returnIntent.putExtra("account", rad_del.isChecked());
            returnIntent.putExtra("project", et_pjt.getText());
            returnIntent.putExtra("time", et_time.getText());
            finish();
        });

        findViewById(R.id.btn_cnl).setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        });
    }
}
