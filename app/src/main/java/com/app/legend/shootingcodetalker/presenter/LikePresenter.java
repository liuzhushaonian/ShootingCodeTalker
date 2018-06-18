package com.app.legend.shootingcodetalker.presenter;

import com.app.legend.shootingcodetalker.bean.Result;
import com.app.legend.shootingcodetalker.interfaces.ILikeActivity;
import com.app.legend.shootingcodetalker.utils.DatabaseUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LikePresenter extends BasePresenter<ILikeActivity> {

    private ILikeActivity activity;

    public LikePresenter(ILikeActivity activity) {
        attachView(activity);

        this.activity=getView();
    }

    public void getData(){

        Observable
                .create((ObservableOnSubscribe<List<Result>>) e -> {
                    List<Result> resultList= DatabaseUtil.getDefault().getLinkList();

                    e.onNext(resultList);
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Result>>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(List<Result> results) {
                        activity.setData(results);
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
