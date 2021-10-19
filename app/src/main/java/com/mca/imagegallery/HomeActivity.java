package com.mca.imagegallery;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    LinearLayout card, cardContainer;
    LinearLayout galleryLeft, galleryRight;
    int[] homeImgsIds = {R.drawable.home_img1, R.drawable.home_img2, R.drawable.home_img3, R.drawable.home_img4};

    private boolean doubleBackToExitPressedOnce = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_home);

        new NavMenu(this);

        cardContainer = findViewById(R.id.card_container);
        galleryLeft = findViewById(R.id.gallery_left);
        galleryRight = findViewById(R.id.gallery_right);

        setHomeImages();
        browseImages();
    }

    private void setHomeImages() {
        cardContainer.removeAllViews();
        for (int i = 0; i < homeImgsIds.length; i++) {
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.home_card, (ViewGroup) null);
            card = linearLayout;
            ImageView imageView = linearLayout.findViewById(R.id.iv_home_img);
            imageView.setMaxWidth(Resources.getSystem().getDisplayMetrics().widthPixels - (cardContainer.getPaddingRight() * 2));
            imageView.setImageResource(homeImgsIds[i]);
//            final int k = i;
//            card.setOnClickListener(view -> viewImage(homeImgsIds[k]));
            cardContainer.addView(card);
        }
    }

    private void browseImages() {
        galleryLeft.removeAllViews();
        galleryRight.removeAllViews();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    List<User> users = task.getResult().toObjects(User.class);
                    for (final User user : users) {
                        displayImage(user);
                    }
                } else {
                    Utilities.toast(this, task.getException().getMessage());
                }
            })
            .addOnFailureListener(ex -> Utilities.toast(this, ex.getMessage()));
    }

    private void displayImage(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
            .document(user.getEmail().replace('.', '_'))
            .collection("images")
            .get()
            .addOnCompleteListener(task1 -> {
                List<Image> images = task1.getResult().toObjects(Image.class);
                int i = galleryLeft.getChildCount() + galleryRight.getChildCount();
                for (final Image image : images) {
                    final ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, null);
                    imageView.setOnClickListener(view -> viewImage(image, user));
                    Picasso.get().load(image.getUrl()).into(imageView);
                    if (i++ % 2 == 0) {
                        galleryLeft.addView(imageView);
                    } else {
                        galleryRight.addView(imageView);
                    }
                }
            })
            .addOnFailureListener(ex -> Utilities.toast(this, ex.getMessage()));
    }

    private void viewImage(Image image, User user) {
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("profile_url", user.getProfile_url());
        intent.putExtra("name", user.getName());
        intent.putExtra("city", user.getCity());
        intent.putExtra("email", user.getEmail());
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

    // Override function for exiting application when double back clicked
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            return;
        }
        doubleBackToExitPressedOnce = true;
        Utilities.toast(this, "Press BACK again to exit");

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}
