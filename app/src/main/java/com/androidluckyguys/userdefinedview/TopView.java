package com.androidluckyguys.userdefinedview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.androidluckyguys.R;
import com.androidluckyguys.view.DailyMap;

//为弹出来显示里程数的popupwindow
public class TopView {

    Context context;
    private PopupWindow popupWindow;
    public  View popupWindowView;


    public TopView(Context context){
        this.context=context;

        initPopupWindow();
    }

    /**
     * 初始化
     */
    public void initPopupWindow() {

        //通过layout文件夹里面的topwidow构造popupwindow
        popupWindowView = LayoutInflater.from(context).inflate(R.layout.topwindow, null);
        //TextView marker_number=(TextView)popupWindowView.findViewById(R.id.marker_number);
        //marker_number.setText("足迹数"+DailyMap.getfootnum()+"   旗子数"+DailyMap.getflagnum());
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

//设置popupwindow进出的动画效果
        popupWindow.setAnimationStyle(R.style.TopSelectAnimationShow);
        // 菜单背景色。加了一点透明度
        ColorDrawable dw = new ColorDrawable(0xddffffff);
        popupWindow.setBackgroundDrawable(dw);

        //没有焦点，即点击外部时不自动消失
        popupWindow.setFocusable(false);
        //TODO 注意：这里的 R.layout.activity_main，不是固定的。你想让这个popupwindow盖在哪个界面上面。就写哪个界面的布局。这里以主界面为例
        popupWindow.showAtLocation(LayoutInflater.from(context).inflate(R.layout.activity_daily_map, null),
                Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);

        // 设置背景半透明
        backgroundAlpha(0.8f);

        //防止PopupWindow被软件盘挡住
       // popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
       // popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        popupWindow.setOnDismissListener(new popupDismissListener());

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*
                 * if( popupWindow!=null && popupWindow.isShowing()){
                 * popupWindow.dismiss(); popupWindow=null; }
                 */
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });
        dealWithSelect();
    }


    /**
     * 处理点击事件
     */
    private void dealWithSelect(){
        //点击了关闭图标（右上角图标）
        popupWindowView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DailyMap.open_tra_title.setVisibility(View.VISIBLE);
                dimss();
            }
        });
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        ((Activity) context).getWindow().setAttributes(lp);
    }

    class popupDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    //点击收起图标消失popupwindow
    public void dimss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

}