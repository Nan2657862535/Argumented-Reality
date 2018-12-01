package com.androidluckyguys.model;

import android.location.Location;

import com.androidluckyguys.view.ARActivity;

/**
 * Created by ntdat on 1/16/17.
 */

public class ARPoint {
    Location location;
    String name;
    int x,y;//计算出在屏幕显示的位置
    public static float value=0;
    public ARPoint(String name, double lat, double lon, double altitude) {
        this.name = name;
        location = new Location("ARPoint");
        location.setLatitude(lat);
        location.setLongitude(lon);

        setdisplaylocation(lat,lon);
        location.setAltitude(altitude);
    }

    //根据相对的经纬度设置显示的X,Y
    private void setdisplaylocation(double lat, double lon) {
float temp=0;
        if (ARActivity.bdLocation==null)return;
        //当前位置的经纬度
        double selflat=ARActivity.bdLocation.getLatitude();
        double selflon=ARActivity.bdLocation.getLongitude();

        if (lat>selflat){
            if (lon<selflon){
                temp=315;
            }else {
                temp=45;
            }
        }else {
            if (lon<selflon){
                temp=225;
            }else {
                x=135;
            }
        }
        y=100;
        x=(int)(temp-value)*8;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
    public int getx(){
        return x;
    }
    public int getY(){
        return y;
    }
}
