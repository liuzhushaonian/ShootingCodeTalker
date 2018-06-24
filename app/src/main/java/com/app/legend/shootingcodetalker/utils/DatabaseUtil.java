package com.app.legend.shootingcodetalker.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import com.app.legend.shootingcodetalker.bean.DownloadFile;
import com.app.legend.shootingcodetalker.bean.Result;
import com.app.legend.shootingcodetalker.bean.SubFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil extends SQLiteOpenHelper {

    private static volatile DatabaseUtil database;
    private static final String SHOOTING_DATABASE="Shooting";//数据库名称
    private static int VERSION=1;//数据库版本
    private SQLiteDatabase sqLiteDatabase;//数据库实例
    private static final String LIKE="LikeTable";
    private static final String ID="id";
    private static final String TITLE="title";
    private static final String DOWNLOAD_LINK="link";
    private static final String FORMAT="format";
    private static final String LANGUAGE="language";
    private static final String LINK="item_link";


    private static final String LIKE_TABLE="CREATE TABLE IF NOT EXISTS "+LIKE+"(" +
            ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            TITLE+" TEXT NOT NULL," +
            DOWNLOAD_LINK+" TEXT NOT NULL UNIQUE," +
            FORMAT+" TEXT NOT NULL," +
            LANGUAGE+" TEXT NOT NULL," +
            LINK+" TEXT NOT NULL" +
            ")";

    private static final String DOWNLOAD_TABLE="CREATE TABLE IF NOT EXISTS download (id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "path TEXT NOT NULL," +
            "download_link TEXT NOT NULL UNIQUE" +
            ")";

    private static final String FILE_TABLE="CREATE TABLE IF NOT EXISTS file (id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "size TEXT NOT NULL," +
            "download_link TEXT NOT NULL," +
            "result_id INTEGER NOT NULL UNIQUE" +
            ")";


    public static DatabaseUtil getDefault(){

        if (database == null) {
            synchronized (DatabaseUtil.class) {
                database = new DatabaseUtil(ShootingApp.getContext(), SHOOTING_DATABASE, null, VERSION);
            }
        }

        return database;
    }

    public DatabaseUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        sqLiteDatabase=getReadableDatabase();

    }

    public DatabaseUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(LIKE_TABLE);//创建默认表
        db.execSQL(DOWNLOAD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 存储喜欢的字幕
     * @param result
     * @return
     */
    public int saveLike(Result result){

        int re=-1;

        String sql="insert into "+LIKE+"("+TITLE+","+FORMAT+","+LANGUAGE+","+DOWNLOAD_LINK+","+LINK+") " +
                "values ('"+result.getTitle()+"','"+result.getFormat()+"','"+result.getLanguage()+"','"+result.getDownload_link()+"','" +
                result.getLink()+"'" +
                ")";

        String sql_get_id="select id from "+LIKE+" where "+DOWNLOAD_LINK+" = '"+result.getDownload_link()+"'";

        try {
            sqLiteDatabase.execSQL(sql);

            Cursor cursor=sqLiteDatabase.rawQuery(sql_get_id,null);

            if (cursor!=null) {
                if (cursor.moveToFirst()) {
                    re = cursor.getInt(cursor.getColumnIndex("id"));
                }

                cursor.close();
            }


        }catch (Exception e){
            re=-1;
        }

        return re;

    }

    /**
     * 移除收藏
     * @param result
     * @return
     */
    public int removeLike(Result result){

        int id=result.getId();
        if (id<0){
            return 0;
        }

        String sql="delete from "+LIKE+" where id="+id;

        try {

            sqLiteDatabase.execSQL(sql);

            return -1;

        }catch (Exception e){

            return 0;

        }

    }

    /**
     * 查询数据库内是否已经收藏该item，如果是，则返回id
     * @param link 下载链接
     * @return 返回id
     */
    public int queryTable(String link){

        int result=-1;

        String sql="select id from "+LIKE+" where "+DOWNLOAD_LINK+"='"+link+"' limit 1";

        try {

            Cursor cursor=sqLiteDatabase.rawQuery(sql,null);
            if (cursor!=null){

                if (cursor.moveToFirst()){

                    result=cursor.getInt(cursor.getColumnIndex("id"));

                }
                cursor.close();
            }

        }catch (Exception e){
            result=-1;
        }

        return result;

    }

    /**
     * 获取全部收藏
     * @return 返回收藏列表
     *
     */
    public List<Result> getLinkList(){

        String sql="select * from "+LIKE;

        List<Result> resultList=new ArrayList<>();

        Cursor cursor=sqLiteDatabase.rawQuery(sql,null);

        if (cursor!=null){

            if (cursor.moveToFirst()){
                do {

                    String title=cursor.getString(cursor.getColumnIndex(TITLE));
                    String download_link=cursor.getString(cursor.getColumnIndex(DOWNLOAD_LINK));
                    int id=cursor.getInt(cursor.getColumnIndex(ID));
                    String format=cursor.getString(cursor.getColumnIndex(FORMAT));
                    String language=cursor.getString(cursor.getColumnIndex(LANGUAGE));
                    String link=cursor.getString(cursor.getColumnIndex(LINK));



                    Result result=new Result();
                    result.setDownload_link(download_link);
                    result.setId(id);
                    result.setLanguage(language);
                    result.setLink(link);
                    result.setTitle(title);
                    result.setFormat(format);

                    if (!isFileExists(download_link)){
                        result.setDownload(1);
                    }

                    resultList.add(result);

                }while (cursor.moveToNext());
            }

            cursor.close();
        }

        return resultList;

    }


    /**
     * 保存下载的文件
     * @param name 文件名
     * @param path 文件路径
     * @param download_link 下载链接
     */
    public void addDownload(String name,String path,String download_link) throws Exception{

        String sql="insert into download (name,path,download_link) values ('"+name+"','"+path+"','"+download_link+"')";

        sqLiteDatabase.execSQL(sql);

    }

    /**
     * 获取全部已下载的文件
     * @return
     */
    public List<DownloadFile> getDownloadFileList(){

        String sql="select * from download";

        Cursor cursor=sqLiteDatabase.rawQuery(sql,null);

        List<DownloadFile> downloadFileList=new ArrayList<>();

        if (cursor!=null){


            if (cursor.moveToFirst()){

                do {

                    String name=cursor.getString(cursor.getColumnIndex("name"));
                    String path=cursor.getString(cursor.getColumnIndex("path"));
                    String link=cursor.getString(cursor.getColumnIndex("download_link"));

                    DownloadFile file=new DownloadFile(name,path,link);
                    downloadFileList.add(file);

                }while (cursor.moveToNext());

            }

            cursor.close();
        }

        Log.d("data-------->>",downloadFileList.size()+"");

        return downloadFileList;

    }

    /**
     * 以downloadLink是否存在判断文件是否被下载
     * 同时判断文件是否还存在，如果不存在，则删除数据库数据
     * @param downloadLink
     * @return
     */
    public boolean isFileExists(String downloadLink){

        String sql="select download_link,path from download where download_link = '"+downloadLink+"' limit 1";

        String link="";

        try {

            Cursor cursor=sqLiteDatabase.rawQuery(sql,null);

            if (cursor!=null){

                if (cursor.moveToFirst()){
                    link=cursor.getString(cursor.getColumnIndex("download_link"));
                }

                cursor.close();
            }

        }catch (Exception e){
            link="";
        }

        return link.isEmpty();

    }

    /**
     * 删除已下载的文件
     * @param link 下载链接
     * @return 返回数字表示是否删除成功
     */
    public int deleteFile(String link){

        String sql="delete from download where download_link = '"+link+"'";

        int result=-1;

        try {

            sqLiteDatabase.execSQL(sql);

            result=1;
        }catch (Exception e){
            result=-1;

        }
        return result;

    }


}
