package com.mca.imagegallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {
    private Button close;
    private ImageView imageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        close = findViewById(R.id.close);
        imageView = findViewById(R.id.img);

        close.setOnClickListener(view -> {
            super.onBackPressed();
        });

        loadImage();
    }

    private void loadImage() {
        String url = getIntent().getStringExtra("url");
        Picasso.get().load(url).into(imageView);

//        byte[] bytes = getIntent().getByteArrayExtra("image");
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        imageView.setImageBitmap(bitmap);
    }
}
