package com.mca.imagegallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {

    public final static String LOGIN_SHARED_FILE = "shared_file";
    public final static String RECENT_USER_SHARED_FILE = "recent_user";

    public static void toast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static byte[] compressImage(Activity activity, Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        return baos.toByteArray();
    }

//    public static void getOrientedImage(Activity activity, Uri uri) {
//        Bitmap bitmap, rotatedBitmap;
//        try {
//            bitmap = BitmapFactory.decodeFile(uri.toString());
//
//            ExifInterface ei = new ExifInterface(uri.toString());
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotatedBitmap = rotateImage(bitmap, 90);
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotatedBitmap = rotateImage(bitmap, 180);
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotatedBitmap = rotateImage(bitmap, 270);
//                    break;
//
//                case ExifInterface.ORIENTATION_NORMAL:
//                default:
//                    rotatedBitmap = bitmap;
//            }
//            if (rotatedBitmap != bitmap) {
//                FileOutputStream fOut = new FileOutputStream(uri.toString());
//                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//                fOut.flush();
//                fOut.close();
//            }
//            bitmap.recycle();
//            rotatedBitmap.recycle();
//        } catch (IOException ex) {
//            Utils.toast(activity, ex.getMessage());
//        }
//    }

//    public static Bitmap rotateImage(Bitmap source, float angle) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//    }

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

    public static String getID(String email) {
        return email.replace('.', '_');
    }
}
