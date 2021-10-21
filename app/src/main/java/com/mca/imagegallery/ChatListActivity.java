package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ChatListActivity extends AppCompatActivity {
    private LinearLayout chatCard;
    private LinearLayout chatCardContainer;

    private FirebaseFirestore db;
    private FirebaseDatabase database;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        new NavMenu(this);

        chatCardContainer = findViewById(R.id.chat_card_container);

        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        listUsers();
    }

    private void listUsers() {
        String id = Utils.getID(getSharedPreferences(Utils.LOGIN_SHARED_FILE, MODE_PRIVATE).getString("email", null));

        database.getReference()
            .child(id)
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Iterable<DataSnapshot> users = task.getResult().getChildren();
                    for (DataSnapshot snapshot : users) {
                        setChatUser(snapshot.getKey());
                    }
                } else {
                    Utils.toast(this, task.getException().getMessage());
                }
            })
            .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
    }

    private void setChatUser(String id) {

        db.collection("users")
            .document(id)
            .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        addUserCard(user);
                    }
                    else {
                        Utils.toast(this, task.getException().getMessage());
                    }
                })
                .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
    }

    private void addUserCard(User user) {
        chatCard = (LinearLayout) getLayoutInflater().inflate(R.layout.chat_card, null);
        ImageView ivProfile = chatCard.findViewById(R.id.iv_profile);
        TextView tvName = chatCard.findViewById(R.id.tv_name);
        Picasso.get().load(user.getProfile_url()).into(ivProfile);
        tvName.setText(user.getName());

        chatCard.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("email", user.getEmail());
            intent.putExtra("name", user.getName());
            intent.putExtra("profile_url", user.getProfile_url());
            startActivity(intent);
        });
        chatCardContainer.addView(chatCard);
    }

//    private void setChatUser(User user) {
//        for (int i = 0; i < 10; i++) {
//            LinearLayout chatCard = (LinearLayout) getLayoutInflater().inflate(R.layout.chat_card, null);
//            chatCard.setOnClickListener(view -> startActivity(new Intent(this, ChatActivity.class)));
//            chatCardContainer.addView(chatCard);
//        }
//    }

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
}
