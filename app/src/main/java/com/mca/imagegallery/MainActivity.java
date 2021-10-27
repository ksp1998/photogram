package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.Utils;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;

    private User loggedInUser;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loggedInUser = Utils.getUserFromSharedPreferences(this);

        isUserLoggedIn();
        addRecentUser();

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));
        btnRegister.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void isUserLoggedIn() {
        if(loggedInUser.getEmail() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(0, 0);
        }
    }

    private void addRecentUser() {
        User recentUser = Utils.getRecentLoggedInUser(this);
        if(recentUser.getEmail() != null) {

            LinearLayout userCard = (LinearLayout) getLayoutInflater().inflate(R.layout.user_card, null);
            userCard.setOnClickListener(view -> {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("email", recentUser.getEmail());
                startActivity(intent);
            });

            ImageView ivProfile = userCard.findViewById(R.id.iv_profile);
            TextView tvName = userCard.findViewById(R.id.tv_name);
            TextView tvEmail = userCard.findViewById(R.id.tv_user_id);

            Picasso.get().load(recentUser.getProfile_url()).into(ivProfile);
            tvName.setText(recentUser.getName());
            tvEmail.setText(recentUser.getEmail());

            RelativeLayout parent = findViewById(R.id.container);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            parent.addView(userCard , layoutParams);
        }
    }

    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Utils.toast(this, "Press BACK again to exit");

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}