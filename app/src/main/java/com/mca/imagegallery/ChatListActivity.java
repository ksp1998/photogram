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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mca.imagegallery.Model.Message;
import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.Crypto;
import com.mca.imagegallery.helper.NavMenu;
import com.mca.imagegallery.helper.Permissions;
import com.mca.imagegallery.helper.Utils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

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

        database.getReference().child(id).orderByChild("time")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatCardContainer.removeAllViews();
                    Iterable<DataSnapshot> chatUsers = snapshot.getChildren();
                    SortedMap<Long, HashMap<String, Message>> map = new TreeMap<>();

                    for (DataSnapshot chatUser : chatUsers) {
                        Iterable<DataSnapshot> messages = chatUser.getChildren();
                        Message recentMessage = null;
                        for (DataSnapshot dss : messages) {
                            recentMessage = dss.getValue(Message.class);
                        }
                        HashMap<String, Message> values = new HashMap<>();
                        values.put(chatUser.getKey(), recentMessage);
                        map.put(recentMessage.getId(), values);
                    }

                    for (HashMap<String, Message> maps : map.values()) {
                        String key = maps.keySet().toArray()[0].toString();
                        Message message = (Message) maps.values().toArray()[0];
                        setChatUser(key, message);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
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
        chatCardContainer.addView(chatCard, 0);
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