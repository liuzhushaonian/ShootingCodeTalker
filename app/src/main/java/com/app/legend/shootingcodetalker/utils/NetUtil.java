package com.app.legend.shootingcodetalker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.app.legend.shootingcodetalker.bean.SubFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetUtil {

    public static int getAPNType(){
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) ShootingApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr==null){
            return netType;
        }

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        if(networkInfo==null){
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType==ConnectivityManager.TYPE_MOBILE){

            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet")){
                netType = 0;
            }
            else{
                netType = 1;
            }
        } else if(nType== ConnectivityManager.TYPE_WIFI){//重点获取WiFi以及无网络状态
            netType = 2;
        }
        return netType;
    }


}
