package com.example.ecocafe.firebase;

import com.example.ecocafe.MapTab;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class Cafe implements Comparable<Cafe> {
    //private int id;
    private String name;
    private double lat;
    private double lng;
    private String Event;
    private String pic;
    private String link;

    public Cafe() {
    }

    public Cafe(String name, Double lat, Double lng,
                String Event, String pic, String link){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
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

    @Override
    public int compareTo(Cafe cafe) {
        LatLng positionThis = new LatLng(this.lat, this.lng);
        double distanceThis = SphericalUtil.computeDistanceBetween(MapTab.currentPosition, positionThis);
        LatLng positionOther = new LatLng(cafe.lat, cafe.lng);
        double distanceOther = SphericalUtil.computeDistanceBetween(MapTab.currentPosition, positionOther);
        if(distanceThis > distanceOther)
            return 1;
        else if(distanceThis < distanceOther)
            return -1;
        else
            return 0;
    }
}
