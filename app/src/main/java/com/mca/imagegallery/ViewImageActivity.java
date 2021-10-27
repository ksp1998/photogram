package com.mca.imagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mca.imagegallery.Model.User;
import com.mca.imagegallery.helper.Utils;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView ivProfile, imageView;
    private TextView tvName, tvUserId;
    private Button btnClose;
    private RelativeLayout profileCard;
    private String image_url;
    private User user;

    int i = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Utils.setTransitions(this, R.transition.explode, R.transition.explode);

        ivProfile = findViewById(R.id.iv_profile);
        imageView = findViewById(R.id.image_view);
        tvName = findViewById(R.id.tv_name);
        tvUserId = findViewById(R.id.tv_user_id);
        btnClose = findViewById(R.id.btn_close);
        profileCard = findViewById(R.id.profile_card);

        user = Utils.getUserFromIntent(this);
        image_url = getIntent().getStringExtra("image_url");

        btnClose.setOnClickListener(view -> onBackPressed());
        profileCard.setOnClickListener(view -> gotoProfile());

        ImageView.ScaleType[] types = {ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.FIT_CENTER};
        imageView.setOnClickListener(view -> {
            i %= 2;
            imageView.setScaleType(types[i++]);
        });
        registerForContextMenu(imageView);

        loadImage();
    }

    private void loadImage() {
        Picasso.get().load(user.getProfile_url()).into(ivProfile);
        tvName.setText(user.getName());
        tvUserId.setText("@".concat(user.getEmail().substring(0, user.getEmail().lastIndexOf('@'))));
        Picasso.get().load(image_url).into(imageView);
    }

    private void gotoProfile() {
        if(getIntent().getStringExtra("activity") != null) {
            onBackPressed();
            return;
        }

        Intent intent = new Intent(this, MyProfileActivity.class);
        intent = Utils.addUserToIntent(intent, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_image_options_menu, menu);
        menu.findItem(R.id.view).setVisible(false);
        menu.findItem(R.id.delete).setVisible(false);
    }


        @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.save:
                Utils.saveImage(this, imageView);
                break;
            case R.id.cancel: break;
        }
        return super.onContextItemSelected(item);
    }
}
