package com.example.administrator.displaywithsocketbitmap7testsend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import static android.graphics.Bitmap.createScaledBitmap;


public class GraphicsView extends View {

    private static final String TAG = "GraphicsView";

    public final static int NONE = 0;
    /**
     * 按下
     */
    public final static int PRESS = 1;
    /**
     * 左移
     */
    public final static int LEFT = 2;
    /**
     * 右移
     */
    public final static int RIGHT = 3;
    /**
     * 上移
     */
    public final static int UP = 4;
    /**
     * 下移
     */
    public final static int DOWN = 5;
    /**
     * 长按
     */
    public final static int LONG_PRESS = 6;
    /**
     * 放大
     */
    public final static int AMPLIFICATION = 7;
    /**
     * 缩小
     */
    public final static int NARROW = 8;

    private static final float MIN_MOVE_DISTANCE = 15;

    private int mTouchMode;
    private float mStartX, mStartY;
    private long mStartTime;
    private float mFingerSpace;

    private GestureDetector mGestureDetector;
    private GestureDetectorCompat mDetector;

    //默认绘图参数
    private int step = 100;//步进
    private int width = 600;//绘图区域长
    private int height = 640;//绘图区域宽

    Bitmap bm;

    private Paint mPaint;//将绘图和绘制网格的Paint统一起来，测试用

    public boolean sourceFromBitmap = false;

    public int zoomLevel = 0;//缩放等级

    private TextView tv_zoomLevel;

    public GraphicsView(Context context) {
        super(context);
        initScreenParameter();
        initGestureDetector();
        tv_zoomLevel = MainActivity.tv_magnification_times;
    }

    public GraphicsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initScreenParameter();
        initGestureDetector();
        tv_zoomLevel = MainActivity.tv_magnification_times;
    }

    public GraphicsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initScreenParameter();
        initGestureDetector();
        tv_zoomLevel = MainActivity.tv_magnification_times;
    }

    //    public GraphicsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initScreenParameter();
//        GestureDetector
//    }
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new MyGestureListener());
    }

    interface Listener {
        public void getMode(int mode);
    }

    Listener mListener;

    public Bitmap getBitmap() {
        return bm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("debug", "GraphicsView Drawing coordinate");
        super.onDraw(canvas);
        canvas.clipRect(0, 0, width, height);
        canvas.drawColor(Color.WHITE);
        canvas.save();//保存画布
        //初始化画笔
        mPaint = new Paint();

        drawPaint(canvas);//绘制图片
        drawCoordinate(canvas);//绘制网格

//        canvas.restore();//恢复画布（坐标设置）
    }

    /**
     * 绘制坐标
     *
     * @param canvas
     */
    private void drawCoordinate(Canvas canvas) {
        //网格画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        //设置路径效果
        PathEffect effects = new DashPathEffect(new float[]{width / 20, width / 20}, 1);
        mPaint.setPathEffect(effects);
        //设置图形重叠式时的处理方式
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        mPaint.setStrokeWidth((float) 1.5);

        Path path = new Path();
        //绘制网格和坐标轴
        for (int i = 0; i * step < height; i++) {
//            canvas.drawLine(0, step * i, width, step * i, mPaint);//无效
            path.moveTo(0, step * i);
            path.lineTo(width, step * i);
            canvas.drawPath(path, mPaint);
        }
    }

    /**
     * 绘制图片
     *
     * @param canvas
     */
    private void drawPaint(Canvas canvas) {
        mPaint = new Paint();
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));//设置图形重叠式时的处理方式

        //缩放图片
        if (bm != null) {
            bm = createScaledBitmap(bm, width, height, false);//将图片缩放至指定大小，适应显示
            canvas.drawBitmap(bm, 0, 0, mPaint);
        }
    }


    /**
     * 以Bitmap方式设置要显示的图片
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.bm = bitmap;
        sourceFromBitmap = true;
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 初始化绘图参数
     */
    private void initScreenParameter() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        //Displaymetrics是取得手机屏幕大小的关键类
        DisplayMetrics outMetrice = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrice);
        width = (int) (outMetrice.widthPixels * 0.8);
        height = (int) (outMetrice.heightPixels * 0.6);
        step = (int) (width / 5.5);
    }

    /**
     * 返回屏幕宽度与高度，整形
     *
     * @return
     */
    public int[] getScreenSize() {
        //获取WindowManager
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        //Displaymetrics是取得手机屏幕大小的关键类
        DisplayMetrics outMetrice = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrice);
        int[] size = {outMetrice.widthPixels, outMetrice.heightPixels};
        return size;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mGestureDetector.onTouchEvent(event);
                mTouchMode = PRESS;
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                mStartTime = System.currentTimeMillis();
                mFingerSpace = 0;
                if (mListener != null) {
                    mListener.getMode(mTouchMode);
                }
//                Log.d(TAG, "onTouchEvent: 手势下滑");
                break;
            case MotionEvent.ACTION_MOVE:
                /**
                 * 此处判断是否为双指操作
                 * 并做出相应操作
                 */
                getGestures(event);
                if (mListener != null) {
                    mListener.getMode(mTouchMode);
                }
//                Log.d(TAG, "onTouchEvent: 手势移动");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode = NONE;
                if (mListener != null) {
                    mListener.getMode(mTouchMode);
                }
//                Log.d(TAG, "onTouchEvent: 手势向上");
                break;

            case MotionEvent.ACTION_CANCEL:
                mTouchMode = NONE;
                if (mListener != null) {
                    mListener.getMode(mTouchMode);
                }
//                Log.d(TAG, "onTouchEvent: 手势取消");
                break;
        }
        return true;
    }


    private void getGestures(MotionEvent event) {
        // 2个手指以上
        if (event.getPointerCount() >= 2) {
            //先判断是否是缩放
            float x1 = event.getX(0) - event.getX(1);
            float y1 = event.getY(0) - event.getY(1);
            //第一个手指和第二个手指的间距
            float value = (float) Math.sqrt(x1 * x1 + y1 * y1);
            if (mFingerSpace == 0) {
                mFingerSpace = value;
            } else {
                //一段时间内，如果两值间的变化不大，则认为是移动，否则是；加时间限制是为了防止反应过快
                if (System.currentTimeMillis() - mStartTime > 100) {
                    //移动后两指的间距的变化值
                    float fingerDistanceChange = value - mFingerSpace;
                    //同时手指间的间距变化大于最小距离时就认为是缩放
                    if (Math.abs(fingerDistanceChange) > MIN_MOVE_DISTANCE) {
                        float scale = value / mFingerSpace;
                        if (scale > 1) {
                            mTouchMode = AMPLIFICATION;
                            /*
                            读到放大手势
                             */
                            zoomLevelIncrease();
                            Log.d(TAG, "getGestures: 手势二点放大");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mTouchMode = NARROW;
                            /*
                            读到缩小手势
                             */
                            zoomLevelDecrease();
                            Log.d(TAG, "getGestures: 手势二点缩小");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mStartTime = System.currentTimeMillis();
                        mStartX = event.getX();
                        mStartY = event.getY();
                        mFingerSpace = value;
                        //当第一个手指200毫秒内移动MIN_MOVE_DISTANCE倍距离，同时手指间的间距变化小于4倍距离时为移动
                    }
                }
                return;
            }
        }

//        //判断是否长按,x方向移动小于最小距离同时y方向小于最小距离，
//        float offsetX = Math.abs(event.getX() - mStartX);
//        float offsetY = Math.abs(event.getY() - event.getY());
//        long time = System.currentTimeMillis() - mStartTime;
//        if (time > 1500 && Math.abs(offsetX) < MIN_MOVE_DISTANCE && Math.abs(offsetY) < MIN_MOVE_DISTANCE) {
//            mTouchMode = LONG_PRESS;
//            Log.d(TAG, "getGestures: 手势长按");
//            return;
//        }
//        //移动时区分上下还是左右
//        if (System.currentTimeMillis() - mStartTime > 50) {
//            float xDistance = event.getX() - mStartX;
//            float yDistance = event.getY() - mStartY;
//            if (Math.abs(xDistance) > Math.abs(yDistance)) {
//                if (xDistance > 5) {
//                    mTouchMode = RIGHT;
//                    Log.d(TAG, "getGestures: 手势右移");
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else if (xDistance < -5) {
//                    mTouchMode = LEFT;
//                    Log.d(TAG, "getGestures: 手势左移");
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else {
//                if (yDistance > 5) {
//                    mTouchMode = DOWN;
//                    Log.d(TAG, "getGestures: 手势下移");
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else if (yDistance < -5) {
//                    mTouchMode = UP;
//                    Log.d(TAG, "getGestures: 手势上移");
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            mStartTime = System.currentTimeMillis();
//            mStartX = event.getX();
//            mStartY = event.getY();
//        }
    }

    /**
     * 增加图片放大等级并更新显示
     */
    private void zoomLevelIncrease() {
        if (zoomLevel < MainActivity.zoom.length - 1) {
            zoomLevel++;
            MainActivity.zoomLevel = zoomLevel;
            MainActivity.tv_magnification_times.setText("x" + MainActivity.zoom[zoomLevel]);
//            invalidate();
        }
    }

    /**
     * 减小图片放大等级并更新显示
     */
    private void zoomLevelDecrease() {
        if (zoomLevel > 0) {
            zoomLevel--;
            MainActivity.zoomLevel = zoomLevel;
            MainActivity.tv_magnification_times.setText("x" + MainActivity.zoom[zoomLevel]);
//            invalidate();
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (mTouchMode == AMPLIFICATION || mTouchMode == NARROW) {
                return;
            }
            mTouchMode = LONG_PRESS;
            if (mListener != null) {
                mListener.getMode(mTouchMode);
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

}
