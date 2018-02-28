package com.example.administrator.displaywithsocketbitmap7testsend;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime {
    //声明日期格式化操作对象，直接对new Date()进行实例化
    private SimpleDateFormat sdf = null;

    /**
     * 得到完整的日期，格式为：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public String getDate(){
        this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return this.sdf.format(new Date());
    }

    /**
     * 得到完整的日期，格式为：yyyy年MM月dd日HH时mm分ss秒
     * @return
     */
    public String getDateComplete(){
        this.sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        return this.sdf.format(new Date());
    }

    /**
     * 得到完整的日期，格式为：yyyyMMddHHmmss
     * @return
     */
    public String getTimeStamp(){
        this.sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return this.sdf.format(new Date());
    }

}
