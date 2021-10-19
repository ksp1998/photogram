package com.mca.imagegallery;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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

    public NavMenu(Activity activity) {

        NavMenu.activity = activity;
        Button btnHome = activity.findViewById(R.id.btn_home);
        Button btnSearch = activity.findViewById(R.id.btn_search);
        Button btnAdd = activity.findViewById(R.id.btn_add);
        Button btnChat = activity.findViewById(R.id.btn_chat);
        Button btnMyProfile = activity.findViewById(R.id.btn_my_profile);

        btnHome.setOnClickListener(view -> activity.startActivity(new Intent(activity, HomeActivity.class)));
        btnSearch.setOnClickListener(view -> activity.startActivity(new Intent(activity, SearchActivity.class)));
        btnChat.setOnClickListener(view -> activity.startActivity(new Intent(activity, ChatListActivity.class)));
        btnMyProfile.setOnClickListener(view -> activity.startActivity(new Intent(activity, MyProfileActivity.class)));

        Class<? extends Activity> aClass = activity.getClass();
        if (HomeActivity.class.equals(aClass)) {
            btnHome.setOnClickListener(null);
        }
        if (SearchActivity.class.equals(aClass)) {
            btnSearch.setOnClickListener(null);
        }
        if (ChatListActivity.class.equals(aClass)) {
            btnChat.setOnClickListener(null);
        }
        if (MyProfileActivity.class.equals(aClass)) {
            btnMyProfile.setOnClickListener(null);
        }

        btnAdd.setOnClickListener(view -> {
            dialog = Utilities.pickDialog(activity);
            dialog.show();
        });
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
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            activity.startActivityForResult(gallery, GALLERY_REQUEST);
        }
    }

    protected static void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
            byte[] bytes = Utilities.compressImage(activity, imageUri);

            pd = new ProgressDialog(activity);
            pd.setMessage("Uploading... Please Wait!");
            pd.setCancelable(false);
            pd.show();

            uploadImageToFirebaseStorage(bytes);
        }
        catch (IOException ex) {
            Utilities.toast(activity, ex.getMessage());
        }
    }

    private static void uploadImageToFirebaseStorage(byte[] bytes) {

        SharedPreferences sp = activity.getSharedPreferences("shared_file", MODE_PRIVATE);
        String id = sp.getString("id", "NULL");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("imagegallery-ks/" + id + "/" + file.getName());
        reference.putBytes(bytes)
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    reference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String name = sp.getString("name", "NULL");
                            url = uri.toString();
                            Image image = new Image(name.concat("'s image"), Timestamp.now(), url);

                            uploadImageUrlToFireStore(reference, image);
                        })
                        .addOnFailureListener(ex -> Utilities.toast(activity, ex.getMessage()));
                }
                else {
                    Utilities.toast(activity, task.getException().getMessage());
                }
                pd.dismiss();
            });
    }

    private static void uploadImageUrlToFireStore(StorageReference reference, Image image) {
        SharedPreferences sp = activity.getSharedPreferences("shared_file", MODE_PRIVATE);
        String id = sp.getString("id", "NULL");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
            .document(id)
            .collection("images")
            .document(reference.getName())
            .set(image)
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(activity.getClass() == MyProfileActivity.class) {
                        updateUserGallery();
                    } else {
                        Utilities.toast(activity, "Image uploaded...");
                    }
                } else {
                    Utilities.toast(activity, task.getException().getMessage());
                }
            })
            .addOnFailureListener(ex -> Utilities.toast(activity, ex.getMessage()));
    }

    private static void updateUserGallery() {
        final ImageView imageView = (ImageView) activity.getLayoutInflater().inflate(R.layout.gallery_image, null);
        Picasso.get().load(imageUri).into(imageView);

        LinearLayout userGalleryLeft = activity.findViewById(R.id.user_gallery_left);
        LinearLayout userGalleryRight = activity.findViewById(R.id.user_gallery_right);

        activity.findViewById(R.id.tv_no_photo).setVisibility(View.GONE);

        if(userGalleryRight.getChildCount() < userGalleryLeft.getChildCount()) {
            userGalleryRight.addView(imageView, 0);
        } else {
            userGalleryLeft.addView(imageView, 0);
        }
    }
}

//                    activity.finish();
//                    activity.overridePendingTransition(0, 0);
//                    activity.startActivity(activity.getIntent());
//                    activity.overridePendingTransition(0, 0);