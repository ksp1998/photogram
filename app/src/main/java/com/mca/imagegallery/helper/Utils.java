package com.mca.imagegallery.helper;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    public static User getUserFromSharedPreferences(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences(LOGIN_SHARED_FILE, MODE_PRIVATE);
        User user = new User();
        user.setEmail(sp.getString("email", null));
        user.setName(sp.getString("name", null));
        user.setCity(sp.getString("city", null));
        user.setProfile_url(sp.getString("profile_url", null));
        return user;
    }

    public static User getRecentLoggedInUser(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences(RECENT_USER_SHARED_FILE, MODE_PRIVATE);
        User user = new User();
        user.setEmail(sp.getString("email", null));
        user.setName(sp.getString("name", null));
        user.setProfile_url(sp.getString("profile_url", null));
        return user;
    }

    public static void setSharedPreferences(Activity activity, String sharedFile, User user) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(sharedFile, MODE_PRIVATE).edit();
        editor.putString("name", user.getName());
        editor.putString("city", user.getCity());
        editor.putString("email", user.getEmail());
        editor.putString("password", user.getPassword());
        editor.putString("profile_url", user.getProfile_url());
        editor.apply();
    }

    public static void addUserToSharedPreferences(Activity activity, User user) {
        setSharedPreferences(activity, LOGIN_SHARED_FILE, user);
    }

    public static void addRecentUserToSharedPreferences(Activity activity, User user) {
        setSharedPreferences(activity, RECENT_USER_SHARED_FILE, user);
    }

    public static void clearLoginPreferences(Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(LOGIN_SHARED_FILE, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static User getUserFromIntent(Activity activity) {
        User user = new User();
        user.setEmail(activity.getIntent().getStringExtra("email"));
        user.setName(activity.getIntent().getStringExtra("name"));
        user.setCity(activity.getIntent().getStringExtra("city"));
        user.setProfile_url(activity.getIntent().getStringExtra("profile_url"));
        return user;
    }

    public static Intent addUserToIntent(Intent intent, User user) {
        intent.putExtra("profile_url", user.getProfile_url());
        intent.putExtra("name", user.getName());
        intent.putExtra("city", user.getCity());
        intent.putExtra("email", user.getEmail());
        return intent;
    }

    public static byte[] compressImage(Activity activity, Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        return baos.toByteArray();
    }

    public static ProgressDialog progressDialog(Activity activity, String msg) {
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setTitle(R.string.app_name);
        pd.setIcon(R.drawable.logo);
        pd.setMessage(msg);
        pd.setCancelable(false);
        return pd;
    }

    public static BottomSheetDialog imagePickDialog(Activity activity) {

        BottomSheetDialog dialog = new BottomSheetDialog(activity);
        LinearLayout layout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.image_pick_options, null);
        RelativeLayout layoutCamera  = layout.findViewById(R.id.layout_camera);
        layoutCamera.setOnClickListener(camView -> NavMenu.openCamera(activity));
        RelativeLayout btnGallery = layout.findViewById(R.id.layout_gallery);
        btnGallery.setOnClickListener(galView -> NavMenu.openGallery(activity));
        TextView btnCancel = layout.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(view -> dialog.dismiss());


        dialog.setContentView(layout);
        dialog.setContentView(layout);
        dialog.create();

        return dialog;
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
