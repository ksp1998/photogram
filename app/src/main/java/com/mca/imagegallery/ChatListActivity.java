package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ChatListActivity extends AppCompatActivity {
    Button btnAdd;
    Button btnChat;
    Button btnHome;
    Button btnMyProfile;
    Button btnSearch;
    LinearLayout chatCard;
    LinearLayout chatCardContainer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        btnHome = findViewById(R.id.btn_home);
        btnSearch = findViewById(R.id.btn_search);
        btnAdd = findViewById(R.id.btn_add);
        btnChat = findViewById(R.id.btn_chat);
        btnMyProfile = findViewById(R.id.btn_my_profile);
        chatCardContainer = findViewById(R.id.chat_card_container);

        btnHome.setOnClickListener(view -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        btnSearch.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchActivity.class));
        });

        btnMyProfile.setOnClickListener(view -> {
            startActivity(new Intent(this, MyProfileActivity.class));
        });

        setChatList();
    }

    private void setChatList() {
        chatCardContainer.removeAllViews();
        for (int i = 0; i < 10; i++) {
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.chat_card, (ViewGroup) null);
            chatCard = linearLayout;
            linearLayout.setOnClickListener(view -> {
                startActivity(new Intent(this, ChatActivity.class));
            });

            chatCardContainer.addView(chatCard);
        }
    }
}
