package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChatListActivity extends AppCompatActivity {
    LinearLayout chatCard;
    LinearLayout chatCardContainer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatCardContainer = findViewById(R.id.chat_card_container);

        setChatList();

        new NavMenu(this);
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
