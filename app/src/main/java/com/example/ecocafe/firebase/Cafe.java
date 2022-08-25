package com.example.ecocafe.firebase;

import com.google.android.gms.maps.model.LatLng;

public class Cafe {
    //private int id;
    private String name;
    private LatLng latLng;
    private String Event;
    private String pic;
    private String link;

    public Cafe() {
    }

    public Cafe(String name, LatLng latLng,
                String Event, String pic, String link){
        this.name = name;
        this.latLng = latLng;
        this.Event = Event;
        this.pic = pic;
        this.link = link;
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
