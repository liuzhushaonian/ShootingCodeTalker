package com.app.legend.shootingcodetalker.presenter;

import android.util.Log;
import android.widget.Toast;

import com.app.legend.shootingcodetalker.bean.Result;
import com.app.legend.shootingcodetalker.bean.SubFile;
import com.app.legend.shootingcodetalker.interfaces.ISubActivity;
import com.app.legend.shootingcodetalker.utils.DatabaseUtil;
import com.app.legend.shootingcodetalker.utils.FileUtil;
import com.app.legend.shootingcodetalker.utils.HtmlPagerUtil;
import com.app.legend.shootingcodetalker.utils.ShootingApp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SubPresenter extends BasePresenter<ISubActivity> {

    private ISubActivity activity;

    public SubPresenter(ISubActivity activity) {


        attachView(activity);

        this.activity=getView();
    }


    public void prepareGetData(Result result){

        getPager(result);
    }

    private void getPager(Result result){

        if (result==null){
            return;
        }


        Observable
                .create((ObservableOnSubscribe<SubFile>) e -> {

                    String url=result.getLink();

                    String pager= HtmlPagerUtil.getSearchPager(url);

                    Document document= Jsoup.parse(pager);

                    Element span=document.getElementById("detail-filelist");

                    Elements elements=span.getElementsByClass("waves-effect");

                    for (Element div:elements){

                        SubFile subFile=getSubFile(div);

                        if (subFile!=null){
                            e.onNext(subFile);
                        }

                    }

                    e.onComplete();



                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SubFile>() {

                    Disposable disposable;
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(SubFile subFile) {
                        activity.setData(subFile);
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

    private SubFile getSubFile(Element element){

        SubFile subFile=new SubFile();

        Element span_name=element.getElementById("filelist-name");

        String name=span_name.text();

        Element span_size=element.getElementById("filelist-size");

        String size=span_size.text();

        String download_info=element.attr("onclick");

        String reg="\"(.*?)\"";

        Pattern pattern=Pattern.compile(reg);

        Matcher matcher=pattern.matcher(download_info);


        while (matcher.find()){

            String s=matcher.group();

            s=s.replace("\"","");

            if (subFile.getNet_id()==null||subFile.getNet_id().isEmpty()){

                subFile.setNet_id(s);

            }else if (subFile.getNet_part()==null||subFile.getNet_part().isEmpty()){
                subFile.setNet_part(s);
            }else if (subFile.getNet_name()==null||subFile.getNet_name().isEmpty()){

                subFile.setNet_name(s);
            }

        }


        String download_link="http://assrt.net/download/"+subFile.getNet_id()+"/-/"+subFile.getNet_part()+"/"+subFile.getNet_name();

        subFile.setDownloadLink(download_link);

        subFile.setName(name);
        subFile.setSize(size);

        if (!DatabaseUtil.getDefault().isFileExists(download_link)){
            subFile.setDownload(1);
        }


        return subFile;

    }

    public void toggle(Result result){

        Observable
                .create((ObservableOnSubscribe<Integer>) e -> {

                    int id=result.getId();

                    int s=-1;

                    if (id>0){//有id，表示已经收藏，取消收藏

                        s= DatabaseUtil.getDefault().removeLike(result);

                    }else {//无id，表示未收藏，收藏到数据库

                        s=DatabaseUtil.getDefault().saveLike(result);
                    }

                    e.onNext(s);

                    e.onComplete();

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer==0){//没删除成功

                            Toast.makeText(ShootingApp.getContext(), "取消收藏失败", Toast.LENGTH_SHORT).show();

                        }else {
                            result.setId(integer);
                        }

                        activity.notifyChange();

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }
                });

    }

    /**
     * 打包下载
     * @param result 用文件下载
     */
    public void download(Result result){

        downloadAll(result);
    }

    private void downloadAll(Result result){

        Observable
                .create((ObservableOnSubscribe<Integer>) e -> {

                    int p=HtmlPagerUtil.download(result.getDownload_link(),result.getTitle());

                    e.onNext(p);
                    e.onComplete();

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(Integer integer) {

                        if (integer==-100){//无网络状态
                            Toast.makeText(ShootingApp.getContext(),"网络连接失败",Toast.LENGTH_SHORT).show();
                        }

                        activity.changeBtn(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if(!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                });

    }

    public void changeUI(Result result){

        if (!DatabaseUtil.getDefault().isFileExists(result.getDownload_link())){
            result.setDownload(1);
            activity.changeBtn(result.getDownload());
        }

    }


}
