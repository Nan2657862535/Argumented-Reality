package com.androidluckyguys.model;

public class FlagMarker {
    private String use_id;
    private String marker_id;
    private String latitude;
    private String longitude;
    private String place_name;
    private String travel_plan;

    public String getUse_id() {
        return use_id;
    }

    public void setUse_id(String use_id) {
        this.use_id = use_id;
    }

    public String getMarker_id() {
        return marker_id;
    }

    public void setMarker_id(String marker_id) {
        this.marker_id = marker_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getTravel_plan() {
        return travel_plan;
    }

    public void setTravel_plan(String travel_plan) {
        this.travel_plan = travel_plan;
    }

    public FlagMarker(String use_id, String marker_id, String latitude, String longitude, String place_name, String travel_plan) {
        this.use_id = use_id;
        this.marker_id = marker_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_name = place_name;
        this.travel_plan = travel_plan;
    }
}
