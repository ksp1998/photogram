package com.mca.imagegallery.helper;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mca.imagegallery.HomeActivity;
import com.mca.imagegallery.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {

    public final static String LOGIN_SHARED_FILE = "login_preferences";
    public final static String RECENT_USER_SHARED_FILE = "recent_user";

    public static void toast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getID(String email) {
        return email.replace('.', '_');
    }

    public static byte[] compressImage(Activity activity, Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        return baos.toByteArray();
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
            .setIcon(R.drawable.logo)
            .setView(layout)
            .create();
    }

    public static void setTransitions(Activity activity, int enter, int exit) {
        Transition enterTransition = TransitionInflater.from(activity).inflateTransition(enter);
        activity.getWindow().setEnterTransition(enterTransition);
        Transition exitTransition = TransitionInflater.from(activity).inflateTransition(exit);
        activity.getWindow().setExitTransition(exitTransition);
    }

    public static Bundle getAnimationBundle(Activity activity) {
        return ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
    }

    public static void startActivity(Activity activity, Class destination) {
        Intent intent = new Intent(activity, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }

    public static void saveImage(Activity activity, ImageView imageView) {

        Bitmap bitmap = ( (BitmapDrawable) imageView.getDrawable()).getBitmap();

        File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(storageLoc, System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            Utils.toast(activity, "Image saved to gallery...");
        }
        catch (IOException ex) {
            Utils.toast(activity, ex.getMessage());
        }
    }
}
