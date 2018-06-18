package com.app.legend.shootingcodetalker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.app.legend.shootingcodetalker.bean.SubFile;

import java.io.File;

public class FileUtil {


    private static final String PATH= Environment.getExternalStorageDirectory().
            getAbsolutePath();

    private static SharedPreferences sharedPreferences
            =ShootingApp.getContext()
            .getSharedPreferences(Conf.SHARE_NAME, Context.MODE_PRIVATE);


    public static File getSubFile(SubFile subFile){

        String path=sharedPreferences.getString(Conf.DOWNLOAD_PATH_NAME,Conf.DEFAULT_DOWNLOAD_PATH);

        return new File(PATH+path+subFile.getName());

    }

    public static boolean isExists(String name){


        String path=sharedPreferences.getString(Conf.DOWNLOAD_PATH_NAME,Conf.DEFAULT_DOWNLOAD_PATH);

        File file=new File(PATH+path+name);

        return file.exists();

    }

}
