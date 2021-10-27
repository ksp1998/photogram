package com.mca.imagegallery;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.mca.imagegallery.Model.Image;
import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.NavMenu;
import com.mca.imagegallery.helper.Permissions;
import com.mca.imagegallery.helper.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyProfileActivity extends AppCompatActivity {
    private ImageView ivProfile;
    private TextView tvName, tvCity, tvNoPhoto;
    private Button btnBack, btnMessage, btnEdit;
    private ImageButton btnLogout;
    private LinearLayout userGalleryLeft;
    private LinearLayout userGalleryRight;
    private User user;
    private boolean otherUserProfile = false;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

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

        user = Utils.getUserFromSharedPreferences(this);

        String email = null;
        if(getIntent().getStringExtra("email") != null) {
            email = getIntent().getStringExtra("email");
        }

        if(email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
            otherUserData();
        }
        else {
            btnLogout.setOnClickListener(view -> confirmDialog());
            btnEdit.setOnClickListener(view -> startActivity(new Intent(this, EditProfile.class)));
        }

        Picasso.get().load(user.getProfile_url()).into(ivProfile);
        tvName.setText(user.getName());
        tvCity.setText(user.getCity());

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        setUserGalleryImages();
    }

    private void otherUserData() {
        otherUserProfile = true;
        user = Utils.getUserFromIntent(this);

        btnBack.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        btnMessage.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(view -> onBackPressed());
        btnMessage.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent = Utils.addUserToIntent(intent, user);
            startActivity(intent);
        });
    }

    private void confirmDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setIcon(R.drawable.logo)
            .setMessage("Are you sure?")
            .setPositiveButton("YES", (dialog, which) -> logout())
            .setNegativeButton("NO", null)
            .show();
    }

    private void logout() {
        Utils.clearLoginPreferences(this);
        startActivity(new Intent(this, MainActivity.class));
    }

    public void setUserGalleryImages() {
        userGalleryLeft.removeAllViews();
        userGalleryRight.removeAllViews();

        db.collection("users")
            .document(Utils.getID(user.getEmail()))
            .collection("images")
            .orderBy("id", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {

                    List<Image> images = task.getResult().toObjects(Image.class);
                    if(images.size() > 0) { tvNoPhoto.setVisibility(View.GONE); }

                    int i = userGalleryLeft.getChildCount() + userGalleryRight.getChildCount();
                    for (Image image : images) {
                        ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, null);
                        imageView.setOnClickListener(view -> viewImage(image));
                        imageView.setTag(image);
                        registerForContextMenu(imageView);
                        Picasso.get().load(image.getUrl()).into(imageView);

                        if (i % 2 == 0) userGalleryLeft.addView(imageView);
                        else userGalleryRight.addView(imageView);
                        i++;
                    }
                }
                else {
                    Utils.toast(this, task.getException().getMessage());
                }
            });
    }

    private void viewImage(Image image) {
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("activity", "MyProfile");
        intent.putExtra("profile_url", user.getProfile_url());
        intent.putExtra("name", user.getName());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("image_url", image.getUrl());
        startActivity(intent, Utils.getAnimationBundle(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NavMenu.onActivityResult(requestCode, resultCode, data);
    }

    ImageView imageView;
    Image image;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_image_options_menu, menu);

        if(otherUserProfile) {
            menu.findItem(R.id.delete).setVisible(false);
        }

        imageView = (ImageView) v;
        image = (Image) imageView.getTag();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.view:
                viewImage(image);
                break;
            case R.id.save:
                Utils.saveImage(this, imageView);
                break;
            case R.id.delete:
                deleteUserImage();
                break;
            case R.id.cancel: break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteUserImage() {
        db.collection("users")
            .document(Utils.getID(user.getEmail()))
            .collection("images")
            .document(String.valueOf(image.getId()))
            .delete()
            .addOnCompleteListener(task -> {
                deleteUserImageFromFirestoreImages();
                deleteUserImageFromFirebaseStorage();
                imageView.setVisibility(View.GONE);
                Utils.toast(this, "Image deleted!");
            })
            .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
    }

    private void deleteUserImageFromFirestoreImages() {
        db.collection("images")
            .document(String.valueOf(image.getId()))
            .delete()
            .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
    }

    private void deleteUserImageFromFirebaseStorage() {
        storage.getReference()
            .child("imagegallery-ks")
            .child(Utils.getID(user.getEmail()))
            .child(String.valueOf(image.getId()))
            .delete()
            .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(otherUserProfile) return;
        user = Utils.getUserFromSharedPreferences(this);

        Picasso.get().load(user.getProfile_url()).into(ivProfile);
        tvName.setText(user.getName());
        tvCity.setText(user.getCity());
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }
}