package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mca.imagegallery.Model.Message;
import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.Crypto;
import com.mca.imagegallery.helper.NavMenu;
import com.mca.imagegallery.helper.Permissions;
import com.mca.imagegallery.helper.Utils;
import com.squareup.picasso.Picasso;

public class ChatListActivity extends AppCompatActivity {

    private LinearLayout chatCardContainer;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseDatabase database;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        new NavMenu(this);

        chatCardContainer = findViewById(R.id.chat_card_container);
        progressBar = findViewById(R.id.progress_bar);

        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        listUsers();
    }

    private void listUsers() {

        String id =  Utils.getID((Utils.getUserFromSharedPreferences(this).getEmail()));
        progressBar.setVisibility(View.VISIBLE);

        database.getReference()
            .child(id)
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Iterable<DataSnapshot> users = task.getResult().getChildren();
                    for (DataSnapshot snapshot : users) {
                        Iterable<DataSnapshot> messages = snapshot.getChildren();
                        Message recentMessage = null;
                        for (DataSnapshot dss : messages) {
                            recentMessage = dss.getValue(Message.class);
                        }
                        setChatUser(snapshot.getKey(), recentMessage);
                    }
                } else {
                    Utils.toast(this, task.getException().getMessage());
                }
                progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(ex -> {
                Utils.toast(this, ex.getMessage());
                progressBar.setVisibility(View.GONE);
            });
    }

    private void setChatUser(String id, Message recentMessage) {

        db.collection("users")
            .document(id)
            .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        addUserCard(user, recentMessage);
                    }
                    else {
                        Utils.toast(this, task.getException().getMessage());
                    }
                })
                .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
    }

    private void addUserCard(User user, Message recentMessage) {
        LinearLayout chatCard = (LinearLayout) getLayoutInflater().inflate(R.layout.chat_card, null);
        ImageView ivProfile = chatCard.findViewById(R.id.iv_profile);
        TextView tvName = chatCard.findViewById(R.id.tv_name);
        TextView tvRecentMessage = chatCard.findViewById(R.id.tv_recent_message);

        Picasso.get().load(user.getProfile_url()).into(ivProfile);
        tvName.setText(user.getName());
        if(recentMessage != null) {
            String msg = Crypto.decrypt(recentMessage.getMessage());
            if(recentMessage.getType().equals("out")) {
                tvRecentMessage.setText("You: " + msg);
            } else {
                tvRecentMessage.setText(user.getName() + ": " + msg);
            }
        }

        chatCard.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent = Utils.addUserToIntent(intent, user);
            startActivity(intent);
        });
        chatCardContainer.addView(chatCard);
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }
}
