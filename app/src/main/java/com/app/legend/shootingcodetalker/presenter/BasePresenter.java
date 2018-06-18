package com.app.legend.shootingcodetalker.presenter;

import android.view.View;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class BasePresenter<T> {


    protected Reference<T> activityRes;


    /**
     * 建立关联
     * @param view 传入activity或fragment实例
     */
    public void attachView(T view){

        activityRes=new WeakReference<T>(view);

    }

    protected T getView(){

        return activityRes.get();
    }

    public boolean isViewAttached(){

        return activityRes!=null&&activityRes.get()!=null;

    }

    public void detachView(){

        if (activityRes!=null){

            activityRes.clear();
            activityRes=null;
        }
    }
}
