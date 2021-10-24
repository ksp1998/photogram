package com.mca.imagegallery.Model;

import com.google.firebase.Timestamp;

public class Image {

    long id;
    String description;
    Timestamp upload_date;
    String url;

    public Image() {}

    public Image(long id, String description, Timestamp upload_date, String url) {
        this.id = id;
        this.description = description;
        this.upload_date = upload_date;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
