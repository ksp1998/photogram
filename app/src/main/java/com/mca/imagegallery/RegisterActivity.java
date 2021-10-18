package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etCity, etEmail, etPassword;
    private Button btnBack;
    private Button btnSignUp;
    private TextView linkLogin;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_name);
        etCity = findViewById(R.id.et_city);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnBack = findViewById(R.id.btn_back);
        btnSignUp = findViewById(R.id.btn_signup);
        linkLogin = findViewById(R.id.link_login);
        progressBar = findViewById(R.id.progress_bar);

        btnBack.setOnClickListener(view -> goBack());

        btnSignUp.setOnClickListener(view -> {

            String name = etName.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(validateData(name, email, city, password)) {
                User user = new User(name, email, city, password);
                newRegistration(user);
            }
        });

        linkLogin.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));

        db = FirebaseFirestore.getInstance();
    }

    private void goBack() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    private boolean validateData(String name, String email, String city, String password) {
        if(name.isEmpty()) {
            etName.setError("Name is required!");
            etName.requestFocus();
            return false;
        }

        if(email.isEmpty()) {
            etEmail.setError("Email is required!");
            etEmail.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please provide valid email!");
            etEmail.requestFocus();
            return false;
        }

        if(city.isEmpty()) {
            etCity.setError("City is required!");
            etCity.requestFocus();
            return false;
        }

        if(password.isEmpty()) {
            etPassword.setError("Password is required!");
            etPassword.requestFocus();
            return false;
        }

        if(password.length() < 6) {
            etPassword.setError("Minimum password length should be 6 characters!");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void newRegistration(User user) {

        progressBar.setVisibility(View.VISIBLE);
        String id = user.getEmail().replace('.', '_');

        db.collection("users")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(!task.getResult().exists()) {
                            registerUser(id, user);
                        } else {
                            etEmail.setError("Email already registered!");
                            etEmail.requestFocus();
                        }
                    } else {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void registerUser(String id, User user) {

        db.collection("users")
                .document(id)
                .set(user)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "User has been registered successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
}