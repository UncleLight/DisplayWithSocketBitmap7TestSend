package com.example.administrator.displaywithsocketbitmap7testsend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class RevImageThread implements Runnable {
    private static final String TAG = "RevImageThread";

    public Socket s;
    private DataOutputStream data_out = null;
    public Socket s_send;

    //连接地址
    public String ip;
    public int port;

    //向UI线程发送消息
    private Handler handler;

    private Bitmap bitmap;
    private static final int COMPLETED = 0x111;

    private short[] data = {0, 0, 4, 0};
    private boolean isSend = true;


    public RevImageThread(Handler handler) {
        this("192.168.1.100", 20000, handler);
    }

    public RevImageThread(String ip, int port, Handler handler) {
        this.ip = ip;
        this.port = port;
        this.handler = handler;
    }

    public void setIsSend(boolean send) {
        isSend = send;
    }

    public void setSendData(short[] shorts) {
        data = shorts;
        isSend = true;
    }

    public void run() {
        InputStream ins = null;
        try {
            s = new Socket(ip, port);
            s_send = new Socket(ip, port);
//            Log.d(TAG, "run: s state-->" + s.isConnected());
//            Log.d(TAG, "run: s_send state-->" + s_send.isConnected());
            ins = s.getInputStream();
//            data_out = new DataOutputStream(s.getOutputStream());
            data_out = new DataOutputStream(s_send.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] buffer;
        int len = 0;

        //循环，不断接收发送来的数据
        while (true) {
            try {
                //发送标志位消息
                if (isSend) {
//                    send();
//                    isSend = false;
                }


                if (ins != null) {
                    buffer = new byte[1024 * 40];
                    int nIdx = 0;
                    int nTotalLen = buffer.length;
                    int nReadLen = 0;

                    while (nIdx < nTotalLen) {
                        try {
                            nReadLen = ins.read(buffer, nIdx, nTotalLen - nIdx);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (nReadLen > 0) {
                            nIdx = nIdx + nReadLen;
                        } else {
                            break;
                        }
                    }
//                    Log.d(TAG, "on decode");
                    bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                }

                //非空时才更新显示
                if (bitmap != null) {
                    Message msg = handler.obtainMessage();
                    msg.what = COMPLETED;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send() {
        try {

            int i = 0;
            while (i < data.length) {
                data_out.write(data[i]);
//                data_out.writeShort(data[i]);
//                Log.d(TAG, "send: data-->" + data[i]);
                i++;
            }
            data_out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
