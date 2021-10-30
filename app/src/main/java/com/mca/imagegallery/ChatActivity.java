package com.mca.imagegallery;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mca.imagegallery.Model.Message;
import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.Crypto;
import com.mca.imagegallery.helper.Utils;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    private TextView tvTitle;
    private Button btnBack, btnSend;
    private LinearLayout chatContainer;
    private EditText inputMessage;
    private ScrollView scroller;
    private ProgressBar progressBar;
    private String receiverId, senderId, receiverProfile, senderProfile;
    private User user;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_back);
        btnSend = findViewById(R.id.btn_send);
        inputMessage =  findViewById(R.id.message_box);
        chatContainer = findViewById(R.id.chat_card_container);
        scroller = findViewById(R.id.scroller);
        progressBar = findViewById(R.id.progress_bar);

        user = Utils.getUserFromIntent(this);

        tvTitle.setText(user.getName());
        tvTitle.setOnClickListener(view -> gotoProfile());
        btnBack.setOnClickListener(view -> onBackPressed());
        btnSend.setOnClickListener(view -> sendMessage());

        receiverId = Utils.getID(user.getEmail());
        senderId = Utils.getID(getSharedPreferences(Utils.LOGIN_SHARED_FILE, MODE_PRIVATE).getString("email", null));
        receiverProfile = user.getProfile_url();
        senderProfile = getSharedPreferences(Utils.LOGIN_SHARED_FILE, MODE_PRIVATE).getString("profile_url", null);

        database = FirebaseDatabase.getInstance();

        listMessages();
    }

    private void gotoProfile() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        intent = Utils.addUserToIntent(intent, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void sendMessage() {
        String text = inputMessage.getText().toString().trim();
        if(!text.equals("")) {

            long id = System.currentTimeMillis();
            Message message = new Message(id, Crypto.encrypt(text), "out");

            reference = database.getReference().child(senderId).child(receiverId);
            reference.child(String.valueOf(id))
                .setValue(message)
                .addOnCompleteListener(task -> {
                    // Utils.toast(this, "Message sent...");
                    inputMessage.getText().clear();
                    scroller.fullScroll(View.FOCUS_DOWN);
                })
                .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));

            message = new Message(id, Crypto.encrypt(text), "in");
            reference = database.getReference().child(receiverId).child(senderId);
            reference.child(String.valueOf(id))
                .setValue(message)
                .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
        }
    }

    private String previousMessageDate;
    private void listMessages() {
        chatContainer.removeAllViews();
        progressBar.setVisibility(View.VISIBLE);
        previousMessageDate = null;

        receiveMessages();
    }

    private void receiveMessages() {

        reference = database.getReference().child(senderId).child(receiverId);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                addMessage(message);
                scroller.fullScroll(View.FOCUS_DOWN);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                TextView deletedMessage = chatContainer
                        .findViewWithTag(snapshot.child("id").getValue())
                        .findViewById(R.id.message);
                if(deletedMessage != null) {
                    deletedMessage.setTextColor(Color.RED);
                    deletedMessage.setText("deleted");
                    unregisterForContextMenu(deletedMessage);
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addMessage(Message message) {
        int layout;
        String profileUrl;
        if(message.getType().equals("in")) {
            layout = R.layout.message_received;
            profileUrl = receiverProfile;
        } else {
            layout = R.layout.message_sent;
            profileUrl = senderProfile;
        }
        RelativeLayout messageCard = (RelativeLayout) getLayoutInflater().inflate(layout, null);

        TextView tvMessage  = messageCard.findViewById(R.id.message);
        tvMessage.setText(Crypto.decrypt(message.getMessage()));
        tvMessage.setTag(message);
        // tvMessage.setContentDescription(""+message.getId());
        messageCard.setTag(message.getId());
        registerForContextMenu(tvMessage);

        ImageView ivProfile = messageCard.findViewById(R.id.iv_profile);
        Picasso.get().load(profileUrl).into(ivProfile);

        String date = Utils.getDate(message.getId());
        if(previousMessageDate == null || !previousMessageDate.equals(date)) {
            TextView tvDate = messageCard.findViewById(R.id.tv_date);
            tvDate.setText(date);
            tvDate.setVisibility(View.VISIBLE);
        }
        previousMessageDate = date;

        TextView tvTime = messageCard.findViewById(R.id.tv_time);
        tvTime.setText(Utils.getTime(message.getId()));
        chatContainer.addView(messageCard);
        scroller.arrowScroll(View.FOCUS_DOWN);
    }

    Message message;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_options_menu, menu);

        message = (Message) v.getTag();
        if(message.getType().equals("in")) {
            menu.findItem(R.id.delete_for_both).setEnabled(false);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("message", Crypto.decrypt(message.getMessage()));
                clipboard.setPrimaryClip(data);
                Utils.toast(this, "Message copied!");
                break;
            case R.id.delete_for_me:
                deleteMessageForMe();
                break;
            case R.id.delete_for_both:
                deleteMessageForBoth();
                break;
            case R.id.cancel: break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteMessageForMe() {
        database.getReference().child(senderId).child(receiverId)
            .child(String.valueOf(message.getId()))
            .removeValue()
            // .addOnCompleteListener(task -> Utils.toast(this, "Message deleted!"))
            .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
    }

    private void deleteMessageForBoth() {
        deleteMessageForMe();
        database.getReference().child(receiverId).child(senderId)
            .child(String.valueOf(message.getId()))
            .removeValue()
            // .addOnCompleteListener(task -> Utils.toast(this, "Message deleted!"))
            .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
    }
}