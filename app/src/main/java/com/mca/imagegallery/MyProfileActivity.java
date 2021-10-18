package com.mca.imagegallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;

public class MyProfileActivity extends AppCompatActivity {
    private ImageView ivProfile;
    private TextView tvName, tvCity;
    private Button btnLogout, btnMessage, btnEdit;
    private LinearLayout userGalleryLeft;
    private LinearLayout userGalleryRight;
    private File file;
    String id, name, city, profile_url;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_my_profile);

        new NavMenu(this);

        ivProfile = findViewById(R.id.iv_profile);
        tvName = findViewById(R.id.tv_name);
        tvCity = findViewById(R.id.tv_city);
        btnLogout = findViewById(R.id.btn_logout);
        btnMessage = findViewById(R.id.btn_message);
        btnEdit = findViewById(R.id.btn_edit);
        userGalleryLeft = findViewById(R.id.user_gallery_left);
        userGalleryRight = findViewById(R.id.user_gallery_right);

        SharedPreferences sp = getSharedPreferences("shared_file", MODE_PRIVATE);
        id = sp.getString("id", "NULL");
        name = sp.getString("name", "NULL");
        city = sp.getString("city", "NULL");
        profile_url = sp.getString("profile_url", "NULL");

        Picasso.get().load(profile_url).into(ivProfile);
        tvName.setText(name);
        tvCity.setText(city);

        btnLogout.setOnClickListener(view -> confirmDialog());
        btnEdit.setOnClickListener(view -> startActivity(new Intent(this, EditProfile.class)));

        setUserGalleryImages();
    }

    private void confirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure?")
                .setIcon(R.drawable.ic_launcher_background)
                .setPositiveButton("YES", (dialog, which) -> logout())
                .setNegativeButton("NO", null)
                .show();
    }

    private void logout() {
        SharedPreferences sp = getSharedPreferences("shared_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
    }

//    private void uploadImage() {
//        try {
//            Uri uri = Uri.fromFile(file);
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
//            byte[] bytes = baos.toByteArray();
//
//            ProgressDialog pd = new ProgressDialog(this);
//            pd.setMessage("Uploading... Please Wait!");
//            pd.setCancelable(false);
//            pd.show();
//
//            FirebaseStorage storage = FirebaseStorage.getInstance();
//            String path = "imagegallery-ks/" + id + "/" + file.getName().replace('.', '_');
//            StorageReference reference = storage.getReference().child(path);
//            reference.putBytes(bytes)
//                    .addOnCompleteListener(task -> {
//                        if(task.isSuccessful()) {
//
//                            reference.getDownloadUrl()
//                                    .addOnSuccessListener(imageUri -> {
//
//                                        final ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, (ViewGroup) null);
//                                        Picasso.get().load(imageUri).into(imageView);
//                                        if(userGalleryRight.getChildCount() < userGalleryLeft.getChildCount()) {
//                                            userGalleryRight.addView(imageView);
//                                        } else {
//                                            userGalleryLeft.addView(imageView);
//                                        }
//
//                                        Image image = new Image(name.concat("'s image"), Timestamp.now(), imageUri.toString());
//
//                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                        db.collection("users")
//                                                .document(id)
//                                                .collection("images")
//                                                .document(reference.getName())
//                                                .set(image)
//                                                .addOnFailureListener(ex ->
//                                                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show()
//                                                );
//                                    })
//                                    .addOnFailureListener(ex ->
//                                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show()
//                                    );
//                        } else {
//                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                        pd.dismiss();
//                    });
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void setUserGalleryImages() {
        userGalleryLeft.removeAllViews();
        userGalleryRight.removeAllViews();

//        for (int i = 0; i < userImgsIds.length; i++) {
//            imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, (ViewGroup) null);
//            imageView.setImageResource(userImgsIds[i]);
//            if (i % 2 == 0) {
//                userGalleryLeft.addView(imageView);
//            } else {
//                userGalleryRight.addView(imageView);
//            }
//        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(id)
                .collection("images")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        int i = 0;
                        for(DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            String url = snapshot.get("url", String.class);
                            final ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, (ViewGroup) null);
                            imageView.setOnClickListener(view -> viewImage(url));
                            Picasso.get().load(url).into(imageView);
                            if (i % 2 == 0) {
                                userGalleryLeft.addView(imageView);
                            } else {
                                userGalleryRight.addView(imageView);
                            }
                            i++;
                        }
                    } else {
                         Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void viewImage(String url) {
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    // override method to perform action on permission grant and revoke
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // override method which will be called on when image is captured or selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NavMenu.onActivityResult(requestCode, resultCode, data);
    }
}
