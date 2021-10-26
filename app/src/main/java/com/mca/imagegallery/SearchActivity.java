package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.NavMenu;
import com.mca.imagegallery.helper.Permissions;
import com.mca.imagegallery.helper.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private TextView tvNoResult;
    private LinearLayout userList;

    FirebaseFirestore db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        new NavMenu(this);

        etSearch = findViewById(R.id.et_search);
        tvNoResult = findViewById(R.id.tv_no_result);
        userList = findViewById(R.id.user_list);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showResults(charSequence.toString().trim().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        db = FirebaseFirestore.getInstance();

        showResults("");
    }

    private void showResults(String searchText) {

        db.collection("users")
            .orderBy("name")
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    tvNoResult.setVisibility(TextView.VISIBLE);
                    userList.removeAllViews();

                    List<User> users = task.getResult().toObjects(User.class);
                    for (User user : users) {

                        if(user.getName().toLowerCase().contains(searchText) || "@".concat(user.getEmail()).contains(searchText)) {

                            LinearLayout userCard = (LinearLayout) getLayoutInflater().inflate(R.layout.user_card, null);
                            userCard.setBackground(getDrawable(R.drawable.border_top));
                            userCard.setOnClickListener(view -> gotoProfile(user));

                            ImageView ivProfile = userCard.findViewById(R.id.iv_profile);
                            TextView tvName = userCard.findViewById(R.id.tv_name);
                            TextView tvUserId = userCard.findViewById(R.id.tv_user_id);

                            Picasso.get().load(user.getProfile_url()).into(ivProfile);
                            tvName.setText(user.getName());
                            tvUserId.setText("@".concat(user.getEmail().substring(0, user.getEmail().lastIndexOf('@'))));

                            userList.addView(userCard);
                        }
                    }
                    if(userList.getChildCount() > 0) {
                        tvNoResult.setVisibility(TextView.GONE);
                    }
                }
                else {
                    Utils.toast(this, task.getException().getMessage());
                }
            })
            .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));
    }

    private void gotoProfile(User user) {

        Intent intent = new Intent(this, MyProfileActivity.class);
        intent.putExtra("profile_url", user.getProfile_url());
        intent.putExtra("name", user.getName());
        intent.putExtra("city", user.getCity());
        intent.putExtra("email", user.getEmail());
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NavMenu.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }
}
