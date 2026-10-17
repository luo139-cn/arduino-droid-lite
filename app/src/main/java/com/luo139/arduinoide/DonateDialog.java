package com.luo139.arduinoide;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DonateDialog extends Dialog {

    public DonateDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_donate);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            ImageSaver.saveDrawableToGallery(getContext(), R.drawable.donate_qrcode, "donate_qrcode.png");
        });

        findViewById(R.id.ivDonate).setOnClickListener(v -> dismiss());
    }
}
