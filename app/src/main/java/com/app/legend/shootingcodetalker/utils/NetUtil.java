package com.app.legend.shootingcodetalker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
