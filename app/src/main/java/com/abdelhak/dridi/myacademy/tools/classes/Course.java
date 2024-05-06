package com.abdelhak.dridi.myacademy.tools.classes;

public class Course {
    private long id;
    private String userId;
    private String academyName;
    private String title;
    private double price;
    private String profName;
    private String link;
    private String description = "";
    private String requirements = "";
    private String imagePath = "";
    private int duration;
    private String startDate;

    public Course(long id, String userId, String academyName, String title, double price, String profName, String link, int duration, String startDate) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.price = price;
        this.profName = profName;
        this.link = link;
        this.duration = duration;
        this.startDate = startDate;
        this.academyName = academyName;
    }

    public String getAcademyName() {
        return academyName;
    }

    public void setAcademyName(String academyName) {
        this.academyName = academyName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Course() {
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
