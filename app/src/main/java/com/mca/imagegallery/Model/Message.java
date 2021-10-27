package com.mca.imagegallery.Model;

public class Message {

    long id;
    String message;
    String type;

    public Message() {}

    public Message(long id, String message, String type) {
        this.id = id;
        this.message = message;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
