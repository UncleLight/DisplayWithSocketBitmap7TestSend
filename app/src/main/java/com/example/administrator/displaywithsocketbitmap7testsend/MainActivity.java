package com.example.administrator.displaywithsocketbitmap7testsend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int COMPLETED = 0x111;
    private static final String TAG = "MainActivity";

    private BorderRelativeLayout brl;
    private static GraphicsView graphicsView;
    private VerticalSeekBar vsb;

    private MyHandler handler = null;
    RevImageThread revImageThread = null;//继承Runnable
    Thread thread;

    private EditText et_ip;
    private EditText et_port;
    private Button bt_connect;
    private Button bt_takePicture;
    private Button bt_send;
    public static TextView tv_magnification_times;

    private Spinner sp_cam_para1;
    private Spinner sp_cam_para2;
    private Spinner sp_cam_para3;
    private Spinner sp_cam_para4;

    private static Bitmap bitmap;

    private String ip_address;
    private int port_number;

    public boolean setTimeWhenSaving = true;

    public short[] camera_parameter = {0, 0, 4, 0};

    public static int zoomLevel = 0;//缩放等级
    public static float[] zoom = {1f, 1.2f, 1.4f, 1.6f, 1.8f, 2f};//缩放比例

    static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                bitmap = (Bitmap) msg.obj;
                if (zoomLevel != 0){
                    zoomPicture();
                }
                graphicsView.setBitmap(bitmap);
                super.handleMessage(msg);
            }
        }
    }

    private class MyButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_connect:
                    ip_address = et_ip.getText().toString();
                    port_number = Integer.parseInt(et_port.getText().toString());
                    if (handler == null) {
                        starGetPicture();
                    } else {
                        changeGetPicture();
                    }
                    revImageThread.setSendData(camera_parameter);
//                    revImageThread.setSend(true);
                    break;
                case R.id.bt_takePicture:
                    takePicture();
                    break;
                case R.id.bt_send:
                    if (revImageThread != null) {
                        revImageThread.send();
                    }else {
                        starGetPicture();
                    }
            }
        }
    }

    private class MySeekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            for (int i=0; i<zoom.length; i++){
                if (progress == 0){
                    zoomLevel = 0;
                    break;
                }
                if (((float)(i+1)/zoom.length) > ((float)progress/100)){
                    zoomLevel = i;
                    break;
                }
            }
            tv_magnification_times.setText("x"+zoom[zoomLevel]);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 以图片中心点为基准，缩放图片
     */
    private static void zoomPicture() {
        int bm_width = bitmap.getWidth();
        int bm_height = bitmap.getHeight();
        int cut_start_X = (int) ((bm_width - bm_width/zoom[zoomLevel])/2);
        int cut_start_Y = (int) ((bm_height - bm_height/zoom[zoomLevel])/2);
        int cut_width = (int) (bm_width/zoom[zoomLevel]);
        int cut_height = (int) (bm_height/zoom[zoomLevel]);
        bitmap = Bitmap.createBitmap(bitmap, cut_start_X, cut_start_Y, cut_width, cut_height, null, false);
    }

    /**
     * 保存当前预览的图像
     */
    private void takePicture() {
        //判断是否安装SD卡
//        if (!android.os.Environment.getExternalStorageState().equals(
//                android.os.Environment.MEDIA_MOUNTED)) {
//            Toast.makeText(this, "请安装SD卡！", Toast.LENGTH_SHORT).show();
//        }
        //获取当前显示的图片
        final Bitmap picturebitmap = bitmap;
        //加载对应布局文件
        View saveView = getLayoutInflater().inflate(R.layout.save_alert_dialog, null);
        //获取对话框上的EditText组件
        final EditText pictureName = (EditText) saveView.findViewById(R.id.phone_name);
        if (setTimeWhenSaving) {
            DateTime dt = new DateTime();
            pictureName.setText(dt.getTimeStamp());
        }
        //获取对话框上的ImageView组件
        ImageView iv_show = (ImageView) saveView.findViewById(R.id.show);
        iv_show.setImageBitmap(picturebitmap);

        //对话框显示saveDialog组件
        new AlertDialog.Builder(MainActivity.this).setView(saveView)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (picturebitmap != null) {
                            try {
                                File file = new File(FileOperation.getSaveDir() +"/" + pictureName.getText().toString() + ".jpg");
                                if(file.createNewFile()){
                                    FileOutputStream fileOS = new FileOutputStream(file);//创建文件输出流对象
                                    //将图片以JPEG格式输出到输出流从中
                                    picturebitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOS);
                                    fileOS.flush();
                                    fileOS.close();
                                    Toast.makeText(MainActivity.this, "图片保存成功", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(MainActivity.this, "创建图片文件失败", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "图片为空，保存失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    /**
     * 初始化TCP连接的IP和端口
     */
    private void starGetPicture() {
        handler = new MyHandler();
        revImageThread = new RevImageThread(ip_address, port_number, handler);
        thread = new Thread(revImageThread);
        thread.start();
    }

    /**
     * 更改TCP连接的IP和端口
     */
    private void changeGetPicture() {
        if (thread != null) {
            thread.interrupt();
        }
        if (revImageThread != null) {
            if (revImageThread.s != null) {
                try {
                    revImageThread.s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        revImageThread = new RevImageThread(ip_address, port_number, handler);
        thread = new Thread(revImageThread);
        thread.start();
    }

    /**
     * 停止连接
     */
    private void stopConnect() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        if (revImageThread != null) {
            try {
                if (revImageThread.s != null){
                    revImageThread.s.close();
                }
                if (revImageThread.s_send != null){
                    revImageThread.s_send.close();
                }
                revImageThread = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        createDir();
    }

    /**
     * 创建保存图片的文件夹
     */
    private void createDir() {
        if (FileOperation.createDir(FileOperation.getSaveDir())) {
//            Log.d(TAG, "根目录文件夹创建成功");
        } else {
//            Log.d(TAG, "根目录文件夹创建失败");
        }
    }


    /**
     * 菜单初始化
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = new MenuInflater(this);//实例MenuInflater对象
        inflater.inflate(R.menu.toolsmenu, menu);//解析菜单文件
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 设置菜单点击事件监听
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.check_photo:
                Intent intent = new Intent(MainActivity.this, ShowImageActivity.class);
                startActivity(intent);
                break;
            case R.id.set:
                break;
            case R.id.stop_connect:
                stopConnect();
                break;
        }
        return true;
    }

    /**
     * 控件初始化
     */
    private void initWidgets() {
//        image = (ImageView) findViewById(R.id.imageView1);
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_port = (EditText) findViewById(R.id.et_port);

        bt_connect = (Button) findViewById(R.id.bt_connect);
        bt_takePicture = (Button) findViewById(R.id.bt_takePicture);
        bt_send = (Button) findViewById(R.id.bt_send);
        MyButtonListener myButtonListener = new MyButtonListener();
        bt_connect.setOnClickListener(myButtonListener);
        bt_takePicture.setOnClickListener(myButtonListener);
        bt_send.setOnClickListener(myButtonListener);

        brl = (BorderRelativeLayout) findViewById(R.id.brl);
        graphicsView = new GraphicsView(this);
        brl.addView(graphicsView);

        tv_magnification_times = (TextView) findViewById(R.id.tv_magnification_times);
        vsb = (VerticalSeekBar) findViewById(R.id.vsb);
        vsb.setOnSeekBarChangeListener(new MySeekBarListener());

        sp_cam_para1 = (Spinner) findViewById(R.id.sp_camera_parameter1);
        sp_cam_para2 = (Spinner) findViewById(R.id.sp_camera_parameter2);
        sp_cam_para3 = (Spinner) findViewById(R.id.sp_camera_parameter3);
        sp_cam_para4 = (Spinner) findViewById(R.id.sp_camera_parameter4);

        initSpinners();
    }

    /**
     * spinner控件初始化
     */
    private void initSpinners() {
        //从xml文件获取spinner中显示的条目内容
        String[] sp_1_items = getResources().getStringArray(R.array.Auto_Exposure);
        String[] sp_2_items = getResources().getStringArray(R.array.brightness);
        String[] sp_3_items = getResources().getStringArray(R.array.contrast);
        String[] sp_4_items = getResources().getStringArray(R.array.resolution_ratio);

        //为spinner设置适配器和监听器
        ArrayAdapter<String> sp_1_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sp_1_items);
        sp_1_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp_cam_para1.setAdapter(sp_1_adapter);
        sp_cam_para1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this,  "select sp_1-->"+position, Toast.LENGTH_SHORT).show();
                camera_parameter[0] = (short) position;
                if (revImageThread != null){
                    revImageThread.setSendData(camera_parameter);
//                    revImageThread.setIsSend(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> sp_2_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sp_2_items);
        sp_2_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp_cam_para2.setAdapter(sp_2_adapter);
        sp_cam_para2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "select sp_2-->"+position, Toast.LENGTH_SHORT).show();
                camera_parameter[1] = (short) position;
//                camera_parameter[1] = (short) (position - 2);
                if (revImageThread != null){
                    revImageThread.setSendData(camera_parameter);
//                    revImageThread.setIsSend(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> sp_3_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sp_3_items);
        sp_3_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp_cam_para3.setAdapter(sp_3_adapter);
        sp_cam_para3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this,  "select sp_3-->"+position, Toast.LENGTH_SHORT).show();
//                camera_parameter[2] = (short) position;
                switch (position){
                    case 0:
                        camera_parameter[2] = (short) 4;
                        break;
                    case 1:
                        camera_parameter[2] = (short) 3;
                        break;
                    case 2:
                        camera_parameter[2] = (short) 0;
                        break;
                    case 3:
                        camera_parameter[2] = (short) 1;
                        break;
                    case 4:
                        camera_parameter[2] = (short) 2;
                        break;
                }
                if (revImageThread != null){
                    revImageThread.setSendData(camera_parameter);
//                    revImageThread.setIsSend(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> sp_4_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sp_4_items);
        sp_4_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp_cam_para4.setAdapter(sp_4_adapter);
        sp_cam_para4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this,  "select sp_4-->"+position, Toast.LENGTH_SHORT).show();
                camera_parameter[3] = (short) position;
                if (revImageThread != null){
                    revImageThread.setSendData(camera_parameter);
//                    revImageThread.setIsSend(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        if (thread != null) {
            thread.interrupt();
        }
        if (revImageThread != null) {
            if (revImageThread.s != null) {
                try {
                    revImageThread.s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onDestroy();
    }
}
