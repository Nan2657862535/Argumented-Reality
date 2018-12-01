package com.androidluckyguys.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

@SuppressWarnings("unused")
public class HttpUtils {
    ///< 请求服务URL
    public   static String PATH = "http://192.168.123.234:8080/LittleTest/Login";
    private static URL url;

    public HttpUtils() {
        // TOiDO Auto-generated constructor stub
    }

    /**
     * 向服务端提交数据
     * @param params    url参数
     * @param encode    字节编码
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String sendMessage(Map<String, String> params, String encode){
        ///< 初始化URL
        StringBuffer buffer = new StringBuffer();
//            StringBuffer buffer = new StringBuffer();

        if (null != params && !params.isEmpty()){
            for (Map.Entry<String, String> entry : params.entrySet()){
                buffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue())).append("&");
            }
            ///< 删除多余的&
            buffer.deleteCharAt(buffer.length() - 1);
        }

        ///< show url
        //System.out.println(buffer.toString());

        try {
            url = new URL(PATH);
            if (null != url)
            {
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("POST");    ///< 设置请求方式
                urlConnection.setDoInput(true);            ///< 表示从服务器获取数据
                urlConnection.setDoOutput(true);        ///< 表示向服务器发送数据

                byte[] data = buffer.toString().getBytes();
                ///< 设置请求体的是文本类型
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
                ///< 获得输出流
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(data);

                // 取得sessionid.

                 String cookieval = urlConnection.getHeaderField("set-cookie");
                String sessionid=null;
                if(cookieval != null) {
                    Log.d("result","登陆过登陆过登陆过登陆过");
                    //sessionid = cookieval.substring(0, cookieval.indexOf(";"));
                }
//sessionid值格式：JSESSIONID=AD5F5C9EEB16C71EC3725DBF209F6178，是键值对，不是单指值
                //发送设置cookie：
                if(sessionid != null) {
                    //urlConnection.setRequestProperty("cookie", sessionid);
                }

                outputStream.close();
                ///< 获得服务器的响应结果和状态码
                int responseCode = urlConnection.getResponseCode();
                //System.out.println("" + responseCode);
                if (200 == responseCode)
                {
                    return changeInputStream(urlConnection.getInputStream(), encode);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获得网络返回值【0 - 正确    1 - 用户名错误    2 - 密码错误】
     * @param inputStream
     * @param encode
     * @return
     */
    private static String changeInputStream(InputStream inputStream, String encode) {
        // TODO Auto-generated method stub
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (null != inputStream)
        {
            try {
                while ((len = inputStream.read(data)) != -1)
                {
                    outputStream.write(data, 0, len);
                }
                result = new String(outputStream.toByteArray(), encode);
                //System.out.println(result);


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }



    public static int addfootinfo(Map<String, String> params, String encode){
        StringBuffer buffer = new StringBuffer();
        if (null != params && !params.isEmpty()){
            for (Map.Entry<String, String> entry : params.entrySet()){
                buffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue())).append("&");
            }
            ///< 删除多余的&
            buffer.deleteCharAt(buffer.length() - 1);
        }

        ///< show url
        //System.out.println(buffer.toString());

        try {
            String PATH="http://192.168.123.234:8080/LittleTest/AddFootInfo";
            url = new URL(PATH);
            if (null != url)
            {
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestProperty("Accept-Charset", "utf-8");
                urlConnection.setRequestProperty("contentType", "utf-8");
                urlConnection.setRequestMethod("POST");    ///< 设置请求方式
                urlConnection.setDoInput(true);            ///< 表示从服务器获取数据
                urlConnection.setDoOutput(true);        ///< 表示向服务器发送数据
                byte[] data = buffer.toString().getBytes();
                ///< 设置请求体的是文本类型
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
                ///< 获得输出流
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(data);
                outputStream.close();
                ///< 获得服务器的响应结果和状态码
                int responseCode = urlConnection.getResponseCode();
                if (200 == responseCode)
                {
                    String s=changeInputStream(urlConnection.getInputStream(), encode);
                    return Integer.parseInt(s);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return -1;
    }

    public static int addflaginfo(Map<String, String> params, String encode)
    {
        StringBuffer buffer = new StringBuffer();
        if (null != params && !params.isEmpty()){
            for (Map.Entry<String, String> entry : params.entrySet()){
                buffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue())).append("&");
            }
            ///< 删除多余的&
            buffer.deleteCharAt(buffer.length() - 1);
        }

        ///< show url
        //System.out.println(buffer.toString());

        try {
            String PATH="http://192.168.123.234:8080/LittleTest/AddFlagInfo";
            url = new URL(PATH);
            if (null != url)
            {
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("POST");    ///< 设置请求方式
                urlConnection.setDoInput(true);            ///< 表示从服务器获取数据
                urlConnection.setDoOutput(true);        ///< 表示向服务器发送数据

                byte[] data = buffer.toString().getBytes();
                ///< 设置请求体的是文本类型
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
                ///< 获得输出流
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(data);
                outputStream.close();
                ///< 获得服务器的响应结果和状态码
                int responseCode = urlConnection.getResponseCode();
                if (200 == responseCode)
                {
                    String s=changeInputStream(urlConnection.getInputStream(), encode);
                    return Integer.parseInt(s);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return -1;
    }

    public static int changefootinfo(Map<String, String> params, String encode){
        StringBuffer buffer = new StringBuffer();
        if (null != params && !params.isEmpty()){
            for (Map.Entry<String, String> entry : params.entrySet()){
                buffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue())).append("&");
            }
            ///< 删除多余的&
            buffer.deleteCharAt(buffer.length() - 1);
        }

        ///< show url
        //System.out.println(buffer.toString());

        try {
            String PATH="http://192.168.123.234:8080/LittleTest/ChangeFootInfo";
            url = new URL(PATH);
            if (null != url)
            {
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestProperty("Accept-Charset", "utf-8");
                urlConnection.setRequestProperty("contentType", "utf-8");
                urlConnection.setRequestMethod("POST");    ///< 设置请求方式
                urlConnection.setDoInput(true);            ///< 表示从服务器获取数据
                urlConnection.setDoOutput(true);        ///< 表示向服务器发送数据
                byte[] data = buffer.toString().getBytes();
                ///< 设置请求体的是文本类型
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
                ///< 获得输出流
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(data);
                outputStream.close();
                ///< 获得服务器的响应结果和状态码
                int responseCode = urlConnection.getResponseCode();
                if (200 == responseCode)
                {
                    return 1;//changeInputStream(urlConnection.getInputStream(), encode);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return -1;
    }

    public static int changeflaginfo(Map<String, String> params, String encode){
        StringBuffer buffer = new StringBuffer();
        if (null != params && !params.isEmpty()){
            for (Map.Entry<String, String> entry : params.entrySet()){
                buffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue())).append("&");
            }
            ///< 删除多余的&
            buffer.deleteCharAt(buffer.length() - 1);
        }

        ///< show url
        //System.out.println(buffer.toString());

        try {
            String PATH="http://192.168.123.234:8080/LittleTest/ChangeFlagInfo";
            url = new URL(PATH);
            if (null != url)
            {
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestProperty("Accept-Charset", "utf-8");
                urlConnection.setRequestProperty("contentType", "utf-8");
                urlConnection.setRequestMethod("POST");    ///< 设置请求方式
                urlConnection.setDoInput(true);            ///< 表示从服务器获取数据
                urlConnection.setDoOutput(true);        ///< 表示向服务器发送数据
                byte[] data = buffer.toString().getBytes();
                ///< 设置请求体的是文本类型
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
                ///< 获得输出流
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(data);
                outputStream.close();
                ///< 获得服务器的响应结果和状态码
                int responseCode = urlConnection.getResponseCode();
                if (200 == responseCode)
                {
                    return 1;//changeInputStream(urlConnection.getInputStream(), encode);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return -1;
    }
} 