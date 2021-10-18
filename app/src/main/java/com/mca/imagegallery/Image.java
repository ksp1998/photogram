package com.mca.imagegallery;

import com.google.firebase.Timestamp;

public class Image {

    String description;
    Timestamp upload_date;
    String url;

    public Image() {}

    public Image(String description, Timestamp upload_date, String url) {
        this.description = description;
        this.upload_date = upload_date;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(Timestamp upload_date) {
        this.upload_date = upload_date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
