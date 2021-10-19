package com.mca.imagegallery;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    private TextView tvTitle;
    private Button btnBack, btnSend;
    private LinearLayout chatContainer;
    private EditText inputMessage;
    RelativeLayout messageCard;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_back);
        btnSend = findViewById(R.id.btn_send);
        inputMessage =  findViewById(R.id.message_box);
        chatContainer = findViewById(R.id.chat_card_container);

        tvTitle.setText(getIntent().getStringExtra("name"));
        btnBack.setOnClickListener(view -> super.onBackPressed());
        btnSend.setOnClickListener(view -> sendMessage());
    }

    private void sendMessage() {
        String message = inputMessage.getText().toString();
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.message_sent, (ViewGroup) null);
        messageCard = relativeLayout;
        final TextView tvMessage  = relativeLayout.findViewById(R.id.message);
        tvMessage.setText(message);
        chatContainer.addView(messageCard);
        inputMessage.setText("");
    }
}
