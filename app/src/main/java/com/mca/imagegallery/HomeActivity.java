package com.mca.imagegallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class HomeActivity extends AppCompatActivity {
    Button btnAdd;
    Button btnChat;
    Button btnHome;
    Button btnMyProfile;
    Button btnSearch;
    LinearLayout card;
    LinearLayout cardContainer;
    int[] galleryImgsIds = {R.drawable.gallery_img1, R.drawable.gallery_img2, R.drawable.gallery_img3, R.drawable.gallery_img4, R.drawable.gallery_img5, R.drawable.gallery_img6, R.drawable.gallery_img7, R.drawable.gallery_img8, R.drawable.gallery_img9, R.drawable.gallery_img10};
    LinearLayout galleryLeft;
    LinearLayout galleryRight;
    int[] homeImgsIds = {R.drawable.home_img1, R.drawable.home_img2, R.drawable.home_img3, R.drawable.home_img4};
    ImageView imageView;

    private boolean doubleBackToExitPressedOnce = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_home);

        btnHome = findViewById(R.id.btn_home);
        btnSearch = findViewById(R.id.btn_search);
        btnAdd = findViewById(R.id.btn_add);
        btnChat = findViewById(R.id.btn_chat);
        btnMyProfile = findViewById(R.id.btn_my_profile);
        cardContainer = findViewById(R.id.card_container);
        galleryLeft = findViewById(R.id.gallery_left);
        galleryRight = findViewById(R.id.gallery_right);

        btnSearch.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchActivity.class));
        });

        btnChat.setOnClickListener(view -> {
            startActivity(new Intent(this, ChatListActivity.class));
        });

        btnMyProfile.setOnClickListener(view -> {
            startActivity(new Intent(this, MyProfileActivity.class));
        });

        setHomeImages();
        setGalleryImages();
    }

    private void setHomeImages() {
        cardContainer.removeAllViews();
        for (int i = 0; i < homeImgsIds.length; i++) {
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.home_card, (ViewGroup) null);
            card = linearLayout;
            imageView = linearLayout.findViewById(R.id.img);
            imageView.setMaxWidth(Resources.getSystem().getDisplayMetrics().widthPixels - (cardContainer.getPaddingRight() * 2));
            imageView.setImageResource(homeImgsIds[i]);
            final int k = i;
            card.setOnClickListener(view -> {
                viewImage(homeImgsIds[k]);
            });
            cardContainer.addView(card);
        }
    }

    private void setGalleryImages() {
        galleryLeft.removeAllViews();
        galleryRight.removeAllViews();
        int i = 0;
        while (true) {
            int[] iArr = galleryImgsIds;
            if (i < iArr.length) {
                int image_id = iArr[i];
                ImageView imageView2 = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, (ViewGroup) null);
                imageView = imageView2;
                imageView2.setOnClickListener(view -> {
                    viewImage(image_id);
                });
                imageView.setImageResource(galleryImgsIds[i]);
                if (i % 2 == 0) {
                    galleryLeft.addView(imageView);
                } else {
                    galleryRight.addView(imageView);
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void viewImage(int image_id) {
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("image_id", image_id);
        startActivity(intent);
    }

    // Override function for exiting application when double back clicked
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}
