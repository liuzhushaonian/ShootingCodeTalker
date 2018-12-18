package com.app.legend.shootingcodetalker.presenter;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.app.legend.shootingcodetalker.bean.SubFile;
import com.app.legend.shootingcodetalker.interfaces.IEditActivity;
import com.app.legend.shootingcodetalker.utils.FileUtil;
import com.app.legend.shootingcodetalker.utils.HtmlPagerUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EditPresenter extends BasePresenter<IEditActivity> {

    private IEditActivity activity;


    public EditPresenter(IEditActivity activity) {

        attachView(activity);

        this.activity=getView();

    }

    public void preview(SubFile subFile){

        parseSubFile(subFile);

    }

    private void parseSubFile(SubFile subFile){

        Log.d("link---->>>",subFile.getDownloadLink()+"");

        Observable
                .create((ObservableOnSubscribe<String>) e -> {

                    Log.d("link---->>>",subFile.getDownloadLink());

                    URL url=new URL(subFile.getDownloadLink());
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setConnectTimeout(60 * 1000);
//                    conn.setReadTimeout(60 * 1000);

                    InputStream inputStream=url.openStream();

                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    StringBuffer sb = new StringBuffer();
                    while ((line = in.readLine()) != null) {
//                        sb.append(line);

                        e.onNext(line);
                    }

//                    e.onNext(sb.toString());
                    e.onComplete();


                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(String content) {
                        activity.showPreview(content);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                });

    }


    public void download(SubFile subFile, Activity activity){

        new Thread(){
            @Override
            public void run() {
                super.run();

                //下载
                HtmlPagerUtil.download(subFile.getDownloadLink(),subFile.getName());

                info(activity);

            }
        }.start();

    }

    private void info(Activity activity){

        activity.runOnUiThread(() -> {

            Toast.makeText(activity, "下载完成", Toast.LENGTH_SHORT).show();

        });

    }

}
