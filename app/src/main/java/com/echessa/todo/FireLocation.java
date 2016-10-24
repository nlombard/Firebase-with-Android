package com.echessa.todo;


public class FireLocation {

    private String lat;

    private String lng;

    public FireLocation() {}

    public FireLocation(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() { return lng; }

    public void setLatLng(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
