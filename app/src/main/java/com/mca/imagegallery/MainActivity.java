package com.mca.imagegallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isUserLoggedIn();
        addRecentUser();

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));
        btnRegister.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void isUserLoggedIn() {
        SharedPreferences sp = getSharedPreferences(Utils.LOGIN_SHARED_FILE, MODE_PRIVATE);
        if(sp.getString("email", null) != null) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    private void addRecentUser() {
        SharedPreferences sp = getSharedPreferences(Utils.RECENT_USER_SHARED_FILE, MODE_PRIVATE);
        if(sp.getString("email", null) != null) {

            LinearLayout userCard = (LinearLayout) getLayoutInflater().inflate(R.layout.user_card, null);
            userCard.setOnClickListener(view -> {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("email", sp.getString("email", null));
                startActivity(intent);
            });

            ImageView ivProfile = userCard.findViewById(R.id.iv_profile);
            TextView tvName = userCard.findViewById(R.id.tv_name);
            TextView tvEmail = userCard.findViewById(R.id.tv_email);

            Picasso.get().load(sp.getString("profile_url", null)).into(ivProfile);
            tvName.setText(sp.getString("name", null));
            tvEmail.setText(sp.getString("email", null));

            RelativeLayout parent = findViewById(R.id.container);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            parent.addView(userCard , layoutParams);
        }
    }

    // Override function for exiting application when double back clicked
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