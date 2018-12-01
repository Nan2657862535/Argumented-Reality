package com.androidluckyguys.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidluckyguys.R;
import com.androidluckyguys.presenter.HttpUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class FlagsInfo extends AppCompatActivity {
private TextView markid;
private String marker_id;
 private String latitude;
 private String longitude;
 private Button save;
 private Button cancel;
 private EditText travel_plan;
 private String travelplan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flags_info);

        //去除标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        markid=(TextView) findViewById(R.id.markid);

        markid.setText(this.getIntent().getStringExtra("markid"));
        marker_id=this.getIntent().getStringExtra("marker_id");
        latitude=this.getIntent().getStringExtra("latitude");
        longitude=this.getIntent().getStringExtra("longitude");
        travelplan=this.getIntent().getStringExtra("travel_plan");
        travel_plan=(EditText)findViewById(R.id.editText1);
        travel_plan.setText(travelplan);
        cancel=(Button)findViewById(R.id.button2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result=streampost(latitude,longitude);
                markid.setText(result);
            }
        }).start();



        save=(Button)findViewById(R.id.button1);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, String> params = new HashMap<String, String>();
                      /*  params.put("user_id", 4+"");
                        params.put("latitude", latitude);
                        params.put("longitude", longitude);
                        params.put("place_name", "日本东京");
                        params.put("travel_plan", travel_plan.getText().toString());
                        */
                        params.put("marker_id", marker_id);
                        params.put("travel_plan", travel_plan.getText().toString());
                        String encode = "utf-8";
                        int resultcode= HttpUtils.changeflaginfo(params,encode);
                        if (resultcode==1){
                            //Toast.makeText(FootsInfo.this, "添加足迹信息成功", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    }
                }).start();
            }
        });

    }

    public String streampost(String latitude,String longitude) {
        URL infoUrl = null;

//        String remote_addr="http://api.weatherdt.com/common/?area="+citycode+"&type="+datacode+"&key=90d48635e440fc4c032e4f5b5b11e996";
        String remote_addr="http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=35.658651,139.745415&output=json&pois=1&ak=md2H7GauTf9yNE2yrDaWNt83e9unmoaB&mcode=6A:EA:FB:A8:01:0B:AA:41:22:50:EC:1D:D8:A5:96:D2:EF:3A:68:BD";
        try {
            infoUrl = new URL(remote_addr);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpconnection = (HttpURLConnection) connection;
            httpconnection.setRequestMethod("GET");
            httpconnection.setReadTimeout(5000);
            InputStream inStream = httpconnection.getInputStream();
            ByteArrayOutputStream data = new ByteArrayOutputStream();

            byte[] buffer = new byte[1000];
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
}
