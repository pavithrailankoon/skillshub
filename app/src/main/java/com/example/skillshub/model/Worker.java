package com.example.skillshub.model;

public class Worker {
    private String uid;
    private String name;
    private String district;
    private String city;
    private String profileUrl;
    private double averageRating;

    // Constructor
    public Worker(String uid, String name, String district, String city, String profileUrl) {
        this.uid = uid;
        this.name = name;
        this.district = district;
        this.city = city;
        this.profileUrl = profileUrl;
    }

    // Getters and setters
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getDistrict() { return district; }
    public String getCity() { return city; }
    public String getProfileUrl() { return profileUrl; }
    public double getAverageRating() { return averageRating; }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
