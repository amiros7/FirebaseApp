package com.example.firebaseapp;

public class User {

    private String email;
    private String phone;
    private String imageUrl;
    private String name;

    public User() {
    }

    public User(String email, String name, String phone, String imageUrl) {
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
