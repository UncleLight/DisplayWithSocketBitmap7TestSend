package com.example.administrator.displaywithsocketbitmap7testsend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.graphics.Bitmap.createScaledBitmap;

public class ShowImageActivity extends AppCompatActivity {
    private static final String TAG = "ShowImageActivity";

    private int width = 600;
    private int height = 640;

    private PopupWindow mPopWindow;

    private ImageView iv;
    private Button bt_show;
    private Button bt_back;

    MyButtonListener myButtonListener;

    class MyListViewListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String result = parent.getItemAtPosition(position).toString();
//            Toast.makeText(ShowImageActivity.this, result, Toast.LENGTH_SHORT).show();

            Bitmap bm = BitmapFactory.decodeFile(FileOperation.SAVE_DIR + "/" + result);
            bm = createScaledBitmap(bm, width, height, false);//将图片缩放至指定大小
            iv.setImageBitmap(bm);
            mPopWindow.dismiss();
        }
    }

    class MyButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_show:
                    if (mPopWindow != null) {
                        mPopWindow.dismiss();
                    }
                    showPopupWindow();
                    break;
                case R.id.bt_back:
                    finish();
                    break;
            }
        }
    }

    /**
     * 显示PopupWindow
     */
    private void showPopupWindow() {
        String path = FileOperation.SAVE_DIR;
        File file = new File(path);
        File[] listFiles = file.listFiles();
        if (listFiles.length > 0) {//文件夹下有文件

            //获取contentView
            View contentView = LayoutInflater.from(ShowImageActivity.this).inflate(R.layout.popupwindow_showimage_layout, null);
            //获取PopupWindow布局中的ListView
            ListView lv = (ListView) contentView.findViewById(R.id.lv);

            //获取文件列表
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowImageActivity.this, R.layout.popupwindow_list_item_text, getPictureFromCatalogue(FileOperation.SAVE_DIR));
            //设置显示内容
            lv.setAdapter(adapter);
            //设置ListView的监听器
            lv.setOnItemClickListener(new MyListViewListener());

            //实例化PopupWindow
            mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        mPopWindow = new PopupWindow(contentView, 300, 300, true);
//        mPopWindow = new PopupWindow(contentView, (int) (getScreenWidth() * 0.9), (int) (getScreenHeight() * 0.5), true);

            //指定Activity和其布局文件，用于PopupWindow的显示
            View rootView = LayoutInflater.from(ShowImageActivity.this).inflate(R.layout.activity_show_image, null);
//        View rootView = LayoutInflater.from(ShowImageActivity.this).inflate(R.layout.activity_main, null);

            //设置PopupWindow击外区域可关闭，需要以下两句同时使用才可生效
            mPopWindow.setOutsideTouchable(true);
            mPopWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            //在指定的Activity中以指定的方式显示PopupWindow，同时设置偏移量
            mPopWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        } else { //文件夹下没有文件
            Toast.makeText(this, "存储文件夹下未找到照片", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 获取SD卡下目录中的文件列表
     *
     * @param innerSDCardPath
     * @return
     */
    private String[] getPictureFromCatalogue(String innerSDCardPath) {
//        String innersdcardpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(innerSDCardPath);
        File[] filearray = file.listFiles();
        List<String> listdata = new ArrayList<>();
        int i;
        for (i = 0; i < filearray.length; i++) {
            listdata.add(filearray[i].getName());
        }

        Collections.reverse(listdata);//倒序排列，将最新的照片显示在前面
        return listdata.toArray(new String[listdata.size()]);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        init();
//        initScreenParameter();

    }

    /**
     * 初始化控件
     */
    private void init() {
        iv = (ImageView) findViewById(R.id.iv);
        bt_show = (Button) findViewById(R.id.bt_show);
        bt_back = (Button) findViewById(R.id.bt_back);
        myButtonListener = new MyButtonListener();
        bt_show.setOnClickListener(myButtonListener);
        bt_back.setOnClickListener(myButtonListener);

        width = (int) (getScreenWidth()*0.9);
        height = (int) (getScreenHeight()*0.6);
    }

    /**
     * 返回屏幕高度，整形
     *
     * @return
     */
    public int getScreenHeight() {
        //获取WindowManager
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //Displaymetrics是取得手机屏幕大小的关键类
        DisplayMetrics outMetrice = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrice);
        return outMetrice.heightPixels;
    }

    /**
     * 返回屏幕宽度，整形
     *
     * @return
     */
    public int getScreenWidth() {
        //获取WindowManager
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //Displaymetrics是取得手机屏幕大小的关键类
        DisplayMetrics outMetrice = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrice);
        return outMetrice.widthPixels;
    }


    /**
     * 初始化绘图参数
     */
    private void initScreenParameter() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        //Displaymetrics是取得手机屏幕大小的关键类
        DisplayMetrics outMetrice = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrice);
//        width = (int) (outMetrice.widthPixels * Float.parseFloat((String) this.getResources().getString(R.string.display_width_percent)));
//        height = (int) (outMetrice.heightPixels * Float.parseFloat((String) this.getResources().getString(R.string.display_height_percent)));
    }
}


