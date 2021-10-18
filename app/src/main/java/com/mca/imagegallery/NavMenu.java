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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NavMenu {

    private static Activity activity;
    private static AlertDialog dialog = null;
    private static File file;
    private static Uri imageUri;

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

        LinearLayout layout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.image_pick_options, (ViewGroup) null);
        ImageView btnCamera  = layout.findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(view -> openCamera());
        ImageView btnGallery = layout.findViewById(R.id.btn_gallery);
        btnGallery.setOnClickListener(view -> openGallery());

        btnAdd.setOnClickListener(view -> {

            if(dialog == null) {
                dialog = new AlertDialog.Builder(activity)
                        .setTitle("Pick Action")
                        .setIcon(R.drawable.ic_launcher_background)
                        .setView(layout)
                        .create();
            }
            dialog.show();
        });
    }

    public static void openCamera() {
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

    public static void openGallery() {
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
            uploadImage();
        }

        if(requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            file = new File(String.valueOf(imageUri));
            uploadImage();
        }

        dialog.dismiss();
    }

    private static void uploadImage() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] bytes = baos.toByteArray();

            ProgressDialog pd = new ProgressDialog(activity);
            pd.setMessage("Uploading... Please Wait!");
            pd.setCancelable(false);
            pd.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            SharedPreferences sp = activity.getSharedPreferences("shared_file", MODE_PRIVATE);
            String id = sp.getString("id", "NULL");
            String path = "imagegallery-ks/" + id + "/" + file.getName().replace('.', '_');
            StorageReference reference = storage.getReference().child(path);
            reference.putBytes(bytes)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {

                            reference.getDownloadUrl()
                                    .addOnSuccessListener(imageUri -> {

//                                        final ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, (ViewGroup) null);
//                                        Picasso.get().load(imageUri).into(imageView);
//                                        if(userGalleryRight.getChildCount() < userGalleryLeft.getChildCount()) {
//                                            userGalleryRight.addView(imageView);
//                                        } else {
//                                            userGalleryLeft.addView(imageView);
//                                        }

                                        String name = sp.getString("name", "NULL");
                                        Image image = new Image(name.concat("'s image"), Timestamp.now(), imageUri.toString());

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users")
                                                .document(id)
                                                .collection("images")
                                                .document(reference.getName())
                                                .set(image)
                                                .addOnFailureListener(ex -> Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show());
                                    })
                                    .addOnFailureListener(ex ->
                                            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        } else {
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
