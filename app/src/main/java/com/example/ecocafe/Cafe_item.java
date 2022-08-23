package com.example.ecocafe;

import android.media.Image;

import com.google.android.gms.maps.model.LatLng;

public class Cafe_item {
    //private int id;
    private String name;
    private LatLng latLng;
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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

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