package com.mca.imagegallery.helper;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mca.imagegallery.ChatListActivity;
import com.mca.imagegallery.EditProfile;
import com.mca.imagegallery.HomeActivity;
import com.mca.imagegallery.Model.GalleryImage;
import com.mca.imagegallery.Model.Image;
import com.mca.imagegallery.MyProfileActivity;
import com.mca.imagegallery.R;
import com.mca.imagegallery.SearchActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NavMenu {

    private static Activity activity;
    public static AlertDialog dialog = null;
    public static ProgressDialog pd;
    public static File file;
    public static Uri imageUri;
    public static String url;

    public static final int CAMERA_REQUEST = 100;
    public static final int GALLERY_REQUEST = 101;

    private static SharedPreferences sp;
    private static String id;
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;
    private static StorageReference reference;

    public NavMenu(Activity activity) {

        NavMenu.activity = activity;
        Button btnHome = activity.findViewById(R.id.btn_home);
        Button btnSearch = activity.findViewById(R.id.btn_search);
        Button btnAdd = activity.findViewById(R.id.btn_add);
        Button btnChat = activity.findViewById(R.id.btn_chat);
        Button btnMyProfile = activity.findViewById(R.id.btn_my_profile);

        btnHome.setOnClickListener(view -> Utils.startActivity(activity, HomeActivity.class));
        btnSearch.setOnClickListener(view -> Utils.startActivity(activity, SearchActivity.class));
        btnChat.setOnClickListener(view -> Utils.startActivity(activity, ChatListActivity.class));
        btnMyProfile.setOnClickListener(view -> Utils.startActivity(activity, MyProfileActivity.class));

        Class<? extends Activity> aClass = activity.getClass();
        if (HomeActivity.class.equals(aClass)) {
            btnHome.setOnClickListener(null);
            btnHome.setBackground(activity.getDrawable(R.drawable.menu_ic_home_active));
        }
        if (SearchActivity.class.equals(aClass)) {
            btnSearch.setOnClickListener(null);
            btnSearch.setBackground(activity.getDrawable(R.drawable.menu_ic_search_active));
        }
        if (ChatListActivity.class.equals(aClass)) {
            btnChat.setOnClickListener(null);
            btnChat.setBackground(activity.getDrawable(R.drawable.menu_ic_chat_active));
        }
        if (MyProfileActivity.class.equals(aClass)) {
            btnMyProfile.setOnClickListener(null);
            btnMyProfile.setBackground(activity.getDrawable(R.drawable.menu_ic_user_active));
        }

        btnAdd.setOnClickListener(view -> {
            dialog = Utils.pickDialog(activity);
            dialog.show();
        });

        sp = activity.getSharedPreferences(Utils.LOGIN_SHARED_FILE, MODE_PRIVATE);
        id = Utils.getID(sp.getString("email", null));

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static void openCamera(Activity activity) {
        NavMenu.activity = activity;
        if(!Permissions.checkPermissions(activity)) {
            Permissions.requestPermissions();
        }

        if(Permissions.checkPermissions(activity)) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
            file = new File(Environment.getExternalStorageDirectory(), "/photo_" + timeStamp + ".jpeg");
            imageUri = Uri.fromFile(file);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            activity.startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    public static void openGallery(Activity activity) {
        NavMenu.activity = activity;
        if(!Permissions.checkPermissions(activity)) {
            Permissions.requestPermissions();
        }

        if(Permissions.checkPermissions(activity)) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            activity.startActivityForResult(intent, GALLERY_REQUEST);
        }
    }

    public static void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if(activity.getClass() != EditProfile.class) {
                uploadImage();
            }
        }

        if(requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            file = new File(String.valueOf(imageUri));
            if(activity.getClass() != EditProfile.class) {
                uploadImage();
            }
        }

        if(dialog != null) {
            dialog.dismiss();
        }
    }

    private static void uploadImage() {
        try {
            byte[] bytes = Utils.compressImage(activity, imageUri);

            pd = Utils.progressDialog(activity, "Uploading... Please Wait!");
            pd.show();

            uploadImageToFirebaseStorage(bytes);
        }
        catch (IOException ex) {
            Utils.toast(activity, ex.getMessage());
        }
    }

    private static void uploadImageToFirebaseStorage(byte[] bytes) {
        long imageId = System.currentTimeMillis();
        reference = storage.getReference().child("imagegallery-ks/" + id + "/" + imageId);
        reference.putBytes(bytes)
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    reference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String name = sp.getString("name", null);
                            url = uri.toString();

                            Image image = new Image(imageId, name.concat("'s image"), Timestamp.now(), url);
                            uploadImageUrlToFirestoreUsers(image);

                            GalleryImage galleryImage = new GalleryImage(imageId, url, db.collection("users").document(id));
                            uploadImageUrlToFirestoreImages(galleryImage);
                        })
                        .addOnFailureListener(ex -> {
                            Utils.toast(activity, ex.getMessage());
                            pd.dismiss();
                        });
                }
                else {
                    Utils.toast(activity, task.getException().getMessage());
                    pd.dismiss();
                }
            })
            .addOnFailureListener(ex -> {
                Utils.toast(activity, ex.getMessage());
                pd.dismiss();
            });
    }

    private static void uploadImageUrlToFirestoreUsers(Image image) {

        db.collection("users")
            .document(id)
            .collection("images")
            .document(reference.getName())
            .set(image)
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(activity.getClass() == MyProfileActivity.class) {
                        activity.recreate();
                    } else {
                        activity.startActivity(new Intent(activity, MyProfileActivity.class));
                    }
                    Utils.toast(activity, "Image uploaded...");
                } else {
                    Utils.toast(activity, task.getException().getMessage());
                }
                pd.dismiss();
            })
            .addOnFailureListener(ex -> {
                Utils.toast(activity, ex.getMessage());
                pd.dismiss();
            });
    }

    private static void uploadImageUrlToFirestoreImages(GalleryImage image) {

        db.collection("images")
            .document(String.valueOf(image.getId()))
            .set(image)
            .addOnFailureListener(ex -> Utils.toast(activity, ex.getMessage()));
    }
}