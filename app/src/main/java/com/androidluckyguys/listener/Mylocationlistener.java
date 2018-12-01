package com.androidluckyguys.listener;

import android.util.Log;

import com.androidluckyguys.model.ARPoint;
import com.androidluckyguys.view.ARActivity;
import com.androidluckyguys.view.AROverlayView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;

import java.util.List;


//监听定位消息
public class Mylocationlistener extends BDAbstractLocationListener {

    @Override
    public void onReceiveLocation(BDLocation location) {
//此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取地址相关的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
        ARActivity.bdLocation=location;
        String addr = location.getAddrStr();    //获取详细地址信息
        String country = location.getCountry();    //获取国家
        String province = location.getProvince();    //获取省份
        String city = location.getCity();    //获取城市
        String district = location.getDistrict();    //获取区县
        String street = location.getStreet();    //获取街道信息

        List<Poi> poiList = location.getPoiList();
        //获取周边POI信息
        //POI信息包括POI ID、名称等，具体信息请参照类参考中POI类的相关说明
        String Poiinfo="";
        for (int i=0;i<poiList.size();i++){
            Poiinfo+=poiList.get(i).getName()+" \n";
        }
        AROverlayView.arPoints.add(new ARPoint(Poiinfo, 3.1850, 101.6868, 0));
       String n= poiList.get(0).getId();
        ARActivity.tvCurrentLocation.setText(country+province+city+district+street+"\n"+Poiinfo+"\n");

        Log.d("", "得到的位置啊啊啊:"+addr+":"+country+province+city+district+street+location.getLatitude());

        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();

        nearbySearchOption.location(new LatLng(location.getLatitude(), location.getLongitude()));
        nearbySearchOption.radius(1000);// 检索半径，单位是米0
        nearbySearchOption.keyword("景点");
        nearbySearchOption.pageNum(1);
        ARActivity.poiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }
}
