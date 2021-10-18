package com.mca.imagegallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private Button btnBack;
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;
    private TextView linkRegister;
    private ProgressBar progressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnBack = findViewById(R.id.btn_back);
        btnLogin = findViewById(R.id.btn_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        linkRegister = findViewById(R.id.link_register);
        progressBar = findViewById(R.id.progress_bar);

        btnBack.setOnClickListener(view -> goBack());
        btnLogin.setOnClickListener(view -> login());
        linkRegister.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void goBack() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    private void login() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        String id = email.replace('.', '_');

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(task.getResult().exists() && task.getResult().get("password", String.class).equals(password)) {
                            String name = task.getResult().get("name", String.class);
                            String city = task.getResult().get("city", String.class);
                            String profile_url = task.getResult().get("profile_url", String.class);
                            setSharedPreferences(id, name, city, profile_url);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid email or password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void setSharedPreferences(String id, String name, String city, String profile_url) {
        SharedPreferences sp = getSharedPreferences("shared_file", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("id", id);
        editor.putString("name", name);
        editor.putString("city", city);
        editor.putString("profile_url", profile_url);
        editor.commit();
//        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
    }
}
