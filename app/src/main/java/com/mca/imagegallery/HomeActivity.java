package com.mca.imagegallery;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {
    LinearLayout card, cardContainer;
    LinearLayout galleryLeft, galleryRight;
    int[] homeImgsIds = {R.drawable.home_img1, R.drawable.home_img2, R.drawable.home_img3, R.drawable.home_img4};

    private boolean doubleBackToExitPressedOnce = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_home);

        new NavMenu(this);


        cardContainer = findViewById(R.id.card_container);
        galleryLeft = findViewById(R.id.gallery_left);
        galleryRight = findViewById(R.id.gallery_right);

        setHomeImages();
        browseImages();
    }

    private void setHomeImages() {
        cardContainer.removeAllViews();
        for (int i = 0; i < homeImgsIds.length; i++) {
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.home_card, (ViewGroup) null);
            card = linearLayout;
            ImageView imageView = linearLayout.findViewById(R.id.img);
            imageView.setMaxWidth(Resources.getSystem().getDisplayMetrics().widthPixels - (cardContainer.getPaddingRight() * 2));
            imageView.setImageResource(homeImgsIds[i]);
//            final int k = i;
//            card.setOnClickListener(view -> viewImage(homeImgsIds[k]));
            cardContainer.addView(card);
        }
    }

    private void browseImages() {
        galleryLeft.removeAllViews();
        galleryRight.removeAllViews();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("imagegallery-ks");

        reference.listAll()
                .addOnSuccessListener(listResult -> {

                    for (StorageReference prefix : listResult.getPrefixes()) {
                        reference.child(prefix.getName()).listAll()
                                .addOnSuccessListener(listResult1 -> {
                                    int i = 0;
                                    for (StorageReference item : listResult1.getItems()) {
                                        final int k = i;
                                        reference.child(prefix.getName()).child(item.getName()).getDownloadUrl()
                                                .addOnSuccessListener(uri -> {
                                                    final ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, (ViewGroup) null);
                                                    Picasso.get().load(uri).into(imageView);
                                                    imageView.setOnClickListener(view -> viewImage(uri));
                                                    if (k % 2 == 0) {
                                                        galleryLeft.addView(imageView);
                                                    } else {
                                                        galleryRight.addView(imageView);
                                                    }
                                                })
                                                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show());
                                        i++;
                                    }
                                })
                                .addOnFailureListener(ex ->
                                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    }

                    int i = galleryLeft.getChildCount() + galleryRight.getChildCount();
                    for (StorageReference item : listResult.getItems()) {
                        final int k = i;
                        reference.child(item.getName()).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    final ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, (ViewGroup) null);
                                    Picasso.get().load(uri).into(imageView);
                                    imageView.setOnClickListener(view -> viewImage(uri));
                                    if (k % 2 == 0) {
                                        galleryLeft.addView(imageView);
                                    } else {
                                        galleryRight.addView(imageView);
                                    }
                                })
                                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show());
                        i++;
                    }
                })
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show());
//        reference.listAll()
//                .addOnSuccessListener(listResult -> {
//                    int i = 0;
//                    for (StorageReference item : listResult.getItems()) {
//                        final long ONE_MEGABYTE = 1024 * 1024;
//                        final int k = i;
//                        item.getBytes(ONE_MEGABYTE)
//                                .addOnSuccessListener(bytes -> {
//                                    final ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gallery_image, (ViewGroup) null);
//                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                                    imageView.setImageBitmap(bitmap);
//                                    imageView.setOnClickListener(view -> viewImage(bytes));
//
//                                    if (k % 2 == 0) {
//                                        galleryLeft.addView(imageView);
//                                    } else {
//                                        galleryRight.addView(imageView);
//                                    }
//                                })
//                                .addOnFailureListener(ex ->
//                                        Log.d("ERROR -> ", ex.getMessage())
//                                );
//                        i++;
//                    }
//                })
//                .addOnFailureListener(ex -> {
//                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//                });
    }

    private void viewImage(Uri uri) {
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("url", uri.toString());
        startActivity(intent);
    }

    // override method to perform action on permission grant and revoke
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // override method which will be called on when image is captured or selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NavMenu.onActivityResult(requestCode, resultCode, data);
    }

    // Override function for exiting application when double back clicked
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}
