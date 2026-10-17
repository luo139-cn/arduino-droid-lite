package com.luo139.arduinoide;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnNew).setOnClickListener(v ->
                EditorActivity.start(this));

        findViewById(R.id.btnOpen).setOnClickListener(v ->
                Toast.makeText(this, "存储打开功能开发中", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnMonitor).setOnClickListener(v ->
                SerialMonitorActivity.start(this));

        findViewById(R.id.btnSettings).setOnClickListener(v ->
                Toast.makeText(this, "设置功能开发中", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnDonate).setOnClickListener(v -> {
            DonateDialog dialog = new DonateDialog(this);
            dialog.show();
        });
    }
}
