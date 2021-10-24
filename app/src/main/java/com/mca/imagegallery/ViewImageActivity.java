package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView ivProfile, imageView;
    private TextView tvName, tvUserId;
    private Button close;
    private RelativeLayout profileCard;
    private String profile_url, name, city, email, image_url;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ivProfile = findViewById(R.id.iv_profile);
        imageView = findViewById(R.id.image_view);
        tvName = findViewById(R.id.tv_name);
        tvUserId = findViewById(R.id.tv_user_id);
        close = findViewById(R.id.close);
        profileCard = findViewById(R.id.profile_card);

        profile_url = getIntent().getStringExtra("profile_url");
        name = getIntent().getStringExtra("name");
        city = getIntent().getStringExtra("city");
        email = getIntent().getStringExtra("email");
        image_url = getIntent().getStringExtra("image_url");

        close.setOnClickListener(view -> finish());
        profileCard.setOnClickListener(view -> gotoProfile());

        loadImage();
    }

    private void loadImage() {
        Picasso.get().load(profile_url).into(ivProfile);
        tvName.setText(name);
        tvUserId.setText("@".concat(email.substring(0, email.lastIndexOf('@'))));
        Picasso.get().load(image_url).into(imageView);
    }

    private void gotoProfile() {
        if(getIntent().getStringExtra("activity") != null) {
            finish();
            return;
        }

        Intent intent = new Intent(this, MyProfileActivity.class);
        intent.putExtra("profile_url", profile_url);
        intent.putExtra("name", name);
        intent.putExtra("city", city);
        intent.putExtra("email", email);
        startActivity(intent);
    }
}
