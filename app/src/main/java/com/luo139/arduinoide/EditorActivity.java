package com.luo139.arduinoide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends AppCompatActivity {

    private static final String DEFAULT_SERVER = "http://192.168.1.100:8080";

    private Spinner spinnerBoard;
    private EditText etCode;
    private TextView tvLog;
    private SerialManager serial;

    public static void start(Context context) {
        context.startActivity(new Intent(context, EditorActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        spinnerBoard = findViewById(R.id.spinnerBoard);
        etCode = findViewById(R.id.etCode);
        tvLog = findViewById(R.id.tvLog);
        Button btnCompile = findViewById(R.id.btnCompile);
        Button btnUpload = findViewById(R.id.btnUpload);

        serial = new SerialManager(this);

        List<String> names = new ArrayList<>();
        for (BoardConfig b : BoardConfig.all()) names.add(b.name);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, names);
        spinnerBoard.setAdapter(adapter);

        etCode.setText("void setup() {\n  pinMode(LED_BUILTIN, OUTPUT);\n}\n\nvoid loop() {\n  digitalWrite(LED_BUILTIN, HIGH);\n  delay(500);\n  digitalWrite(LED_BUILTIN, LOW);\n  delay(500);\n}\n");

        btnCompile.setOnClickListener(v -> doCompile());
        btnUpload.setOnClickListener(v -> doUpload());
    }

    private BoardConfig selectedBoard() {
        return BoardConfig.all().get(spinnerBoard.getSelectedItemPosition());
    }

    private void log(String msg) {
        runOnUiThread(() -> tvLog.append("\n" + msg));
    }

    private void doCompile() {
        BoardConfig board = selectedBoard();
        String code = etCode.getText().toString();
        log("== 编译 " + board.name + " ==");
        CompileClient.compile(DEFAULT_SERVER, board.fqbn, code,
                new CompileClient.CallbackFn() {
                    @Override public void onResult(String firmware, String type, String log) {
                        log("编译成功，固件类型 " + type + "，大小 " + firmware.length());
                        if (log != null && !log.isEmpty()) log(log);
                    }
                    @Override public void onError(String message) {
                        log(message);
                    }
                });
    }

    private void doUpload() {
        BoardConfig board = selectedBoard();
        log("== 烧录 " + board.name + " ==");
        log("提示：请先在编译页获取固件（当前为协议演示）");
        Uploader uploader = new Uploader(serial, new Uploader.Listener() {
            @Override public void onProgress(String msg) { log(msg); }
            @Override public void onDone(boolean success, String msg) { log(success ? "完成：" : "失败：" + msg); }
        });
        uploader.upload(board, "");
    }
}
