package com.mca.imagegallery;

public class User {

    String name;
    String email;
    String city;
    String password;
    String profile_url;

    public User() {}

    public User(String name, String email, String city, String password) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.password = password;
        this.profile_url = "https://cdn.vox-cdn.com/thumbor/SbX1VbxJhxijxD1tzRTJ8uq38P4=/1400x1400/filters:format(jpeg)/cdn.vox-cdn.com/uploads/chorus_asset/file/19101461/spider_man_far_from_home_peter_parker_1562394390.jpg";
    }

    public User(String name, String email, String city, String password, String profile_url) {
        this(name, email, city, password);
        this.profile_url = profile_url;
    }

    public String getEmail() {
        return email;
    }
}
