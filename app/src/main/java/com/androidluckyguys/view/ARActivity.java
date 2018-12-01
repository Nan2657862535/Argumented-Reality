package com.androidluckyguys.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Matrix;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidluckyguys.R;
import com.androidluckyguys.listener.Mylocationlistener;
import com.androidluckyguys.model.ARPoint;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;


/**
 * ARActivity为主要界面，其他的为附加功能
 *
 *
 *
 * */
//ak  md2H7GauTf9yNE2yrDaWNt83e9unmoaB
public class ARActivity extends AppCompatActivity implements SensorEventListener, LocationListener,CameraBridgeViewBase.CvCameraViewListener2 {


    final static String TAG = "ARActivity";
    private SurfaceView surfaceView;
    private FrameLayout cameraContainerLayout;
    private AROverlayView arOverlayView;
    private Camera camera;
    private ARCamera arCamera;
    public static TextView tvCurrentLocation;

    private SensorManager sensorManager;
    //需要两个Sensor
    private Sensor aSensor;
    private Sensor mSensor;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];

    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private LocationManager locationManager;
    public Location location;
    public static BDLocation bdLocation;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;
    private int LOGIN_REGISTER=1;
    public LocationClient mLocationClient = null;
    public Mylocationlistener myListener=new Mylocationlistener();

    public ImageView more;
    public ImageView changemap;
    public static PoiSearch poiSearch;
    private ArrayList<String> adapterlist;
    private Spinner spinner;
    private CameraBridgeViewBase mCVCamera;

//缓存相机每帧输入的数据
    private Mat mRgba;

    //建立连接
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override

        public void onManagerConnected(int status) {

            switch (status) {

                case LoaderCallbackInterface.SUCCESS:

                {

                    Log.e(TAG, "OpenCV loaded successfully");

                    mCVCamera.enableView();

                } break;

                default:

                {

                    super.onManagerConnected(status);

                } break;

            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_ar);
//去除标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }


//初始化百度地图sdk
        SDKInitializer.initialize(getApplicationContext());

        // 设置检索监听器
        //初始化spinner
        initcontrol();
spinnerinit();

        mCVCamera = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mCVCamera.setVisibility(SurfaceView.VISIBLE);
        mCVCamera.setCvCameraViewListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BDlocationinit();
        sensorinit();
        requestLocationPermission();
        requestCameraPermission();
        registerSensors();


        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Internal OpenCVlibrary not found. Using OpenCV Manager for initialization,配置失败");
            //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this,mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV libraryfound inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


        initAROverlayView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCVCamera != null)
            mCVCamera.disableView();
    }

    private void initcontrol() {
        cameraContainerLayout = (FrameLayout) findViewById(R.id.camera_container_layout);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);

        //点击登录按钮，跳转到登录页面
        more=(ImageView)findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ARActivity.this,More.class);
                startActivityForResult(intent,LOGIN_REGISTER);
            }
        });

        //跳转到二维地图，即dailymap
        changemap=(ImageView)findViewById(R.id.change);
        changemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ARActivity.this,DailyMap.class);
                startActivity(intent);
            }
        });


        tvCurrentLocation = (TextView) findViewById(R.id.tv_current_location);
        arOverlayView = new AROverlayView(this);


        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(poiSearchListener);
    }

    private void spinnerinit() {

        adapterlist=new ArrayList<>();
        adapterlist.add("餐饮");
        adapterlist.add("景点");
        adapterlist.add("学校");
        adapterlist.add("公交站");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,adapterlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner= (Spinner)findViewById(R.id.choosetype);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();

                nearbySearchOption.location(new LatLng(location.getLatitude(), location.getLongitude()));
                nearbySearchOption.radius(1000);// 检索半径，单位是米0
                nearbySearchOption.keyword(adapterlist.get(position));
                nearbySearchOption.pageNum(1);
                poiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**

     * POI检索监听器，获取附近兴趣点

     */

    OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {


        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果
            Log.d("地点维度地点维度地点维度",result.location.latitude+"");
        }

        public void onGetPoiIndoorResult(PoiIndoorResult var1) {

        }

        @Override

        public void onGetPoiResult(PoiResult poiResult) {


            if (poiResult == null

                    || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果

                Toast.makeText(ARActivity.this, "未找到结果",

                        Toast.LENGTH_LONG).show();

                return;

            }


            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回


                //

                int totalPage = poiResult.getTotalPageNum();// 获取总分页数
                Toast.makeText(

                        ARActivity.this,

                        "总共查到" + poiResult.getTotalPoiNum() + "个兴趣点, 分为"

                                + totalPage + "页", Toast.LENGTH_SHORT).show();


            }
            //AROverlayView.arPoints.clear();
           for (int i=0;i<poiResult.getAllPoi().size()/2;i++){
               PoiInfo poiInfo=poiResult.getAllPoi().get(i);
               AROverlayView.arPoints.add(new ARPoint(poiInfo.name/*+poiInfo.location.latitude+" "+poiInfo.location.longitude*/, poiInfo.location.latitude, poiInfo.location.longitude, 0));
               Log.d(TAG, "onGetPoiResult: 得到POI维度维度"+poiResult.getAllPoi().get(0).name+poiResult.getAllPoi().get(0).location.latitude);
           }

        }
    };

    //初始化传感器
        private void sensorinit() {
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        //更新显示数据的方法
        calculateOrientation();
    }

    //初始化百度定位
    private void BDlocationinit() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true

        option.setIsNeedLocationPoiList(true);
//可选，是否需要周边POI信息，默认为不需要，即参数为false
        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        mLocationClient.start();

    }

    //得到手机方向
    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);
        // 要经过一次数据格式的转换，转换为度
        values[0] = (float) Math.toDegrees(values[0]);
        //Log.i(TAG, values[0]+"");
        //values[1] = (float) Math.toDegrees(values[1]);
        //values[2] = (float) Math.toDegrees(values[2]);
        if(values[0] >= -5 && values[0] < 5){
            Log.i(TAG, "正北");
        }
        else if(values[0] >= 5 && values[0] < 85){
            Log.i(TAG, "东北");
        }
        else if(values[0] >= 85 && values[0] <=95){
            Log.i(TAG, "正东");
        }
        else if(values[0] >= 95 && values[0] <175){
            Log.i(TAG, "东南");
        }
        else if((values[0] >= 175 && values[0] <= 180) || (values[0]) >= -180 && values[0] < -175){
            Log.i(TAG, "正南");
        }
        else if(values[0] >= -175 && values[0] <-95){
            Log.i(TAG, "西南");
        }
        else if(values[0] >= -95 && values[0] < -85){
            Log.i(TAG, "正西");
        }
        else if(values[0] >= -85 && values[0] <-5){
            Log.i(TAG, "西北");
        }
        ARPoint.value=values[0];
    }



    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        mLocationClient.stop();
        sensorManager.unregisterListener(this);
    }

    public void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        } else {
            initARCameraView();
        }
        //initARCameraView();
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ARActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {
            initLocationService();
        }
        //initLocationService();
    }

    public void initAROverlayView() {
        if (arOverlayView.getParent() != null) {
            ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
        }
        cameraContainerLayout.addView(arOverlayView);
    }

    public void initARCameraView() {
        reloadSurfaceView();

        if (arCamera == null) {
            arCamera = new ARCamera(this, surfaceView);
        }
        if (arCamera.getParent() != null) {
            ((ViewGroup) arCamera.getParent()).removeView(arCamera);
        }
        cameraContainerLayout.addView(arCamera);
        arCamera.setKeepScreenOn(true);
        initCamera();
    }

    private void initCamera() {
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
               // camera = Camera.open();
                //camera.startPreview();
               // arCamera.setCamera(camera);
            } catch (RuntimeException ex){
                Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void reloadSurfaceView() {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }

        cameraContainerLayout.addView(surfaceView);
    }

    private void releaseCamera() {
        if(camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            arCamera.setCamera(null);
            camera.release();
            camera = null;
        }
    }

    private void registerSensors() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    //核心代码在这，当转动手机时，更新方向矩阵
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magneticFieldValues = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometerValues = sensorEvent.values;
        calculateOrientation();


        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrixFromVector = new float[16];
            float[] projectionMatrix = new float[16];
            float[] rotatedProjectionMatrix = new float[16];

            SensorManager.getRotationMatrixFromVector(rotationMatrixFromVector, sensorEvent.values);

            if (arCamera != null) {
                projectionMatrix = arCamera.getProjectionMatrix();
            }

            //更新矩阵，到ArOverlayView查看方法
            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrixFromVector, 0);
            this.arOverlayView.updateRotatedProjectionMatrix(rotatedProjectionMatrix);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do nothing
    }

    private void initLocationService() {

       if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        try   {
            this.locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled)    {
                // cannot get location
                this.locationServiceAvailable = false;
            }

            this.locationServiceAvailable = true;

            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null)   {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location==null){
                        locationManager.requestLocationUpdates("gps", 60000, 1, this);
                    }
                    updateLatestLocation();
                }
            }

            if (isGPSEnabled)  {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null)  {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location==null){
                        locationManager.requestLocationUpdates("gps", 60000, 1, this);
                    }
                    updateLatestLocation();
                }
            }
            locationManager.requestLocationUpdates("gps", 3000, 5,this);
        } catch (Exception ex)  {
            Log.e(TAG, ex.getMessage());

        }
    }

    private void updateLatestLocation() {
        if (arOverlayView !=null) {
            arOverlayView.updateCurrentLocation(location);
            tvCurrentLocation.setText(String.format("lat: %s \nlon: %s \naltitude: %s \n",
                    location.getLatitude(), location.getLongitude(), location.getAltitude()));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
      /*  Log.d("位置改变","变了");
        this.location=location;
        updateLatestLocation();*/
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==LOGIN_REGISTER){

        }
    }


    private void drawoutlinde(Mat blurredImage) {
        List<MatOfPoint> contours=new ArrayList<>();
        Imgproc.findContours(blurredImage,contours,new Mat(),Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        double maxVal = 0;
        int maxValIdx = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (maxVal < contourArea)
            {
                maxVal = contourArea;
                maxValIdx = contourIdx;
            }
        }

        Mat mRgba=new Mat();
        mRgba.create(blurredImage.rows(), blurredImage.cols(), CvType.CV_8UC3);
        //绘制检测到的轮廓
        Imgproc.drawContours(mRgba, contours, maxValIdx, new Scalar(0,255,0), 5);
    }



    @Override
public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

            mRgba = inputFrame.rgba();
       Mat mGray = inputFrame.gray();
        Mat cannyMat = new Mat();
        Mat lines=new Mat();
        Imgproc.Canny(mGray, cannyMat, 10, 1000, 3, false);
        Imgproc.HoughLinesP(cannyMat, lines, 1, Math.PI/180, 20, 150, 10);
           for (int x = 0; x < lines.cols() && x < 1000; x++) {
                      double[] vec = lines.get(0, x);
                if(vec!=null) {
                        double x1 = vec[0],
                                      y1 = vec[1],
                                               x2 = vec[2],
                                               y2 = vec[3];
                            Point start = new Point(x1, y1);
                             Point end = new Point(x2, y2);
                    Imgproc.line(mRgba, start, end, new Scalar(255,0,0), 3);
                          }
                 }
        Point start = new Point(0, 0);
        Point end = new Point(200, 200);
        Imgproc.line(mRgba, start, end, new Scalar(255,0,0), 3);
          return mRgba;
        }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }
}
