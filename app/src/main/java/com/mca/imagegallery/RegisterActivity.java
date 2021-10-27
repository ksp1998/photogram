package com.mca.imagegallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.Crypto;
import com.mca.imagegallery.helper.Utils;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etCity, etEmail, etPassword;
    private Button btnBack, btnSignUp;
    private TextView linkLogin;
    private ProgressDialog pd;

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

        btnBack.setOnClickListener(view -> onBackPressed());

        btnSignUp.setOnClickListener(view -> {

            String name = etName.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(validateData(name, email, city, password)) {
                User user = new User(name, city, email, Crypto.encrypt(password));
                newRegistration(user);
            }
        });

        linkLogin.setOnClickListener(view -> Utils.startActivity(this, LoginActivity.class));

        db = FirebaseFirestore.getInstance();
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

        pd = Utils.progressDialog(this, "Please wait...");
        pd.show();

        db.collection("users")
            .document(Utils.getID(user.getEmail()))
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(!task.getResult().exists()) {
                        registerUser(user);
                    } else {
                        etEmail.setError("Email already registered!");
                        etEmail.requestFocus();
                    }
                } else {
                    Utils.toast(this, task.getException().getMessage());
                }
                pd.dismiss();
            });
    }

    private void registerUser(User user) {

        db.collection("users")
            .document(Utils.getID(user.getEmail()))
            .set(user)
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Utils.toast(this, "User has been registered successfully...");
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("email", user.getEmail());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else {
                    Utils.toast(this, task.getException().getMessage());
                }
                pd.dismiss();
            });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}