package com.app.legend.shootingcodetalker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HtmlPagerUtil {


    private static final String PATH= Environment.getExternalStorageDirectory().
            getAbsolutePath();

    private static SharedPreferences sharedPreferences
            =ShootingApp.getContext()
            .getSharedPreferences(Conf.SHARE_NAME, Context.MODE_PRIVATE);

    public static String getSearchPager(String url){

        String pager="";


        if (url==null){
            return pager;
        }

        if (url.isEmpty()){
            return pager;
        }

        Connection connection=Jsoup.connect(url);

        try {
            Connection.Response response=connection.execute();
            pager=response.body();
        } catch (IOException e) {


            pager="";
        }

        return pager;
    }


    public static int download(String url,String title){

        int result=-1;

        //先判断网络状态

        int s=NetUtil.getAPNType();

        if (s==-1){
            return -100;
        }


        try {

            Request.Builder builder=new Request.Builder().url(url).method("GET",null);

            Request request=builder.build();

            File sdcard= ShootingApp.getContext().getExternalCacheDir();

            int cacheSize=100*1024*1024;

            OkHttpClient.Builder builder1= null;
            builder1 = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20,TimeUnit.SECONDS);

            if (sdcard!=null){
                builder1.cache(new Cache(sdcard.getAbsoluteFile(),cacheSize));
            }

            OkHttpClient okHttpClient=builder1.build();

            Call call=okHttpClient.newCall(request);

            Response response=call.execute();

            if (response.body()!=null) {
                byte[] bytes = response.body().bytes();

                String path = sharedPreferences.getString(Conf.DOWNLOAD_PATH_NAME, Conf.DEFAULT_DOWNLOAD_PATH);

                String format=url.substring(url.lastIndexOf("."),url.length());

                if (title.contains(".")){//如果title已经有格式，则将格式去掉

                    String s1=title.substring(title.lastIndexOf("."),title.length());

                    title=title.replace(s1,"");

                }

                File file = new File(PATH+path + title+format);


                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                FileOutputStream outputStream = new FileOutputStream(file);

                outputStream.write(bytes);

                outputStream.flush();

                outputStream.close();

                DatabaseUtil.getDefault().addDownload(title,file.getAbsolutePath(),url);//保存到出数据

                result = 1;
            }

        }catch (Exception e){
            result=-1;

        }

        return result;

    }
}
