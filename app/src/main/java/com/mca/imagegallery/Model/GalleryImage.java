package com.mca.imagegallery.Model;

import com.google.firebase.firestore.DocumentReference;

public class GalleryImage {
    long id;
    String url;
    DocumentReference user;

    public GalleryImage() {}

    public GalleryImage(long id, String url, DocumentReference user) {
        this.id = id;
        this.url = url;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DocumentReference getUser() {
        return user;
    }

    public void setUser(DocumentReference user) {
        this.user = user;
    }
}
