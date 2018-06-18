package com.app.legend.shootingcodetalker.presenter;

import com.app.legend.shootingcodetalker.bean.SubFile;
import com.app.legend.shootingcodetalker.interfaces.IEditActivity;
import com.app.legend.shootingcodetalker.utils.FileUtil;
import com.app.legend.shootingcodetalker.utils.HtmlPagerUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

        Observable
                .create((ObservableOnSubscribe<String>) e -> {

                    File file= FileUtil.getSubFile(subFile);

                    if (file==null||!file.exists()){
                        HtmlPagerUtil.download(subFile.getDownloadLink(),subFile.getName());
                        file=FileUtil.getSubFile(subFile);
                    }

                    StringBuilder stringBuilder=new StringBuilder();

                    if (file!=null) {
//                        FileReader reader = new FileReader(file);

                        BufferedReader reader = null;
                        try {


                            reader = new BufferedReader(new FileReader(file));
                            String tempString = null;
                            int line = 1;
                            // 一次读入一行，直到读入null为文件结束
                            while ((tempString = reader.readLine()) != null) {
                                // 显示行号
                                stringBuilder.append(tempString);
                                line++;
                            }
                            reader.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e1) {

                                    e1.printStackTrace();
                                }
                            }
                        }

                    }

                    e.onNext(stringBuilder.toString());
                    e.onComplete();

                })
                .subscribeOn(Schedulers.io())
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



}
