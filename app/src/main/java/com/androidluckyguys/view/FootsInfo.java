package com.androidluckyguys.view;

import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidluckyguys.R;
import com.androidluckyguys.presenter.HttpUtils;
import com.androidluckyguys.presenter.UploadUtil;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FootsInfo extends AppCompatActivity {

    private static final int SELECT_PHOTO=100;
    private Button select_image;
    private ImageView imageView;
    private LatLng point;
    private String latitude;
    private String longitude;
    private String marker_id;
    private TextView foots_title;
    private String img_src;
    private EditText tra_thought;

    private GridView gridView1;              //网格显示缩略图
    private Button buttonPublish;            //发布按钮
    private final int IMAGE_OPEN = 1;        //打开图片标记
    private String pathImage;                //选择图片路径
    private Bitmap bmp;                      //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;     //适配器
    private Button upload;
    private String thought;
    private Button cancel;
    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if(resultCode==RESULT_OK && requestCode==IMAGE_OPEN) {
            Uri uri = data.getData();
            img_src=getRealPathFromUri(FootsInfo.this,uri);
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[] { MediaStore.Images.Media.DATA },
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }  //end if 打开图片
    }

    //刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(pathImage)){
            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.add(map);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.griditem_addpic,
                    new String[] { "itemImage"}, new int[] { R.id.imageView1});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if(view instanceof ImageView && data instanceof Bitmap){
                        ImageView i = (ImageView)view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView1.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FootsInfo.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foots_info);
        //去除标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        latitude=this.getIntent().getStringExtra("latitude");
        longitude=this.getIntent().getStringExtra("longitude");
        thought=this.getIntent().getStringExtra("thought");
        marker_id=getIntent().getStringExtra("marker_id");
        cancel=(Button)findViewById(R.id.button2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tra_thought=(EditText)findViewById(R.id.editText1);
        tra_thought.setText(thought);
        latitude=this.getIntent().getStringExtra("latitude");
        longitude=this.getIntent().getStringExtra("longitude");
        upload=(Button)findViewById(R.id.button1);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        /*
         * 防止键盘挡住输入框
         * 不希望遮挡设置activity属性 android:windowSoftInputMode="adjustPan"
         * 希望动态调整高度 android:windowSoftInputMode="adjustResize"
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_ADJUST_PAN);
        //锁定屏幕
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //获取控件对象
        gridView1 = (GridView) findViewById(R.id.gridView1);

        /*
         * 载入默认图片添加图片加号
         * 通过适配器实现
         * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
         */
        //获取资源图片加号
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.addpicture);
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.griditem_addpic,
                new String[] { "itemImage"}, new int[] { R.id.imageView1});
        /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
         * map.put("itemImage", R.drawable.img);
         * 解决方法:
         *              1.自定义继承BaseAdapter实现
         *              2.ViewBinder()接口实现
         *  参考 http://blog.csdn.net/admin_/article/details/7257901
         */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView1.setAdapter(simpleAdapter);

        /*
         * 监听GridView点击事件
         * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
         */
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if( imageItem.size() == 10) { //第一张为默认图片
                    Toast.makeText(FootsInfo.this, "图片数9张已满", Toast.LENGTH_SHORT).show();
                }
                else if(position == 0) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(FootsInfo.this, "添加图片", Toast.LENGTH_SHORT).show();
                    //选择图片
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                    //通过onResume()刷新数据
                }
                else {
                    dialog(position);
                    //Toast.makeText(MainActivity.this, "点击第"+(position + 1)+" 号图片",
                    //      Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void upload() {
        Toast.makeText(this, img_src, Toast.LENGTH_SHORT).show();
        uploadImage(img_src);
    }

    private void addfootinfo() {
        Map<String, String> params = new HashMap<String, String>();
        /*params.put("user_id", 4+"");
        //params.put("marker_id", markid);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("thought", tra_thought.getText().toString());
        params.put("photos_path", "H:\\\\eclipse-workspace\\\\.metadata\\\\.plugins\\\\org.eclipse.wst.server.core\\\\tmp0\\\\wtpwebapps\\\\LittleTest\\\\uploadphpto");
        */
        String encode = "utf-8";
        params.put("marker_id", marker_id);
        params.put("thought", tra_thought.getText().toString());
        int resultcode= HttpUtils.changefootinfo(params,encode);
        if (resultcode==1){
            //Toast.makeText(FootsInfo.this, "添加足迹信息成功", Toast.LENGTH_SHORT).show();
        this.finish();

        }
    }


    /**

     * 从相册选取图片

     */
 public void uploadImage(String path) {

        new Thread(new Runnable() {

            @Override

            public void run() {




                String result;


                String uploadurl = "http://192.168.123.234:8080/LittleTest/Upload";//SharedPreferencesUtil.getServerUrls(getActivity()) + "mobileqrcode/uploadsignimg.html";

                try {

                    File file = new File(img_src);

                    result = UploadUtil.uploadImage(file, uploadurl);

                    addfootinfo();

                } catch (Exception e) {

                    e.printStackTrace();

                }



            }

        }).start();



    }


    /**

     * 根据图片的Uri获取图片的绝对路径。@uri 图片的uri

     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null

     */

    public static String getRealPathFromUri(Context context, Uri uri) {

        if(context == null || uri == null) {

            return null;

        }

        if("file".equalsIgnoreCase(uri.getScheme())) {

            return getRealPathFromUri_Byfile(context,uri);

        } else if("content".equalsIgnoreCase(uri.getScheme())) {

            return getRealPathFromUri_Api11To18(context,uri);

        }

//        int sdkVersion = Build.VERSION.SDK_INT;

//        if (sdkVersion < 11) {

//            // SDK < Api11

//            return getRealPathFromUri_BelowApi11(context, uri);

//        }

////        if (sdkVersion < 19) {

////             SDK > 11 && SDK < 19

////            return getRealPathFromUri_Api11To18(context, uri);

//            return getRealPathFromUri_ByXiaomi(context, uri);

////        }

//        // SDK > 19

        return getRealPathFromUri_AboveApi19(context, uri);//没用到

    }



    //针对图片URI格式为Uri:: file:///storage/emulated/0/DCIM/Camera/IMG_20170613_132837.jpg

    private static String getRealPathFromUri_Byfile(Context context,Uri uri){

        String uri2Str = uri.toString();

        String filePath = uri2Str.substring(uri2Str.indexOf(":") + 3);

        return filePath;

    }



    /**

     * 适配api19以上,根据uri获取图片的绝对路径

     */


    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {

        String filePath = null;

        String wholeID = null;



        wholeID = DocumentsContract.getDocumentId(uri);



        // 使用':'分割

        String id = wholeID.split(":")[1];



        String[] projection = { MediaStore.Images.Media.DATA };

        String selection = MediaStore.Images.Media._ID + "=?";

        String[] selectionArgs = { id };



        Cursor cursor = context.getContentResolver().query(

                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,

                selection, selectionArgs, null);

        int columnIndex = cursor.getColumnIndex(projection[0]);



        if (cursor.moveToFirst()) {

            filePath = cursor.getString(columnIndex);

        }

        cursor.close();

        return filePath;

    }



    /**

     * //适配api11-api18,根据uri获取图片的绝对路径。

     * 针对图片URI格式为Uri:: content://media/external/images/media/1028

     */

    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {

        String filePath = null;

        String[] projection = { MediaStore.Images.Media.DATA };



        CursorLoader loader = new CursorLoader(context, uri, projection, null,

                null, null);

        Cursor cursor = loader.loadInBackground();



        if (cursor != null) {

            cursor.moveToFirst();

            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));

            cursor.close();

        }

        return filePath;

    }



    /**

     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径

     */

    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {

        String filePath = null;

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(uri, projection,

                null, null, null);

        if (cursor != null) {

            cursor.moveToFirst();

            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));

            cursor.close();

        }

        return filePath;

    }


}
