package com.mca.imagegallery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfile extends AppCompatActivity {

    private Button btnBack, btnUpdate;
    private EditText etName, etCity;
    private String id, name, city, profile_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnBack = findViewById(R.id.btn_back);
        btnUpdate = findViewById(R.id.btn_update);
        etName = findViewById(R.id.et_name);
        etCity = findViewById(R.id.et_city);

        btnBack.setOnClickListener(view -> goBack());

        SharedPreferences sp = getSharedPreferences("shared_file", MODE_PRIVATE);
        id = sp.getString("id", "NULL");
        name = sp.getString("name", "NULL");
        city = sp.getString("city", "NULL");
        profile_url = sp.getString("profile_url", "NULL");
        etName.setText(name);
        etCity.setText(city);

        btnUpdate.setOnClickListener(view -> updateProfile());
    }

    private void updateProfile() {
        if(!etName.getText().toString().equals(name) || !etCity.getText().toString().equals(city)) {
            Toast.makeText(this, "Begin updating", Toast.LENGTH_SHORT).show();
        }
    }

    private void goBack() {
        if(!etName.getText().toString().equals(name) || !etCity.getText().toString().equals(city)) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Unsaved changes?")
                    .setIcon(R.drawable.ic_launcher_background)
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}