package com.app.legend.shootingcodetalker.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.app.legend.shootingcodetalker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动返回操作
 * Created by legend on 2017/11/23.
 */

public class SlideHelper implements Application.ActivityLifecycleCallbacks {

    private Activity activity;

    private float defaultSpan;

    private ViewGroup viewGroup;

    private ViewManager viewManager;

    private static SlideHelper slideHelper;

    private View shadowView;

    private VelocityTracker velocityTracker;

    public boolean isScroll = false;

    private static int OPEN = 1000;

    private static int CLOSE = 2000;

    private List<Activity> activityList;//用于管理可以滑动的Activity

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    private int getWidth() {
        int width = this.activity.getResources().getDisplayMetrics().widthPixels;

        return width;

    }


    public static SlideHelper getInstance() {
        return slideHelper;
    }


    /**
     * 设置
     *
     * @param activity
     */
    public void setSlideActivity(Activity activity) {

        this.activityList.add(activity);

        this.activity = activity;

        this.defaultSpan = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16,
                activity.getResources().getDisplayMetrics());

        this.viewGroup = (ViewGroup) this.activity.getWindow().getDecorView();

        SlideParentFrameLayout parent=new SlideParentFrameLayout(activity);

        View view=viewGroup.getChildAt(0);//获取viewgroup里的第一个view

        viewGroup.removeView(view);//移除它

        parent.addView(view,0);//放置在自己写的parent里

        viewGroup.addView(parent,0);//将parent放入到这个viewgroup里

        slideView(parent.getChildAt(0),parent);//设置可滑动view

    }

    //交给manager处理显示view
    private void addToManager() {
        this.viewManager.addViewAtContent(this.activity);
        if (!isScroll) {
            addShadow();
        }
//        addShadow();
    }

    private SlideHelper(Application application) {
        application.registerActivityLifecycleCallbacks(this);
        if (this.viewManager == null) {
            this.viewManager = ViewManager.getInstance();
            velocityTracker = VelocityTracker.obtain();
            this.activityList = new ArrayList<>();
        }
    }

    private void slideView(View view, ViewGroup parent) {

        if (view == null||parent==null) {
            Log.w("waning!slideView-->>>", " the view is null!");
            return;
        }
        slideViewByHelper(view,parent);
    }


    /**
     * 滑动view
     *
     * @param view 传入需要滑动的view
     * @param parent 传入父布局，重写其touch事件
     */
    private void slideViewByHelper(final View view, ViewGroup parent) {

        if (parent==null||view==null){
            return;
        }

        parent.setOnTouchListener(new View.OnTouchListener() {

            float dx, rx;
            float dy = 0f;

            boolean con = false;


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                velocityTracker.addMovement(event);

                float speed = -1.0f;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dx = event.getRawX();

                        if (dx <= defaultSpan) {

                            addToManager();

                            con = true;

                            return true;
                        }

                    case MotionEvent.ACTION_MOVE:

                        if (dx <= defaultSpan && con) {

                            rx = event.getRawX() - dx;

                            if (rx < 0) {
                                rx = 0;
                            }

                            view.scrollTo((int) -rx, 0);

                            if (isScroll) {
                                viewManager.changeViewLocation((int) rx);

                            } else {
                                changeAlpha((int) rx);
                            }

                            return true;

                        }

                    case MotionEvent.ACTION_UP:

                        if (con) {

                            velocityTracker.computeCurrentVelocity(1000);

                            speed = velocityTracker.getXVelocity();

                            float endX = event.getRawX();

                            float end = view.getLeft();


                            if (isHalfScreen(endX) || speed > 500) {
                                //滑动距离过半或滑动速度超过限定值，向右滑动并退出当前Activity
                                autoScrollToRight(view, endX);

                            } else {
                                //滑动距离没过半且速度达不到要求
                                autoScrollToLeft(view, endX);

                            }

                            con = false;//抬起手后更改变量，避免二次重复触摸

                            return true;
                        }
                }

                return false;
            }
        });
    }

    private boolean scroll = true;

    /**
     * 抬起手后自动滑向右端（关闭Activity操作）
     */
    private void autoScrollToRight(final View view, final float currentX) {

        scroll = true;

        final int sp = getWidth() / 100;

        new Thread() {
            int remain = (int) (getWidth() - currentX);

            int current = (int) currentX;

            @Override
            public void run() {
//                super.run();
                try {
                    while (scroll) {

                        sleep(1);//睡眠。保证速度不会太快导致内存溢出
                        remain -= sp;
                        current += sp;
                        ViewInfo info = new ViewInfo(view, current, sp, remain);
                        openHandler.obtainMessage(10, info).sendToTarget();

                        if (remain < -sp * 3) {
                            scroll = false;
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }


    /**
     * 平滑移动与最后退出，设置在主线程
     */
    private Handler openHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {


            ViewInfo info = (ViewInfo) msg.obj;

            int sp = info.getSp();

            int distance = info.getCurrent();

            View view = info.getView();
            view.scrollBy(-sp, 0);

            //阴影也要随之改变
            if (!isScroll) {
                changeAlpha(distance);
            } else {
                viewManager.changeViewLocation(distance);

            }

            //判断退出，一定要在主线程内。
            if (info.getRemain() < -sp * 3) {
                close();
            }


        }
    };


    /**
     * 抬起手后自动滑向左端（还原Activity操作）
     * 1、将底部view还原至上一个Activity
     * 2、好像也没什么了
     */
    private void autoScrollToLeft(final View view, final float currentX) {

        scroll = true;

        final int sp = getWidth() / 100;

        new Thread() {

            int remain = (int) (getWidth() - currentX);

            int current = (int) currentX;

            @Override
            public void run() {
//               super.run();
                try {

                    Log.d("masg-->>", currentX + "");
                    while (scroll) {
                        sleep(1);

                        current -= sp;
                        remain += sp;
                        ViewInfo info = new ViewInfo(view, current, sp, remain);

                        closeHandler.obtainMessage(20, info).sendToTarget();

                        if (current < sp) {
                            scroll = false;
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private Handler closeHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {


            ViewInfo info = (ViewInfo) msg.obj;
            int sp = info.getSp();
            View view = info.getView();
            int current = info.getCurrent();

            view.scrollBy(sp, 0);

            if (!isScroll) {
                changeAlpha(current);
            }

            if (current < sp) {
                view.scrollTo(0, 0);
                resetView(CLOSE);

            }

        }
    };


    private void recycleVelocity() {

        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
    }

    /**
     * 结束Activity
     */
    private void close() {

        if (this.activity != null) {
            resetView(OPEN);

            this.activity.finish();

            this.activity.overridePendingTransition(0, R.anim.fade);

            scroll = false;

            resetActivity(this.activity);

        }

    }

    /**
     * 重置底部view
     */
    private void resetView(int type) {
        switch (type) {
            case 1000:
                this.viewManager.resetView(this.activity);

                break;
            case 2000:
                this.viewManager.closeResetView(this.activity);
                break;
        }

    }

    /**
     * 判断是否超过屏幕二分之一
     *
     * @param ex
     * @return
     */
    private boolean isHalfScreen(float ex) {
        boolean isHalf = false;

        float screenWidth = this.activity.getResources().getDisplayMetrics().widthPixels;

        if (ex >= screenWidth / 2) {
            isHalf = true;
        }

        return isHalf;
    }


    /**
     * 添加阴影
     */
    private void addShadow() {

        this.shadowView = new View(this.activity);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        this.shadowView.setLayoutParams(layoutParams);

        this.shadowView.setBackgroundColor(Color.parseColor("#c8000000"));

        ViewGroup viewGroup = (ViewGroup) this.activity.getWindow().getDecorView();

        ViewGroup frame= (ViewGroup) viewGroup.getChildAt(0);

        frame.addView(this.shadowView, 1);
    }

    /**
     * 改变透明度
     *
     * @param distance
     */
    private void changeAlpha(int distance) {

        if (this.shadowView == null) {
            return;
        }

        int width = this.activity.getResources().getDisplayMetrics().widthPixels;

        int dis = width / 200;

        int space = (width - distance);//平均分为200份

        //???? --怎么写来着？

        int speed = space / dis;

        //从200开始的透明度，可调
        if (speed > 200) {
            speed = 200;
        }

        //防止小于0
        if (speed<0){
            speed=0;
        }
//        if (speed < 16) {
//            speed = 16;
//        }


        String dex = Integer.toHexString(speed);

        //更改算法，实现真正透明
        if (dex.length()==1){

            dex="0"+dex;
        }

        String alpha = "#" + dex + "000000";

        this.shadowView.setBackgroundColor(Color.parseColor(alpha));

        ViewGroup.LayoutParams params=this.shadowView.getLayoutParams();

        params.width=distance;

        this.shadowView.setLayoutParams(params);

    }


    /**
     * 在application里进行注册
     *
     * @param application
     */
    public static void setApplication(Application application) {
        if (slideHelper == null) {
            slideHelper = new SlideHelper(application);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        this.viewManager.add(activity);

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        this.viewManager.remove(activity);

        Log.d("destroy->>>>", "" + activity.toString());
    }

    /**
     * 移除Activity后，需要将上一个Activity作为持有Activity进行操作
     *
     * @param activity
     */
    public void resetActivity(Activity activity) {

        this.activityList.remove(activity);
        if (this.activityList.size() == 0) {
            return;
        }

        this.activity = activityList.get(activityList.size() - 1);

    }


    /**
     * 管理类
     * 管理Activity的数组，添加，删除，恢复持有Activity，以及重置view与动画效果
     */
    private static class ViewManager {

        private static List<Activity> activities;

        private static ViewManager viewManager;

        private View preView;

        private ViewManager() {
        }

        //初始化
        public static ViewManager getInstance() {

            if (activities == null) {

                activities = new ArrayList<>();
            }

            if (viewManager == null) {


                viewManager = new ViewManager();
            }

            return viewManager;
        }

        //添加
        public void add(Activity activity) {
            if (activity == null) {
                return;
            }

            activities.add(activity);

//            Log.d("add-size-->>>",activities.size()+"");

        }

        //移除与恢复持有
        public void remove(Activity activity) {
            if (activity == null) {
                return;
            }
            activities.remove(activity);
//            Log.d("remove-size-->>>",activities.size()+"");

            //恢复持有
//            slideHelper.resetActivity(activities.get(activities.size()-1));


        }

        /**
         * 传入当前Activity，绘制下一层的Activity
         *
         * @param activity 当前Activity
         */
        public void addViewAtContent(Activity activity) {
            Activity previousActivity = null;

            //判断null以及是否唯一，如是则返回不做处理
            if (go() || activity == null) {
                return;
            }

            for (int i = activities.size(); i >= 0; i--) {

                if (activity == activities.get(i - 1)) {
                    previousActivity = activities.get(i - 2);
                    break;
                }

            }

            if (previousActivity == null) {

                Log.w("waning!", "the activity is not in the list!");

                return;
            }

            //获取上一个Activity的界面
            ViewGroup viewGroup1 = (ViewGroup) previousActivity.getWindow().getDecorView();

            this.preView = viewGroup1.getChildAt(0);

            viewGroup1.removeView(this.preView);//移除


            //获取当前Activity最底部ViewGroup
            ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();

            ViewGroup frame= (ViewGroup) viewGroup.getChildAt(0);

            frame.addView(this.preView, 0);//

        }

        /**
         * 重置Activity界面，避免关闭Activity后使得界面消失
         * 带动画效果，为向右滑到尽头后使Activity退出
         * @param activity 传入当前Activity
         */
        public void resetView(Activity activity) {
            if (activity == null || go()) {
                Log.d("waning!", "the activities is null or size is 0");
                return;
            }

            Activity previousActivity = null;

            for (int i = activities.size(); i >= 0; i--) {

                if (activity == activities.get(i - 1)) {
                    previousActivity = activities.get(i - 2);
                    break;
                }

            }

            if (previousActivity == null) {
                return;
            }

            ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();

            ViewGroup frame= (ViewGroup) viewGroup.getChildAt(0);

            View view = frame.getChildAt(0);

            ViewGroup previousViewGroup = (ViewGroup) previousActivity.getWindow().getDecorView();

            //动画，代替下面三行代码
            setTransition(view, frame, previousViewGroup);

//            frame.removeView(view);
//
//            previousViewGroup.addView(view);
//
//            if (slideHelper.isScroll) {
//                view.scrollTo(0, 0);//摆正位置
//            } else {
//                frame.removeViewAt(0);//移除阴影
//            }


        }

        /**
         * 手势未成功关闭Activity时，恢复上一个Activity的界面，避免用户按返回键后，上一个界面是空白的
         * 原理与重置view差不多，只是没有了动画效果，但是一定要摆正上一个view的位置，或是清除阴影
         *
         * @param activity 当前所持Activity
         */
        public void closeResetView(Activity activity) {
            if (activity == null || go()) {
                Log.d("waning!", "the activities is null or size is 0");
                return;
            }

            Activity previousActivity = null;

            for (int i = activities.size(); i >= 0; i--) {

                if (activity == activities.get(i - 1)) {
                    previousActivity = activities.get(i - 2);
                    break;
                }

            }

            if (previousActivity == null) {
                return;
            }

            ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();//获取底部最外层

            ViewGroup frame= (ViewGroup) viewGroup.getChildAt(0);//获取自定义的外层

            View view = frame.getChildAt(0);//获取上一个界面的view

            ViewGroup previousViewGroup = (ViewGroup) previousActivity.getWindow().getDecorView();//获取上一个界面的外层

//            setTransition(view,viewGroup,previousViewGroup);

            frame.removeView(view);//移除上一个界面的view

            previousViewGroup.addView(view, 0);//添加回上一个界面

            if (slideHelper.isScroll) {
                view.scrollTo(0, 0);//摆正位置
            } else {
                frame.removeViewAt(0);//移除阴影
            }

        }

        //判断队列是否为空以及其长度
        private boolean go() {

            return activities == null || activities.size() == 0 | activities.size() == 1;
        }


        /**
         * 改变底下view的位置，随着滑动而滑动
         *
         * @param space
         */
        public void changeViewLocation(int space) {

            if (this.preView == null) {
                return;
            }
            if (space > slideHelper.getWidth()) {
                space = slideHelper.getWidth();
            }

            int width = this.preView.getResources().getDisplayMetrics().widthPixels - space;

            this.preView.scrollTo((int) (width * 0.5), 0);

        }

        /**
         * 移除view动画
         *
         * @param view       需要移除的view
         * @param viewGroup1 从viewGroup1移除
         * @param viewGroup2 添加到viewGroup2
         */
        private void setTransition(final View view, final ViewGroup viewGroup1, final ViewGroup viewGroup2) {

            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 1.0f).setDuration(200);



            //重点！！动画结束后立刻将view移除并添加到上一个Activity里，保证无缝跳转
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    viewGroup1.removeView(view);
                    if (slideHelper.isScroll) {
                        viewGroup1.removeViewAt(0);
                    }

                    addView(view, viewGroup2);

                }
            });

            animator.start();
        }

        /**
         * 添加view
         *
         * @param view
         * @param viewGroup
         */
        private void addView(View view, ViewGroup viewGroup) {

//            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 1.0f).setDuration(100);
//
//
//            animator.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
////                    super.onAnimationStart(animation);

                    viewGroup.addView(view, 0);

                    //摆正view的位置
                    if (slideHelper.isScroll) {
                        view.scrollTo(0, 0);
                    }
//
//                }
//            });
//
//
//            animator.start();



        }

    }

    static class ViewInfo {

        View view;
        int current = 0;
        int sp = 0;
        int remain = 0;

        public ViewInfo(View view, int current, int sp, int remain) {
            this.view = view;
            this.current = current;
            this.sp = sp;
            this.remain = remain;
        }

        public View getView() {
            return view;
        }

        public int getCurrent() {
            return current;
        }

        public int getSp() {
            return sp;
        }

        public int getRemain() {
            return remain;
        }
    }


    interface HolderCallback{

       
    }


    public class SlideParentFrameLayout extends FrameLayout {

        private float defaultSpan;

        public SlideParentFrameLayout(Context context) {
            super(context);

            this.defaultSpan = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        }

        public SlideParentFrameLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.defaultSpan = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        }

        public SlideParentFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.defaultSpan = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        }


        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {

            float dx;

            switch (ev.getAction()){

                case MotionEvent.ACTION_DOWN:

                    dx = ev.getRawX();

                    if (dx <= defaultSpan) {
                        return true;
                    }
                    break;
            }

            return super.onInterceptTouchEvent(ev);
        }
    }



}

