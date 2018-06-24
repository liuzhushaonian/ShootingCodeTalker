package com.app.legend.shootingcodetalker.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.bean.Result;
import com.app.legend.shootingcodetalker.interfaces.ISearchResultActivity;
import com.app.legend.shootingcodetalker.utils.Conf;
import com.app.legend.shootingcodetalker.utils.DatabaseUtil;
import com.app.legend.shootingcodetalker.utils.FileUtil;
import com.app.legend.shootingcodetalker.utils.HtmlPagerUtil;
import com.app.legend.shootingcodetalker.utils.NetUtil;
import com.app.legend.shootingcodetalker.utils.ShootingApp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchResultPresenter extends BasePresenter<ISearchResultActivity> {

    private ISearchResultActivity activity;
    private int count=0;
    private int pagerCount=0;

    public SearchResultPresenter(ISearchResultActivity activity) {
        attachView(activity);

        this.activity = getView();
    }

    /**
     * 检查网络；判断是否应该继续搜索
     * @param keyword
     */
    public void getSearchResult(String keyword) {

        int netType= NetUtil.getAPNType();


        if (netType==-1){//无网络

            activity.showNoNet();//显示无网络
            activity.setNetStatus(true);

        }else if (netType!=2){//非WiFi网络,弹窗确认是否进行搜索

            activity.setNetStatus(false);
            activity.showLoadInfo();

            SharedPreferences sharedPreferences= ShootingApp.getContext().getSharedPreferences(Conf.SHARE_NAME, Context.MODE_PRIVATE);

            boolean onlyWifi=sharedPreferences.getBoolean(Conf.IF_NOT_WIFI_CAN_USE,true);

            if (onlyWifi) {

                activity.alertWindows(keyword);

            }else {

                getHtml(keyword);
            }


        }else {//WiFi网络

            activity.showLoadInfo();
            activity.setNetStatus(false);

            getHtml(keyword);

        }

    }


    private void getHtml(String keyword){

        String url = "http://assrt.net/sub/?searchword=" + keyword+"&no_redir=1";//加上&no_redir=1参数避免搜索结果仅为1条时自动跳转详情页面

        new Thread(){
            @Override
            public void run() {
                super.run();

                String pager= HtmlPagerUtil.getSearchPager(url);

                parsePagerCount(pager,keyword);//解析出所有的页数


            }
        }.start();

    }


    /**
     * 解析页面，获取数据
     * @param pager 解析的HTML页面
     * @param limit 解析的数量
     */
    private void parseHtml(String pager,int limit) {


        Observable
                .create((ObservableOnSubscribe<Result>) e -> {


                    Document document= Jsoup.parse(pager);

                    Element div=document.getElementById("resultsdiv");

                    if (div!=null){
                        Elements elements=div.select("div[onmouseover]");//获取所有class为subitem的div

                        int l=0;

                        if (limit>elements.size()){

                            l=elements.size();
                        }else {
                            l=limit;
                        }


                        for (int i=0;i<l;i++){

                            Element element=elements.get(i);

                            Result result=getResult(element);

                            if (result!=null){
                                e.onNext(result);
                            }
                        }

//                        for (Element element:elements){
//
//                            Result result=getResult(element);
//
//                            if (result!=null){
//                                e.onNext(result);
//                            }
//
//                        }

                        e.onComplete();
                    }
                    e.onComplete();

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Result result) {
                        activity.addResultItem(result);
                        count++;

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
     * 获取一个item
     * @param element
     * @return
     */
    private Result getResult(Element element){

        Result result=new Result();

        Element a=element.select("a.introtitle").first();

        String title=a.attr("title");

        String href=a.attr("href");

        result.setTitle(title);//设置标题

        href="http://assrt.net/"+href;

        result.setLink(href);//设置链接

        Element sublist=element.getElementById("sublist_div");

        Elements spans=sublist.getElementsByTag("span");

        for (Element span:spans){

            String info=span.text();
            if (info.startsWith("格式：")){

                result.setFormat(info);//设置格式
            }

            if (info.startsWith("语言：")){
                result.setLanguage(info);//设置语言
            }

        }

        Element download_a=element.getElementById("downsubbtn");

        String download_link=download_a.attr("onclick");

        download_link=download_link.substring(download_link.indexOf("'")+1,download_link.lastIndexOf("'"));

        download_link="http://assrt.net"+download_link;

        result.setDownload_link(download_link);

        int id= DatabaseUtil.getDefault().queryTable(download_link);

        result.setId(id);

        if (!DatabaseUtil.getDefault().isFileExists(download_link)){//从数据库中查询文件是否已被下载
            result.setDownload(1);
        }

        return result;

    }


    public void showDialog(String content, Activity activity){

        AlertDialog.Builder builder=new AlertDialog.Builder(activity);

        builder
                .setTitle(activity.getString(R.string.alert_title))
                .setMessage(activity.getString(R.string.alert_message))
                //
                .setNegativeButton(activity.getString(R.string.no),(dialog, which) -> {

                    builder.create().cancel();
                    this.activity.closeActivity();
                })
                .setPositiveButton(activity.getString(R.string.yes),(dialog, which) -> {

                    getHtml(content);//确认继续则进行正常搜索

                });

        builder.show();

    }

    /**
     * 获取更多的搜索结果
     * @param pagerCount 总页数
     * @param keyword 搜索关键字
     * @param firstPager 第一页的结果
     */
    private void getMoreResult(int pagerCount,String keyword,String firstPager){

        SharedPreferences sharedPreferences=ShootingApp.getContext().getSharedPreferences(Conf.SHARE_NAME,Context.MODE_PRIVATE);
        int cc=sharedPreferences.getInt(Conf.SEARCH_COUNT,15);

        String url = "http://assrt.net/sub/?searchword=" + keyword+"&no_redir=1";//加上&no_redir=1参数避免搜索结果仅为1条时自动跳转详情页面

        int p=cc/15;//获取需要解析的页数

        int y=cc%15;//获取余数

        if (p>pagerCount){//如果实际页数并没有那么多，则取实际的页数为准

            p=pagerCount;

            for (int o = 1; o <= p; o++) {

                String u = url + "&page=" + o;

                String html = HtmlPagerUtil.getSearchPager(u);

                parseHtml(html,15);

            }



        }else if (p<=0&&y<15){//避免误伤，比如有人调皮地改为比15还小的数字
            p=1;

//            String pager=HtmlPagerUtil.getSearchPager(url);

            parseHtml(firstPager,y);


        }else {//有多页数据,且有余数的存在


            for (int o = 1; o <= p; o++) {

                String u = url + "&page=" + o;

                String html = HtmlPagerUtil.getSearchPager(u);

                parseHtml(html,15);

            }

            if (y>0) {

                int s=p+1;
                String u1 = url + "&page=" +s;

                String html = HtmlPagerUtil.getSearchPager(u1);

                parseHtml(html,y);

            }

        }

    }

    /**
     * 仅仅解析页数，提供后续搜索更多的结果
     * @param pager
     */
    private void parsePagerCount(String pager,String keyword){

        Document document=Jsoup.parse(pager);

        Elements divs=document.getElementsByClass("pagelinkcard");



        if (divs.size()==0){//表示没有搜索到任何结果



            return;
        }

        Element div=divs.get(0);

        if (div!=null){

            Elements as=div.getElementsByTag("a");//索取所有的a标签

            Element a=as.last();//获取最后一个标签

            String count=a.text();

            count=count.substring(count.lastIndexOf("/")+1,count.length());

            getMoreResult(Integer.parseInt(count),keyword,pager);



        }

    }



}
