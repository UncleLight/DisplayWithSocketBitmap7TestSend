package com.example.administrator.displaywithsocketbitmap7testsend;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileOperation {
    public static String SAVE_DIR = Environment.getExternalStorageDirectory().getPath() + "/profile";//使用内置SD卡，并设置目录

    private static final String TAG = "FileOperation";

    public static String getSaveDir() {
        return SAVE_DIR;
    }

    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        //此处因由JVM显示异常
        if (dir.exists()) {
            Log.d(TAG, "createDir: " + destDirName + "文件夹已经存在");
            return false;
        }

        if (destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {
            Log.d(TAG, "createDir: 创建目录 " + destDirName + "成功！");
            return true;
        } else {
            Log.d(TAG, "createDir:  创建目录 " + destDirName + "失败！");
            return false;
        }
    }

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        //此处因由JVM显示异常
//        if (file.exists()) {
//            Log.d(TAG, "creatFile: 创建文件 " + destFileName + "失败，文件已经存在");
//            return false;
//        }
//        if (destFileName.endsWith(File.separator)){
//            Log.d(TAG, "creatFile: 创建文件 " + destFileName + "失败，文件不能为目录");
//            return false;
//        }

        if (!file.getParentFile().exists()){
            Log.d(TAG, "createFile: 目标文件目录不存在，准备创建");
            if (!file.getParentFile().mkdirs()){
                Log.d(TAG, "createFile: 创建文件所在目录失败");
                return false;
            }
        }

        //创建目标文件
        try {
            if (file.createNewFile()){
                Log.d(TAG, "createFile: 创建文件 " + destFileName + "成功！");
                return true;
            }else {
                Log.d(TAG, "createFile: 创建文件 " + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "createFile: 创建文件 " + destFileName + "失败！异常 " + e.getMessage());
            return false;
        }
    }


}
