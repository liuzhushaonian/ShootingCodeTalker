package com.app.legend.shootingcodetalker.presenter;

import android.util.Log;

import com.app.legend.shootingcodetalker.bean.DownloadFile;
import com.app.legend.shootingcodetalker.interfaces.IDownloadActivity;
import com.app.legend.shootingcodetalker.utils.DatabaseUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DownloadPresenter extends BasePresenter<IDownloadActivity> {

    private IDownloadActivity activity;

    public DownloadPresenter(IDownloadActivity activity) {
        attachView(activity);

        this.activity=getView();
    }

    public void getData(){

        Log.d("presenter----->>","get!");

        Observable
                .create((ObservableOnSubscribe<List<DownloadFile>>) e -> {

                    List<DownloadFile> downloadFileList= DatabaseUtil.getDefault().getDownloadFileList();

                    e.onNext(downloadFileList);
                    e.onComplete();

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DownloadFile>>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(List<DownloadFile> downloadFiles) {
                        activity.setData(downloadFiles);
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

}
