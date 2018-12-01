package com.androidluckyguys.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidluckyguys.R;
import com.androidluckyguys.presenter.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class More extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button confirm;
    private EditText password_confirm;
    private EditText number_text;
    private ImageView backtodaily;
    private TextView log_regi_trans;
    Boolean islogin=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        //去除标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        password_confirm=(EditText)findViewById(R.id.confirm_password);
        number_text=(EditText)findViewById(R.id.number_text);
        log_regi_trans=(TextView)findViewById(R.id.log_regi_trans);
        log_regi_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (islogin){
                    password_confirm.setVisibility(View.VISIBLE);
                    number_text.setVisibility(View.VISIBLE);
                    islogin=false;
                    confirm.setText("注册");
                    log_regi_trans.setText("已有账号？登录");
                }
                else {
                    password_confirm.setVisibility(View.GONE);
                    number_text.setVisibility(View.GONE);
                    islogin=true;
                    confirm.setText("登录");
                    log_regi_trans.setText("没有账号？注册");
                }
            }
        });
        //点击返回按钮回到ARmap界面
        backtodaily=(ImageView)findViewById(R.id.backtodaily);
        backtodaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                More.this.finish();
            }
        });


        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        confirm=(Button)findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (islogin)
                login();
                else register();

            }
        });
    }

    private void register() {

        if (username.getText().toString().isEmpty()){
            Toast.makeText(More.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.getText().toString().isEmpty()){
            Toast.makeText(More.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password_confirm.getText().toString().isEmpty()){
            Toast.makeText(More.this, "请确认密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (number_text.getText().toString().isEmpty()){
            Toast.makeText(More.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.getText().toString().equals(password_confirm.getText().toString())){
            Toast.makeText(More.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        HttpUtils.PATH="http://192.168.123.234:8080/LittleTest/register";
        loginHandle(username.getText().toString(), password.getText().toString());
    }


    private void login() {
        // TODO Auto-generated method stub
        String userName = null;
        String passWord = null;

        ///< 简单判断用户是否输入用户名，是否输入密码
        if ((userName = username.getText().toString()).isEmpty())
        {
            if (password.getText().toString().isEmpty())
            {
                Toast.makeText(More.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(More.this, "请输入用户名！", Toast.LENGTH_SHORT).show();
            return;
        }
        else if ((passWord = password.getText().toString()).isEmpty())
        {
            Toast.makeText(More.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        HttpUtils.PATH = "http://192.168.123.234:8080/LittleTest/Login";
        loginHandle(userName, passWord);
    }

    private void loginHandle(final  String userName,final String passWord) {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ///< 发送用户名和密码到服务器进行校验，并获得服务器返回值
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName);
                params.put("pwd", passWord);
                String encode = "utf-8";

                //            Toast.makeText(LoginActivity.this, params.get("user_name"), Toast.LENGTH_SHORT).show();
                //            Toast.makeText(LoginActivity.this, params.get("password"), Toast.LENGTH_SHORT).show();

                String response = HttpUtils.sendMessage(params, encode);
                int responseCode=Integer.parseInt(response.substring(0,1));
                Looper.prepare();
                if (0 == responseCode)
                {
                    Toast.makeText(More.this, "登录成功", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                        editor.putInt("user_id", Integer.parseInt(response.substring(1)));
                        editor.commit();//提交修改
                        finish();

                }
                else if (1 == responseCode)
                {
                    Toast.makeText(More.this, "用户名错误!", Toast.LENGTH_SHORT).show();
                }
                else if (2 == responseCode)
                {
                    Toast.makeText(More.this, "密码错误!", Toast.LENGTH_SHORT).show();
                }
                else if (3==responseCode)
                {
                    Toast.makeText(More.this, "用户名已存在!", Toast.LENGTH_SHORT).show();
                }
                else if (4==responseCode){
                    Toast.makeText(More.this, "注册成功!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(More.this, "异常!", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();

                //执行完毕后给handler发送一个空消息
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    //定义Handler对象
    private Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            // TODO 处理UI
            Toast.makeText(More.this, "结束", Toast.LENGTH_SHORT).show();
        }
    };
}
