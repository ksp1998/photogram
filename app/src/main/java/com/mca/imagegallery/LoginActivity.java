package com.mca.imagegallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.Crypto;
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
        linkRegister.setOnClickListener(view -> Utils.startActivity(this, RegisterActivity.class));

        String recentUserEmail = getIntent().getStringExtra("email");
        if(recentUserEmail != null) {
            etEmail.setText(recentUserEmail);
            etPassword.getText().clear();
        }

        db = FirebaseFirestore.getInstance();
    }

    private void login() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        pd = Utils.progressDialog(this, "Please wait...");
        pd.show();

        db.collection("users")
            .document(Utils.getID(email))
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(task.getResult().exists() && Crypto.decrypt(task.getResult().get("password", String.class)).equals(password)) {
                        User user = task.getResult().toObject(User.class);
                        Utils.addUserToSharedPreferences(this, user);
                        Utils.addRecentUserToSharedPreferences(this, user);
                        startActivity(new Intent(this, HomeActivity.class));
                    }
                    else {
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
