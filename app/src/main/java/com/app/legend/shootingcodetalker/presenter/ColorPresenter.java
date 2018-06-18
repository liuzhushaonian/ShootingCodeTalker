package com.app.legend.shootingcodetalker.presenter;


import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.bean.Color;
import com.app.legend.shootingcodetalker.interfaces.IColorPresenter;
import com.app.legend.shootingcodetalker.utils.ShootingApp;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by legend on 2018/2/23.
 */

public class ColorPresenter extends BasePresenter<IColorPresenter>{

    private IColorPresenter activity;

    public ColorPresenter(IColorPresenter activity) {
        attachView(activity);

        this.activity=getView();
    }

    public void getColorData(){

        Observable
                .create((ObservableOnSubscribe<List<Color>>) e -> {
                    List<Color> colors=getColorList();
                    e.onNext(colors);
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Color>>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(List<Color> colors) {
                        setData(colors);
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

    private void setData(List<Color> colors){
        activity.setData(colors);
    }

    /**
     * 获取颜色并封装
     * @return
     */
    private List<Color> getColorList(){

        List<Color> colors=new ArrayList<>();

        Resources resources= ShootingApp.getContext().getResources();

        XmlResourceParser xmlResourceParser=resources.getXml(R.xml.colors);

        try {
            while (xmlResourceParser.getEventType()!=XmlResourceParser.END_DOCUMENT){

                if (xmlResourceParser.getEventType()== XmlResourceParser.START_TAG){

                    String name=xmlResourceParser.getName();
                    if (name.equals("color")){

                        String n=xmlResourceParser.getAttributeValue(1);
                        int id= Integer.parseInt(xmlResourceParser.getAttributeValue(0));
                        String c=xmlResourceParser.nextText();

                        int use=0;

                        int color_int= android.graphics.Color.parseColor(c);

                        Color color=new Color();
                        color.setColor(color_int);
                        color.setName(n);
                        color.setId(id);
                        color.setIs_use(use);

                        colors.add(color);
                    }
                }

                xmlResourceParser.next();

            }



        } catch (Exception e) {
            e.printStackTrace();
        }


        return colors;
    }
}
