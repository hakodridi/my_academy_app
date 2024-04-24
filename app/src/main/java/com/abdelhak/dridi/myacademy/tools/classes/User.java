package com.abdelhak.dridi.myacademy.tools.classes;

public class User {
    private String id;
    private String name;
    private String email;
    private boolean isAcademy;
    private String imagePath;
    private String phone;
    private String wilaya;
    private String commune;
    private String address;

    public User() {
    }

    public User(String id, String name, String email, boolean isAcademy) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isAcademy = isAcademy;
        this.imagePath = "";
        this.phone = "";
        this.wilaya = "";
        this.commune = "";
        this.address = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isAcademy() {
        return isAcademy;
    }

    public void setAcademy(boolean academy) {
        isAcademy = academy;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWilaya() {
        return wilaya;
    }

    public void setWilaya(String wilaya) {
        this.wilaya = wilaya;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
