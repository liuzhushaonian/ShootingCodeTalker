package com.app.legend.shootingcodetalker.presenter;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.activity.MainActivity;
import com.app.legend.shootingcodetalker.interfaces.IMainActivity;

public class MainPresenter extends BasePresenter<IMainActivity>{

    private IMainActivity activity;


    public MainPresenter(IMainActivity activity) {

        attachView(activity);

        this.activity = getView();
    }

    public void query(String content){

        if (content==null){
            return;
        }

        if (content.isEmpty()){
            return;
        }

        if (!isViewAttached()){
            return;
        }

        this.activity.startActivity(content);


    }

    public void showAbout(Activity activity){

        String about=activity.getResources().getString(R.string.about_content);

        AlertDialog.Builder builder=new AlertDialog.Builder(activity);

        View view= LayoutInflater.from(activity).inflate(R.layout.about_content,null,false);

        TextView textView=view.findViewById(R.id.about_content);

        String versionName="";

        try {
            // ---get the package info---
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);

            versionName = pi.versionName;

        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }

        about=about+"当前版本："+versionName;


        textView.setText(about);

        builder.setView(view).setTitle("关于").setPositiveButton("确定",(dialog, which) -> {

            builder.create().cancel();

        }).show();



    }

}
