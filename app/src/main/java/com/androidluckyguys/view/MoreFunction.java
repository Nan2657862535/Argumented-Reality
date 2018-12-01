package com.androidluckyguys.view;

import android.animation.ArgbEvaluator;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidluckyguys.R;
import com.androidluckyguys.userdefinedview.MyImageView;
import com.androidluckyguys.userdefinedview.RoundImageView;

import java.util.ArrayList;

public class MoreFunction extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private MyImageView mIvHome; // tab 消息的imageview
    private TextView mTvHome;   // tab 消息的imageview

    private MyImageView mIvCategory; // tab 通讯录的imageview
    private TextView mTvCategory;

    private MyImageView mIvFind;  // tab 发现的imageview
    private TextView mTvFind;

    private MyImageView mIvMine; // tab 我的imageview
    private TextView mTvMine;
    private float mSelfHeight = 0;
    private ArrayList<View> mFragments;
    private ArgbEvaluator mColorEvaluator;

    private int mTextNormalColor;// 未选中的字体颜色
    private int mTextSelectedColor;// 选中的字体颜色
    private LinearLayout mLinearLayoutHome;
    private LinearLayout mLinearLayoutCategory;
    private LinearLayout mLinearLayoutFind;
    private LinearLayout mLinearLayoutMine;

    private RoundImageView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_function);
        //去除标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        initColor();//也就是选中未选中的textview的color
        initView();// 初始化控件
        initData(); // 初始化数据(也就是fragments)
        initSelectImage();// 初始化渐变的图片
        aboutViewpager(); // 关于viewpager
        setListener(); // viewpager设置滑动监听
    }

    private void initSelectImage() {
        mIvHome.setImages(R.drawable.message, R.drawable.message);
        mIvCategory.setImages(R.drawable.find, R.drawable.find);
        mIvMine.setImages(R.drawable.user, R.drawable.user);
    }

    private void initColor() {
        /*mTextNormalColor = getResources().getColor(Color.green(1));
        mTextSelectedColor = getResources().getColor(Color.green(1));*/
    }


    private void setListener() {
        //下面的tab设置点击监听
        mLinearLayoutHome.setOnClickListener(this);
        mLinearLayoutCategory.setOnClickListener(this);
        mLinearLayoutMine.setOnClickListener(this);
    }


    private void setTabTextColorAndImageView(int position, float positionOffset) {
        mColorEvaluator = new ArgbEvaluator();  // 根据偏移量 来得到
        int  evaluateCurrent =(int) mColorEvaluator.evaluate(positionOffset,mTextSelectedColor , mTextNormalColor);//当前tab的颜色值
        int  evaluateThe =(int) mColorEvaluator.evaluate(positionOffset, mTextNormalColor, mTextSelectedColor);// 将要到tab的颜色值
        switch (position) {
            case 0:
                mTvHome.setTextColor(evaluateCurrent);  //设置消息的字体颜色
                mTvCategory.setTextColor(evaluateThe);  //设置通讯录的字体颜色

                mIvHome.transformPage(positionOffset);  //设置消息的图片
                mIvCategory.transformPage(1-positionOffset); //设置通讯录的图片
                break;
            case 1:
                mTvCategory.setTextColor(evaluateCurrent);
                mTvFind.setTextColor(evaluateThe);

                mIvCategory.transformPage(positionOffset);
                mIvFind.transformPage(1-positionOffset);
                break;
            case 2:
                mTvFind.setTextColor(evaluateCurrent);
                mTvMine.setTextColor(evaluateThe);

                mIvFind.transformPage(positionOffset);
                mIvMine.transformPage(1-positionOffset);
                break;

        }
    }

    private void initData() {
        mFragments = new ArrayList<>();
        initview();
        initview1();
        initview2();

    }

    private void initview2() {
        //初始化我这个page
        View view=getLayoutInflater().inflate(R.layout.mine,null);
        mFragments.add(view);
    }

    private void initview1() {
        //初始化狸友圈
        View view1=getLayoutInflater().inflate(R.layout.daily_circles,null);
        test=(RoundImageView) view1.findViewById(R.id.test);
        AppBarLayout mAppBar=(AppBarLayout)view1.findViewById(R.id.app_bar);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float mTestScaleX=0,mTestScaleY=0;
                final float screenW = getResources().getDisplayMetrics().widthPixels;
                final float initHeight = getResources().getDimension(R.dimen.app_bar_height);
                if (mSelfHeight == 0) {
                    //得到测试按钮的高度差
                    float distanceTest = test.getY() - (180 - test.getHeight()) / 2.0f;
                    //得到测试按钮的水平差值  屏幕宽度一半 - 按钮宽度一半
                    float distanceSubscribeX = screenW / 2.0f - (test.getWidth() / 2.0f);

                    //得到高度差缩放比值  高度差／能滑动总长度 以此类推
                    mTestScaleY = distanceTest / 180;
                    mTestScaleX = distanceSubscribeX / initHeight;
                }
                //得到文本框、头像缩放值 不透明 ->透明  文本框x跟y缩放
                float scale = 1.0f - (-verticalOffset) / 130;


                //设置测试按钮x跟y平移
                //test.setTranslationY(mTestScaleY * verticalOffset);
                test.setTranslationX(-mTestScaleX * verticalOffset);
            }
        });


        mFragments.add(view1);
    }

    private void initview() {
        //初始化旅游资讯的page
        View view=getLayoutInflater().inflate(R.layout.tra_message,null);
        WebView webView=(WebView) view.findViewById(R.id.showtra_info);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheMaxSize(1024*1024*8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        //设置 缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);//设置适应Html5 重点是这个设置
        webView.setWebViewClient(new WebViewClient() {
            //覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
         webView.loadUrl("http://www.tuniu.com/");
        //webView.loadData("http://www.tuniu.com/", "text/html", "utf-8");
        mFragments.add(view);

    }

    private void aboutViewpager() {

        mViewPager.setAdapter(new Myadapter()); // 设置adapter
    }

    private void initView() {
        mLinearLayoutHome = (LinearLayout) findViewById(R.id.ll_home);
        mLinearLayoutCategory = (LinearLayout) findViewById(R.id.ll_categroy);
        mLinearLayoutMine = (LinearLayout) findViewById(R.id.ll_mine);
        mViewPager = (ViewPager) findViewById(R.id.vp);

        mIvHome = (MyImageView) findViewById(R.id.iv1);  // tab 微信 imageview
        mTvHome = (TextView) findViewById(R.id.rb1);  //  tab  微信 字

        mIvCategory = (MyImageView) findViewById(R.id.iv2); // tab 通信录 imageview
        mTvCategory = (TextView) findViewById(R.id.rb2);  // tab   通信录 字

        mIvMine = (MyImageView) findViewById(R.id.iv3);   // tab 我 imageview
        mTvMine = (TextView) findViewById(R.id.rb3);    // tab   我 字
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_home:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.ll_categroy:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.ll_mine:
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    private class Myadapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v=mFragments.get(position);

            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v=mFragments.get(position);
            container.removeView(v);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;//官方也是这样写的
        }
    }
}
