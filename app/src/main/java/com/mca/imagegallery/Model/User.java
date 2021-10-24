package com.mca.imagegallery.Model;

public class User {

    String name;
    String email;
    String city;
    String password;
    String profile_url;

    public User() {}

    public User(String name, String city, String email, String password) {
        this.name = name;
        this.city = city;
        this.email = email;
        this.password = password;
        this.profile_url = "https://firebasestorage.googleapis.com/v0/b/imagegallery-ks.appspot.com/o/imagegallery-ks%2Fdefault_profile.png?alt=media&token=d2b57bfd-7b4b-40e2-9bf1-3a2e3e86596d";
    }

    public User(String name, String city, String email, String password, String profile_url) {
        this(name, city, email, password);
        this.profile_url = profile_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }
}
