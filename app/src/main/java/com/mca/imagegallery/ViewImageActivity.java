package com.mca.imagegallery;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewImageActivity extends AppCompatActivity {
    private Button close;
    private ImageView imageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

//        getWindow().setFlags(1024, 1024);
        close = findViewById(R.id.close);
        imageView = findViewById(R.id.img);

        close.setOnClickListener(view -> {
            super.onBackPressed();
        });
        loadImage();
    }

    private void loadImage() {
        this.imageView.setImageResource(getIntent().getIntExtra("image_id", R.drawable.home_img1));
    }
}
