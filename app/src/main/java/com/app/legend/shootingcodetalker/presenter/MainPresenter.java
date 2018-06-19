package com.app.legend.shootingcodetalker.presenter;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.activity.MainActivity;
import com.app.legend.shootingcodetalker.interfaces.IMainActivity;
import com.app.legend.shootingcodetalker.utils.Conf;
import com.app.legend.shootingcodetalker.utils.ShootingApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    public void saveAndSetImage(Uri uri, ImageView imageView){

        Observable
                .create((ObservableOnSubscribe<Bitmap>) e -> {

                    Bitmap bitmap=getBitmap(uri);

                    if (bitmap!=null){

                        String path=ShootingApp.getContext().getFilesDir().getAbsolutePath();

                        File file=new File(path+"/"+ Conf.HEADER);

                        FileOutputStream outputStream=new FileOutputStream(file);

                        bitmap.compress(Bitmap.CompressFormat.WEBP,100,outputStream);//保存到本地

                        e.onNext(bitmap);

                    }

                    e.onComplete();

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                });

    }

    private Bitmap getBitmap(Uri uri){


        Bitmap bitmap=null;

        if (uri==null){
            return null;
        }

        try {
            bitmap= BitmapFactory.decodeStream(ShootingApp.getContext().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
