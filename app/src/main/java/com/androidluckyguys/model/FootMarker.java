package com.androidluckyguys.model;
public class FootMarker{
    private String use_id;
    private String marker_id;
    private String latitude;
    private String longitude;
    private String place_name;
    private String thought;
    private String photo_path;

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

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
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

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }

    public FootMarker(String use_id, String marker_id, String latitude, String longitude, String place_name, String thought, String photo_path) {
        this.use_id = use_id;
        this.marker_id = marker_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_name = place_name;
        this.thought = thought;
        this.photo_path = photo_path;
    }
}
