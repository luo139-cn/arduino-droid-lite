package com.luo139.arduinoide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;

public class SerialMonitorActivity extends AppCompatActivity {

    private SerialManager serial;
    private TextView tvMonitor;
    private EditText etSend;
    private final StringBuilder buffer = new StringBuilder();

    public static void start(Context context) {
        context.startActivity(new Intent(context, SerialMonitorActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_monitor);

        tvMonitor = findViewById(R.id.tvMonitor);
        etSend = findViewById(R.id.etSend);
        Button btnConnect = findViewById(R.id.btnConnect);
        Button btnSend = findViewById(R.id.btnSend);

        serial = new SerialManager(this);
        serial.setListener(new SerialManager.Listener() {
            @Override public void onData(byte[] data) {
                buffer.append(new String(data, StandardCharsets.UTF_8));
                runOnUiThread(() -> tvMonitor.setText(buffer.toString()));
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> tvMonitor.append("\n[错误] " + message));
            }
            @Override public void onStatus(String message) {
                runOnUiThread(() -> tvMonitor.append("\n[状态] " + message));
            }
        });

        btnConnect.setOnClickListener(v -> {
            if (serial.connect()) serial.startReading();
        });

        btnSend.setOnClickListener(v -> {
            String text = etSend.getText().toString();
            if (!text.isEmpty()) {
                serial.write((text + "\n").getBytes(StandardCharsets.UTF_8));
                etSend.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serial != null) serial.close();
    }
}
