package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    Button btnAdd;
    Button btnChat;
    Button btnHome;
    Button btnMyProfile;
    Button btnSearch;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btnHome = findViewById(R.id.btn_home);
        btnSearch = findViewById(R.id.btn_search);
        btnAdd = findViewById(R.id.btn_add);
        btnChat = findViewById(R.id.btn_chat);
        btnMyProfile = findViewById(R.id.btn_my_profile);

        btnHome.setOnClickListener(view -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        btnChat.setOnClickListener(view -> {
            startActivity(new Intent(this, ChatListActivity.class));
        });

        btnMyProfile.setOnClickListener(view -> {
            startActivity(new Intent(this, MyProfileActivity.class));
        });
    }
}
