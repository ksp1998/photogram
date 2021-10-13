package com.mca.imagegallery;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnBack;
    private Button btnSignUp;
    private ProgressBar progressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnBack = findViewById(R.id.btn_back);
        btnSignUp = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progress_bar);

        btnBack.setOnClickListener(view -> super.onBackPressed());

        btnSignUp.setOnClickListener(view -> {

            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(validateData(name, email, password)) {
                User user = new User(name, email, password);
                newRegistration(user);
            }
        });
    }

    private boolean validateData(String name, String email, String password) {
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        String id = user.getEmail().replace('.', '_');

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(!dataSnapshot.child(id).exists()) {
                    registerUser(reference, id, user);
                } else {
                    etEmail.setError("Email already registered!");
                    etEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(DatabaseReference reference, String id, User user) {
        reference.child(id).setValue(user).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                Toast.makeText(getApplicationContext(), "User has been registered successfully...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Registration failed.", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }
}