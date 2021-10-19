package com.mca.imagegallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utilities {

    public static void toast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static byte[] compressImage(Activity activity, Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        return baos.toByteArray();
    }

    public static Bitmap getOrientedImage(Activity activity, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);

            ExifInterface ei = new ExifInterface(uri.toString());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bitmap, 90);

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bitmap, 180);

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bitmap, 270);

                case ExifInterface.ORIENTATION_NORMAL:
                default: return bitmap;
            }
//            bitmap.recycle();
        } catch (IOException ex) {
            Utilities.toast(activity, ex.getMessage());
        }

        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static ProgressDialog progressDialog(Activity activity, String msg) {
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage(msg);
        pd.setCancelable(false);
        return pd;
    }

    public static AlertDialog pickDialog(Activity activity) {
        LinearLayout layout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.image_pick_options, null);
        ImageView btnCamera  = layout.findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(camView -> NavMenu.openCamera(activity));
        ImageView btnGallery = layout.findViewById(R.id.btn_gallery);
        btnGallery.setOnClickListener(galView -> NavMenu.openGallery(activity));

        return new AlertDialog.Builder(activity)
            .setTitle("Pick Action")
            .setIcon(R.drawable.ic_launcher_background)
            .setView(layout)
            .create();
    }
}
