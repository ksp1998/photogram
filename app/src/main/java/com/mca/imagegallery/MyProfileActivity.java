package com.mca.imagegallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyProfileActivity extends AppCompatActivity {
    private ImageView ivProfile;
    private TextView tvName, tvCity, tvNoPhoto;
    private Button btnBack, btnLogout, btnMessage, btnEdit;
    private LinearLayout userGalleryLeft;
    private LinearLayout userGalleryRight;
    private String id, name, city, profile_url;
    private boolean otherUserProfile = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_my_profile);

        new NavMenu(this);

        ivProfile = findViewById(R.id.iv_profile);
        tvName = findViewById(R.id.tv_name);
        tvCity = findViewById(R.id.tv_city);
        tvNoPhoto = findViewById(R.id.tv_no_photo);
        btnBack = findViewById(R.id.btn_back);
        btnLogout = findViewById(R.id.btn_logout);
        btnMessage = findViewById(R.id.btn_message);
        btnEdit = findViewById(R.id.btn_edit);
        userGalleryLeft = findViewById(R.id.user_gallery_left);
        userGalleryRight = findViewById(R.id.user_gallery_right);

        SharedPreferences sp = getSharedPreferences("shared_file", MODE_PRIVATE);
        id = sp.getString("id", "NULL");

        String temp = null;
        if(getIntent().getStringExtra("email") != null) {
            temp = getIntent().getStringExtra("email").replace('.', '_');
        }

        if(temp != null && !temp.equals(id)) {
            id = temp;
            otherUserData();
        }
        else {
            name = sp.getString("name", "NULL");
            city = sp.getString("city", "NULL");
            profile_url = sp.getString("profile_url", "NULL");

            btnLogout.setOnClickListener(view -> confirmDialog());
            btnEdit.setOnClickListener(view -> startActivity(new Intent(this, EditProfile.class)));
        }

        Picasso.get().load(profile_url).into(ivProfile);
        tvName.setText(name);
        tvCity.setText(city);

        setUserGalleryImages();
    }

    private void otherUserData() {
        otherUserProfile = true;
        profile_url = getIntent().getStringExtra("profile_url");
        name = getIntent().getStringExtra("name");
        city = getIntent().getStringExtra("city");

        btnBack.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        btnMessage.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(view -> super.onBackPressed());
        btnMessage.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("id", id);
            startActivity(intent);
        });
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

    private void setUserGalleryImages() {
        userGalleryLeft.removeAllViews();
        userGalleryRight.removeAllViews();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
            .document(id)
            .collection("images")
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(task.getResult().getDocuments().size() > 0) {
                        tvNoPhoto.setVisibility(View.GONE);
                    }

                    List<Image> images = task.getResult().toObjects(Image.class);
                    int i = userGalleryLeft.getChildCount() + userGalleryRight.getChildCount();
                    for (Image image : images) {
                        final ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, null);
                        imageView.setOnClickListener(view -> viewImage(image));
                        Picasso.get().load(image.getUrl()).into(imageView);
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

    private void viewImage(Image image) {
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("activity", "MyProfile");
        intent.putExtra("profile_url", profile_url);
        intent.putExtra("name", name);
        intent.putExtra("email", id.replaceAll("_", "."));
        intent.putExtra("image_url", image.getUrl());
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

    @Override
    protected void onResume() {
        super.onResume();
        if(otherUserProfile) return;
        SharedPreferences sp = getSharedPreferences("shared_file", MODE_PRIVATE);
        name = sp.getString("name", "NULL");
        city = sp.getString("city", "NULL");
        profile_url = sp.getString("profile_url", "NULL");

        Picasso.get().load(profile_url).into(ivProfile);
        tvName.setText(name);
        tvCity.setText(city);
    }
}