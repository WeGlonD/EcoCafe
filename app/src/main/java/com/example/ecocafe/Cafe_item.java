package com.example.ecocafe;

import android.media.Image;

import com.google.android.gms.maps.model.LatLng;

public class Cafe_item {
    //private int id;
    private String name;
    private double lat;
    private double lng;
    private String Event;
    private String pic;
    private String link;

    public Cafe_item() {
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() { return lat; }
    public double getLng() { return lng;}

    public void setLat(double lat) { this.lat = lat; }
    public void setLng(double lng) { this.lng = lng; }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}