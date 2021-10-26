package com.mca.imagegallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.Utils;

public class LoginActivity extends AppCompatActivity {
    private Button btnBack, btnLogin;
    private EditText etEmail, etPassword;
    private TextView linkRegister;
    private ProgressDialog pd;

    private FirebaseFirestore db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnBack = findViewById(R.id.btn_back);
        btnLogin = findViewById(R.id.btn_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        linkRegister = findViewById(R.id.link_register);

        btnBack.setOnClickListener(view -> onBackPressed());
        btnLogin.setOnClickListener(view -> login());
        linkRegister.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)));

        String recentUserEmail = getIntent().getStringExtra("email");
        if(recentUserEmail != null) {
            etEmail.setText(recentUserEmail);
        }

        db = FirebaseFirestore.getInstance();
    }

    private void login() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        pd = Utils.progressDialog(this, "Please wait...");
        pd.show();
        String id = Utils.getID(email);

        db.collection("users")
            .document(id)
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(task.getResult().exists() && task.getResult().get("password", String.class).equals(password)) {

                        User user = task.getResult().toObject(User.class);
                        setSharedPreferences(user);
                        startActivity(new Intent(this, HomeActivity.class));
                    } else {
                        Utils.toast(this, "Invalid email or password!");
                    }
                } else {
                    Utils.toast(this, task.getException().getMessage());
                }
                pd.dismiss();
            })
            .addOnFailureListener(ex -> {
                Utils.toast(this, ex.getMessage());
                pd.dismiss();
            });
    }

    private void setSharedPreferences(User user) {
        SharedPreferences.Editor editor = getSharedPreferences(Utils.LOGIN_SHARED_FILE, MODE_PRIVATE).edit();
        editor.putString("name", user.getName());
        editor.putString("city", user.getCity());
        editor.putString("email", user.getEmail());
        editor.putString("profile_url", user.getProfile_url());
        editor.apply();

        editor = getSharedPreferences(Utils.RECENT_USER_SHARED_FILE, MODE_PRIVATE).edit();
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("profile_url", user.getProfile_url());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
