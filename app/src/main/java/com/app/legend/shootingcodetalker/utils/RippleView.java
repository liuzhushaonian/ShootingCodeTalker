package com.app.legend.shootingcodetalker.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by legend on 2018/2/22.
 */

public class RippleView extends LinearLayout {

    private Paint paint;

    private volatile float radius=0;//半径

    private float cx=0;//横坐标

    private float cy=0;//纵坐标

    private int color;//颜色

    private boolean stop=true;

    private int limit=2000;

    private List<ColorBean> colorBeanList;


    public RippleView(Context context) {
        super(context);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        colorBeanList=new ArrayList<>();

    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint=new Paint();

        paint.setColor(this.color);

        paint.setStyle(Paint.Style.FILL);

        paint.setAntiAlias(true);

        canvas.drawCircle(cx,cy,radius,paint);

    }

    private void start(){

        new Thread(){
            @Override
            public void run() {
                super.run();

                while (!stop){

                    try {
                        sleep(10);

                        radius+=50;

                        handler.sendEmptyMessage(1);

                        if (radius>limit) {
                            stop = true;
                            //换肤操作

                            handler.sendEmptyMessage(2);

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }.start();

    }

    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    invalidate();
                    break;
                case 2:

                    setBackgroundColor(color);

                    continueRip();//执行下一个颜色的渲染

                    popBean();
                    break;
            }
        }
    };

    public void startRipper(float cx,float cy,int color,int limit){

        if (!stop){
            cacheRipEvent(cx,cy,color,limit);//添加到缓存
            return;
        }

        stop=false;
        this.radius=0;
        this.cx=cx;
        this.cy=cy;
        this.color=color;
        this.limit=limit;
        start();
    }

    /**
     * 设置最大限制，建议为手机屏幕
     * @param limit
     */
    public void setLimit(int limit) {

//        Log.d("limit--->>",limit+"");
        this.limit = limit;
    }

    /**
     * 用于缓存渲染事件
     */
    private void cacheRipEvent(float cx,float cy,int color,int limit){
        ColorBean bean=new ColorBean(cx,cy,color,limit);

        this.colorBeanList.add(bean);

    }

    /**
     * 继续执行事件
     */
    private void continueRip(){

        if (this.colorBeanList!=null){
            if (!colorBeanList.isEmpty()){

                ColorBean colorBean=colorBeanList.get(0);
                this.cx=colorBean.getCx();
                this.cy=colorBean.getCy();
                this.color=colorBean.getColor();
                this.limit=colorBean.getLimit();
                this.radius=0;
                stop=false;
                start();
            }
        }
    }

    private void popBean(){
        if (this.colorBeanList!=null&&!this.colorBeanList.isEmpty()){
            this.colorBeanList.remove(0);
        }
    }

    class ColorBean{

        private float cx,cy;
        private int color;
        private int limit;

        public ColorBean(float cx, float cy, int color, int limit) {

            this.cx = cx;
            this.cy = cy;
            this.color = color;
            this.limit = limit;
        }


        public float getCx() {
            return cx;
        }

        public float getCy() {
            return cy;
        }

        public int getColor() {
            return color;
        }

        public int getLimit() {
            return limit;
        }
    }



}
