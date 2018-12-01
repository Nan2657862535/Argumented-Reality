package com.androidluckyguys.view;

import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidluckyguys.R;
import com.androidluckyguys.model.FootMarker;
import com.androidluckyguys.presenter.HttpUtils;
import com.androidluckyguys.userdefinedview.TopView;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

public class DailyMap extends AppCompatActivity {
    public static MapView mMapView;
    public static BaiduMap mBaiduMap;
    private ImageView morefunction;
    private BDLocation location;
    private ImageView flag;
    private ImageView foot;
    private Vector<Marker> flags;
    private Vector<Marker> foots;
    public static ImageView open_tra_title;
    private TopView topView;
    private SuggestionSearch mSuggestionSearch;
    private EditText placename;
    private ListView searched_places;
    private SimpleAdapter showplaces;
    private int user_id=4;
    private int FLAGINFO=20;
    private int FOOTINFO=30;
    private Boolean isfinish=false;
    private ArrayList<HashMap<String, Object>>  places;

    private int index=0;
    private Boolean isflags=false;
    private List<SuggestionResult.SuggestionInfo> resl=new List<SuggestionResult.SuggestionInfo>() {
        @Override
        public void add(int location, SuggestionResult.SuggestionInfo object) {

        }

        @Override
        public boolean add(SuggestionResult.SuggestionInfo object) {
            return false;
        }

        @Override
        public boolean addAll(int location, @NonNull Collection<? extends SuggestionResult.SuggestionInfo> collection) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends SuggestionResult.SuggestionInfo> collection) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public boolean contains(Object object) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public SuggestionResult.SuggestionInfo get(int location) {
            return null;
        }

        @Override
        public int indexOf(Object object) {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @NonNull
        @Override
        public Iterator<SuggestionResult.SuggestionInfo> iterator() {
            return null;
        }

        @Override
        public int lastIndexOf(Object object) {
            return 0;
        }

        @NonNull
        @Override
        public ListIterator<SuggestionResult.SuggestionInfo> listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator<SuggestionResult.SuggestionInfo> listIterator(int location) {
            return null;
        }

        @Override
        public SuggestionResult.SuggestionInfo remove(int location) {
            return null;
        }

        @Override
        public boolean remove(Object object) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public SuggestionResult.SuggestionInfo set(int location, SuggestionResult.SuggestionInfo object) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @NonNull
        @Override
        public List<SuggestionResult.SuggestionInfo> subList(int start, int end) {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] array) {
            return null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//要在setContentView之前初始化
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_daily_map);

        //去除标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        /*SharedPreferences preferences = getSharedPreferences("userInfo",Activity.MODE_PRIVATE);
        user_id = Integer.parseInt(preferences.getString("user_id", -1+""));
*/
        //初始化控件和变量
        initdata();
        //设置监听器
        initlistener();
//设置点击旗子的事件
        addflaginit();
//设置足迹的监听事件
        addfootinit();
//点击marker的事件
        markerlistenerinit();

//站看popupwindow
        trainfotitleinit();

        //获取用户的marker信息
        markerinfoinit();
//定位
        MapStatus.Builder builder = new MapStatus.Builder();
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        builder.target(latLng);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        bdlocation(location.getLatitude(),location.getLongitude());

    }

    private void initlistener() {
        morefunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DailyMap.this,MoreFunction.class);
                startActivity(intent);
            }
        });

        searched_places.setAdapter(showplaces);
        searched_places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DailyMap.this, resl.get(position).key, Toast.LENGTH_SHORT).show();
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(resl.get(position).pt);
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isfinish=true;
                searched_places.setVisibility(View.GONE);
                placename.setText(resl.get(position).key);
                bdlocation(resl.get(position).pt.latitude,resl.get(position).pt.longitude);
            }
        });
        placename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isfinish=false;
            }
        });
        placename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isfinish)return;
                if (s.toString().equals("")){
                    searched_places.setVisibility(View.GONE);
                }
                else {
                    searched_places.setVisibility(View.VISIBLE);
                }

                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())

                        .keyword(s.toString()).city("东京"));

            }
        });
        location=ARActivity.bdLocation;
        //设置推荐监听
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);
    }

    private void initdata() {
        places=new ArrayList<>();
        flags=new Vector<>();
        foots=new Vector<>();
        mMapView = (MapView) findViewById(R.id.mmap);
        morefunction=(ImageView)findViewById(R.id.morefunction);
        mBaiduMap = mMapView.getMap();
        //获取推荐实例
        mSuggestionSearch = SuggestionSearch.newInstance();
        open_tra_title=(ImageView)findViewById(R.id.open_tra_title);
        placename=(EditText)findViewById(R.id.placename);
        searched_places=(ListView)findViewById(R.id.searched_places);
        showplaces=new SimpleAdapter(this,places,android.R.layout.simple_list_item_2,
                new String[]{"text"},new int[]{android.R.id.text2});
    }


    @Override
    protected void onResume() {
        super.onResume();
       // refleshmarker();

    }



    //开辟进程获取后台数据
    private void markerinfoinit() {

        foots.clear();
        flags.clear();
new Thread(new Runnable() {
    @Override
    public void run() {
        String footinforesult=getfootinfo();
        String flaginforesult=getflaginfo();
        addfootmarker(footinforesult);
        addflagmarker(flaginforesult);
        Log.i("得到的足迹信息啊啊啊啊 啊啊啊啊啊啊啊",footinforesult);

    }
}).start();

    }

    //根据后台返回的数据添加旗子
    private void addflagmarker(String flaginforesult) {
        try {
            JSONObject jsonObject=new JSONObject(flaginforesult);
            JSONArray jsonArray=jsonObject.getJSONArray("flaginfoList");
            for (int i=0;i<jsonArray.length();i++){
                String s=jsonArray.getJSONObject(i).getString("user_id");
                String s1=jsonArray.getJSONObject(i).getString("marker_id");
                String s2=jsonArray.getJSONObject(i).getString("latitude");
                String s3=jsonArray.getJSONObject(i).getString("longitude");
                String s4=jsonArray.getJSONObject(i).getString("place_name");
                String s5=jsonArray.getJSONObject(i).getString("travel_plan");

                Bundle mBundle = new Bundle();
                mBundle.putString("user_id", s);
                mBundle.putString("marker_id", s1);
                mBundle.putString("place_name", s4);
                mBundle.putString("travel_plan", s5);
                mBundle.putBoolean("isfoot",false);

                LatLng point = new LatLng(Double.parseDouble(s2), Double.parseDouble(s3));
                //构建Marker图标

                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.flags);
//构建MarkerOption，用于在地图上添加Marker

                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap).draggable(true).extraInfo(mBundle).title(s4);

//在地图上添加Marker，并显示
                Marker marker;
                marker=(Marker) mBaiduMap.addOverlay(option);
                marker.setExtraInfo(mBundle);
                flags.add(marker);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "json异常", Toast.LENGTH_SHORT).show();
        }

    }
    //根据后台返回的数据添加足迹
    private void addfootmarker(String footinforesult) {
        try {
            JSONObject jsonObject=new JSONObject(footinforesult);
            JSONArray jsonArray=jsonObject.getJSONArray("footinfoList");
            for (int i=0;i<jsonArray.length();i++){

                String s=jsonArray.getJSONObject(i).getString("user_id");
                String s1=jsonArray.getJSONObject(i).getString("marker_id");
                String s2=jsonArray.getJSONObject(i).getString("latitude");
                String s3=jsonArray.getJSONObject(i).getString("longitude");
                String s4=jsonArray.getJSONObject(i).getString("place_name");
                String s5=jsonArray.getJSONObject(i).getString("thought");
                String s6=jsonArray.getJSONObject(i).getString("photos_path");
                FootMarker footMarker=new FootMarker(s,s1,s2,s3,s4,s5,s6);

                Bundle mBundle = new Bundle();
                mBundle.putString("user_id", s);
                mBundle.putString("marker_id", s1);
                mBundle.putString("place_name", s4);
                mBundle.putString("thought", s5);
                mBundle.putString("photos_path", s6);
                mBundle.putBoolean("isfoot",true);

                LatLng point = new LatLng(Double.parseDouble(s2), Double.parseDouble(s3));
                //构建Marker图标

                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.foots);
//构建MarkerOption，用于在地图上添加Marker

                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap).draggable(true).extraInfo(mBundle).title(s4);

//在地图上添加Marker，并显示
                Marker marker;
                marker=(Marker) mBaiduMap.addOverlay(option);
                marker.setExtraInfo(mBundle);
                foots.add(marker);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "json异常", Toast.LENGTH_SHORT).show();
        }

    }

    //获取后台数据后更新vector并显示图层
    private void refleshmarker() {
        for (int i=0;i<foots.size();i++){
            Marker marker=foots.get(i);
            LatLng point = marker.getPosition();
            //构建Marker图标

            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.foots);
//构建MarkerOption，用于在地图上添加Marker

            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap).draggable(true);

//在地图上添加Marker，并显示

            mBaiduMap.addOverlay(option);

        }

        for (int i=0;i<flags.size();i++){
            Marker marker=flags.get(i);
            LatLng point = marker.getPosition();
            //构建Marker图标

            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.flags);
//构建MarkerOption，用于在地图上添加Marker

            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap).draggable(true);

//在地图上添加Marker，并显示

            mBaiduMap.addOverlay(option);

        }
    }

    //获取后台足迹信息
    public String getfootinfo() {
        URL infoUrl = null;

//        String remote_addr="http://api.weatherdt.com/common/?area="+citycode+"&type="+datacode+"&key=90d48635e440fc4c032e4f5b5b11e996";
        String remote_addr ="http://192.168.123.234:8080/LittleTest/GetFootInfo?user_id="+user_id;
        try {
            infoUrl = new URL(remote_addr);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpconnection = (HttpURLConnection) connection;
            httpconnection.setRequestMethod("GET");
            httpconnection.setReadTimeout(5000);
            InputStream inStream = httpconnection.getInputStream();
            ByteArrayOutputStream data = new ByteArrayOutputStream();

            byte[] buffer = new byte[2000];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                data.write(buffer, 0, len);
            }
            inStream.close();
            String test=new String(data.toByteArray(), "utf-8");
            Log.i("test",test);
            return new String(data.toByteArray(), "utf-8");

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(this,"网络异常",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"输出异常",Toast.LENGTH_SHORT).show();
        }

        return "异常";
    }

    //获取后台旗子信息
    public String getflaginfo() {

        URL infoUrl = null;

//        String remote_addr="http://api.weatherdt.com/common/?area="+citycode+"&type="+datacode+"&key=90d48635e440fc4c032e4f5b5b11e996";
        String remote_addr="http://192.168.123.234:8080/LittleTest/GetFlagInfo?user_id="+user_id;
        try {
            infoUrl = new URL(remote_addr);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpconnection = (HttpURLConnection) connection;
            httpconnection.setRequestMethod("GET");
            httpconnection.setReadTimeout(5000);
            InputStream inStream = httpconnection.getInputStream();
            ByteArrayOutputStream data = new ByteArrayOutputStream();

            byte[] buffer = new byte[2000];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                data.write(buffer, 0, len);
            }
            inStream.close();
            String test=new String(data.toByteArray(), "utf-8");
            Log.i("test",test);
            return new String(data.toByteArray(), "utf-8");

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(this,"网络异常",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"输出异常",Toast.LENGTH_SHORT).show();
        }

        return "异常";
    }

    //显示定位图标
    private void bdlocation(double lat,double lon) {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

// 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(lat)
                .longitude(lon).build();

// 设置定位数据
        mBaiduMap.setMyLocationData(locData);

// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）

      /*  MyLocationConfiguration.LocationMode mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.more);*/
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, BitmapDescriptorFactory .fromResource(R.drawable.marker_red));
        mBaiduMap.setMyLocationConfiguration(config);


        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {

            }
        });

        mMapView.getMap().setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                //左上角经纬度
                Point pt = new Point();
                pt.x=0;
                pt.y=0;
                LatLng ll = mBaiduMap.getProjection().fromScreenLocation(pt);
                //右下角经纬度
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                Point pty = new Point();
                pty.x=dm.widthPixels;
                pty.y=(int)(0.5*dm.heightPixels);
                LatLng lly = mBaiduMap.getProjection().fromScreenLocation(pty);
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

            }
        });

        //  mMapView = (MapView) findViewById(R.id.bmapView);
    }

    OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {

        public void onGetSuggestionResult(SuggestionResult res) {
            if (res == null || res.getAllSuggestions() == null) {
                return;
                //未找到相关结果
            }else
            {
                places.clear();
                resl.clear();
                resl=res.getAllSuggestions();
                for(int i=0;i<resl.size();i++)
                {
                    Log.d("result: ","city"+resl.get(i).city+" dis "+resl.get(i).district+"key "+resl.get(i).key);
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("text",resl.get(i).key);
                    places.add(map);
                }
                showplaces.notifyDataSetChanged();
            }
            //获取在线建议检索结果
        }
    };

    //显示头部topview的监听事件
    private void trainfotitleinit() {

        open_tra_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                topView= new TopView(DailyMap.this);
                refleshmarkernum();
                open_tra_title.setVisibility(View.GONE);
            }
        });

    }

    //标志的监听事件
    private void markerlistenerinit() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Bundle bundle = marker.getExtraInfo();
                Boolean isfoot=bundle.getBoolean("isfoot");
                String marker_id=bundle.getString("marker_id");
                if (isfoot){
                    String s= bundle.getString("thought");
                    Intent intent=new Intent(DailyMap.this,FootsInfo.class);
                    intent.putExtra("marker_id",marker_id);
                    intent.putExtra("latitude",marker.getPosition().latitude+"");
                    intent.putExtra("longitude",marker.getPosition().longitude+"");
                    intent.putExtra("thought",s);
                    startActivityForResult(intent,FOOTINFO);
                }
                else {
                    String s= bundle.getString("travel_plan");
                    Intent intent=new Intent(DailyMap.this,FlagsInfo.class);
                    intent.putExtra("marker_id",marker_id);
                    intent.putExtra("latitude",marker.getPosition().latitude+"");
                    intent.putExtra("longitude",marker.getPosition().longitude+"");
                    intent.putExtra("travel_plan",s);
                    startActivityForResult(intent,FLAGINFO);
                }
                return true;
            }
        });

    }

    //添加足迹的监听事件
    private void addfootinit() {

        foot=(ImageView)findViewById(R.id.foot);



        foot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              new Thread(new Runnable() {
                  @Override
                  public void run() {

                      //右下角经纬度


                      DisplayMetrics dm = new DisplayMetrics();
                      getWindowManager().getDefaultDisplay().getMetrics(dm);
                      Point pty = new Point();
                      pty.x=(int)(0.5*dm.widthPixels);
                      pty.y=(int)(dm.heightPixels*0.5);
                      LatLng lly = DailyMap.mBaiduMap.getProjection().fromScreenLocation(pty);
                      LatLng point = new LatLng(lly.latitude, lly.longitude);
                      //构建Marker图标

                      BitmapDescriptor bitmap = BitmapDescriptorFactory
                              .fromResource(R.drawable.foots);
//构建MarkerOption，用于在地图上添加Marker

                      OverlayOptions option = new MarkerOptions()
                              .position(point)
                              .icon(bitmap).draggable(true);

//在地图上添加Marker，并显示
                      Marker marker;
                      marker=(Marker) DailyMap.mBaiduMap.addOverlay(option);



                      Map<String, String> params = new HashMap<String, String>();
                      params.put("user_id", 4+"");
                      //params.put("marker_id", markid);
                      params.put("latitude", marker.getPosition().latitude+"");
                      params.put("longitude", marker.getPosition().longitude+"");
                      params.put("thought", "");
                      params.put("photos_path", "H:\\\\eclipse-workspace\\\\.metadata\\\\.plugins\\\\org.eclipse.wst.server.core\\\\tmp0\\\\wtpwebapps\\\\LittleTest\\\\uploadphpto");

                      String encode = "utf-8";
                      int marker_id= HttpUtils.addfootinfo(params,encode);
                      Bundle mBundle = new Bundle();
                      mBundle.putString("user_id", 4+"");
                      mBundle.putString("marker_id", marker_id+"");
                      mBundle.putBoolean("isfoot",true);
                      marker.setExtraInfo(mBundle);
                      foots.add(marker);
                  }
              }).start();
                refleshmarkernum();
            }
        });
    }
    //添加旗子的监听事件
    private void addflaginit() {
        flag=(ImageView)findViewById(R.id.flag);



        flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //右下角经纬度
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        Point pty = new Point();
                        pty.x=(int)(0.5*dm.widthPixels);
                        pty.y=(int)(dm.heightPixels*0.5);
                        LatLng lly = DailyMap.mBaiduMap.getProjection().fromScreenLocation(pty);
                        LatLng point = new LatLng(lly.latitude, lly.longitude);
                        //构建Marker图标

                        BitmapDescriptor bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.flags);
//构建MarkerOption，用于在地图上添加Marker

                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .icon(bitmap).draggable(true);

//在地图上添加Marker，并显示
                        Marker marker;
                        marker=(Marker) DailyMap.mBaiduMap.addOverlay(option);



                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_id", 4+"");
                        //params.put("marker_id", markid);
                        params.put("latitude", marker.getPosition().latitude+"");
                        params.put("longitude", marker.getPosition().longitude+"");
                        params.put("travel_plan", "");

                        String encode = "utf-8";
                        int marker_id= HttpUtils.addflaginfo(params,encode);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("user_id", 4+"");
                        mBundle.putString("marker_id", marker_id+"");
                        mBundle.putBoolean("isfoot",false);
                        marker.setExtraInfo(mBundle);
                        flags.add(marker);
                    }
                }).start();
                refleshmarkernum();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FOOTINFO){
            Toast.makeText(this, "添加足迹信息成功", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "添加旗子信息成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

// 当不需要定位图层时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mSuggestionSearch.destroy();
    }


    public int getfootnum(){
        return foots.size();
    }
    public int getflagnum(){
        return flags.size();
    }

    //更新显示足迹数和旗子数
    public void refleshmarkernum(){
        if (topView!=null){
            TextView textView=(TextView)topView.popupWindowView.findViewById(R.id.marker_number);
            textView.setText("足迹数:"+getfootnum()+"    旗子数:"+getflagnum());
        }
    }
}
