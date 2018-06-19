package com.app.legend.shootingcodetalker.utils;

import android.app.Application;
import android.content.Context;

public class ShootingApp extends Application {

    private static Context context;

    public static Context getContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SlideHelper.setApplication(this);
        context=getApplicationContext();
    }
}
