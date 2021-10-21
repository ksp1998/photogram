package com.mca.imagegallery;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    private TextView tvTitle;
    private Button btnBack, btnSend;
    private LinearLayout chatContainer;
    private EditText inputMessage;
    private ScrollView scroller;
    private String receiverId, senderId, reciverProfile, senderProfile;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_back);
        btnSend = findViewById(R.id.btn_send);
        inputMessage =  findViewById(R.id.message_box);
        chatContainer = findViewById(R.id.chat_card_container);
        scroller = findViewById(R.id.scroller);

        tvTitle.setText(getIntent().getStringExtra("name"));
        btnBack.setOnClickListener(view -> super.onBackPressed());
        btnSend.setOnClickListener(view -> sendMessage());

        receiverId = Utils.getID(getIntent().getStringExtra("email"));
        senderId = Utils.getID(getSharedPreferences(Utils.LOGIN_SHARED_FILE, MODE_PRIVATE).getString("email", null));
        reciverProfile = getIntent().getStringExtra("profile_url");
        senderProfile = getSharedPreferences(Utils.LOGIN_SHARED_FILE, MODE_PRIVATE).getString("profile_url", null);
        receiveMessage();
    }

    private void sendMessage() {
        String text = inputMessage.getText().toString().trim();
        if(!text.equals("")) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String time = String.valueOf(System.currentTimeMillis());
            Message message = new Message(text, Timestamp.now().toString(), "out");

            DatabaseReference reference = database.getReference().child(senderId).child(receiverId).child(time);
            reference.setValue(message)
                .addOnCompleteListener(task -> {
                    Utils.toast(this, "Message sent...");
                    inputMessage.getText().clear();
                })
                .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));

            message = new Message(text, Timestamp.now().toString(), "in");
            reference = database.getReference().child(receiverId).child(senderId).child(time);
            reference.setValue(message);
        }
    }

    private void receiveMessage() {
        final Activity activity = this;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(senderId).child(receiverId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listMessages(snapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utils.toast(activity, error.getMessage());
            }
        });
    }

    private void listMessages(Iterable<DataSnapshot> data) {
        chatContainer.removeAllViews();
        for (DataSnapshot snapshot : data) {
            Message message = snapshot.getValue(Message.class);
            addMessage(message);
        }
    }

    private void addMessage(Message message) {
        RelativeLayout messageCard;
        String profileUrl;
        if(message.getIn_out().equals("in")) {
            messageCard = (RelativeLayout) getLayoutInflater().inflate(R.layout.message_received, null);
            profileUrl = reciverProfile;
        } else {
            messageCard = (RelativeLayout) getLayoutInflater().inflate(R.layout.message_sent, null);
            profileUrl = senderProfile;
        }
        TextView tvMessage  = messageCard.findViewById(R.id.message);
        tvMessage.setText(message.getMessage());
        ImageView ivProfile = messageCard.findViewById(R.id.iv_profile);
        Picasso.get().load(profileUrl).into(ivProfile);
        chatContainer.addView(messageCard);
        scroller.fullScroll(View.FOCUS_DOWN);
    }
}