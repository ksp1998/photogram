package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MyProfileActivity extends AppCompatActivity {
    private Button btnAdd;
    private Button btnChat;
    private Button btnHome;
    private Button btnMyProfile;
    private Button btnSearch;
    private ImageView imageView;
    private LinearLayout userGalleryLeft;
    private LinearLayout userGalleryRight;
    int[] userImgsIds = {R.drawable.user_img1, R.drawable.user_img2, R.drawable.user_img3, R.drawable.user_img4, R.drawable.user_img5, R.drawable.user_img6};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_my_profile);
        btnHome = findViewById(R.id.btn_home);
        btnSearch = findViewById(R.id.btn_search);
        btnAdd = findViewById(R.id.btn_add);
        btnChat = findViewById(R.id.btn_chat);
        btnMyProfile = findViewById(R.id.btn_my_profile);
        userGalleryLeft = findViewById(R.id.user_gallery_left);
        userGalleryRight = findViewById(R.id.user_gallery_right);

        btnHome.setOnClickListener(view -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        btnSearch.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchActivity.class));
        });

        btnChat.setOnClickListener(view -> {
            startActivity(new Intent(this, ChatListActivity.class));
        });

        setUserGalleryImages();
    }

    private void setUserGalleryImages() {
        userGalleryLeft.removeAllViews();
        userGalleryRight.removeAllViews();
        for (int i = 0; i < userImgsIds.length; i++) {
            imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, (ViewGroup) null);
            imageView.setImageResource(userImgsIds[i]);
            if (i % 2 == 0) {
                userGalleryLeft.addView(imageView);
            } else {
                userGalleryRight.addView(imageView);
            }
        }
    }
}
