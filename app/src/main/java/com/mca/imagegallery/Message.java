package com.mca.imagegallery;

public class Message {

    String message;
    String datetime;
    String in_out;

    public Message() {}

    public Message(String message, String datetime, String in_out) {
        this.message = message;
        this.datetime = datetime;
        this.in_out = in_out;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getIn_out() {
        return in_out;
    }

    public void setIn_out(String in_out) {
        this.in_out = in_out;
    }
}
