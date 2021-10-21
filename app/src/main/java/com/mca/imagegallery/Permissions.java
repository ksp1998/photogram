package com.mca.imagegallery;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {

    // required permissions
    static String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_CODE = 1;
    private static final int GRANTED = PackageManager.PERMISSION_GRANTED;
    private static Activity activity;

    public static boolean checkPermissions(Activity activity) {
        Permissions.activity = activity;
        return checkCameraPermission() && checkWritePermission() && checkReadPermission();
    }

    public static boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(activity, permissions[0]) == GRANTED;
    }

    public static boolean checkWritePermission() {
        return ContextCompat.checkSelfPermission(activity, permissions[1]) == GRANTED;
    }

    public static boolean checkReadPermission() {
        return ContextCompat.checkSelfPermission(activity, permissions[2]) == GRANTED;
    }

    // method to request permissions
    public static void requestPermissions() {
        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_CODE);
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE && grantResults[0] == GRANTED && grantResults[1] == GRANTED && grantResults[2] == GRANTED) {
            Utils.toast(activity, "PERMISSIONS GRANTED...");
        } else {
            Utils.toast(activity, "PERMISSIONS DENIED!");
        }
    }
}
