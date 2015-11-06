package com.location.locationapp;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vinnyfiore on 11/5/15.
 */
public class Location extends RealmObject{

    @PrimaryKey
    private int PK;

    private double latitude;
    private double longitude;

    private String name;
    private String description;

    public int    getPK() { return PK; }
    public void   setPK(int PK) { this.PK = PK; }

    public double    getLatitude() { return latitude; }
    public void   setLatitude(double latitude) { this.latitude = latitude; }
    public double    getLongitude() { return longitude; }
    public void   setLongitude(double longitude) { this.longitude = longitude; }

    public String getName() { return name; }
    public void   setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void   setDescription(String name) { this.description = description; }



}
