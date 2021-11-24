package com.example.vepr;

import java.io.Serializable;

public class Destination implements Serializable {
    // public final Image image;
    public final String id;
    public final String desName;
    public final String address;
    public final String phone;
    public final String openTime;
    public final String closeTime;
    public final String longitude;
    public final String latitude;
    public final String website;
    public final String type;

    public Destination(String id, String desName, String address, String phone, String openTime, String closeTime, String longitude, String latitude, String website, String type) {
        this.id = id;
        this.desName = desName;
        this.address = address;
        this.phone = phone;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.website = website;
        this.type = type;
    }
    //ArrayList<String> comment;
}