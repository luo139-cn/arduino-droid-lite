package com.luo139.arduinoide;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 把 drawable 里的收款码图片保存到系统相册。
 */
public class ImageSaver {

    public static void saveDrawableToGallery(Context context, int drawableResId, String fileName) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        if (bitmap == null) {
            Toast.makeText(context, "图片加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveViaMediaStore(context, bitmap, fileName);
            } else {
                saveViaFile(context, bitmap, fileName);
            }
            Toast.makeText(context, "已保存到相册", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void saveViaMediaStore(Context context, Bitmap bitmap, String fileName) throws Exception {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) throw new Exception("无法创建媒体文件");
        OutputStream os = context.getContentResolver().openOutputStream(uri);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        os.close();
    }

    private static void saveViaFile(Context context, Bitmap bitmap, String fileName) throws Exception {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();

        // 通知相册刷新
        android.media.MediaScannerConnection.scanFile(context,
                new String[]{file.getAbsolutePath()}, null, null);
    }
}
