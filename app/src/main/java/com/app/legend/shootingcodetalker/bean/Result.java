package com.app.legend.shootingcodetalker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Result implements Parcelable{

    private String title;//文件名


    private String language;//语言

    private String format;//格式

    private int download=-1;//是否已经下载

    private String download_link;//下载链接

    private List<String> file_list;//字幕列表

    private String link;//详情链接

    private int id=-1;

    public Result() {
    }

    protected Result(Parcel in) {
        title = in.readString();
        language = in.readString();
        format = in.readString();
        download = in.readInt();
        download_link = in.readString();
        file_list = in.createStringArrayList();
        link = in.readString();
        id = in.readInt();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    public String getDownload_link() {
        return download_link;
    }

    public void setDownload_link(String download_link) {
        this.download_link = download_link;
    }

    public List<String> getFile_list() {
        return file_list;
    }

    public void setFile_list(List<String> file_list) {
        this.file_list = file_list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(language);
        dest.writeString(format);
        dest.writeInt(download);
        dest.writeString(download_link);
        dest.writeStringList(file_list);
        dest.writeString(link);
        dest.writeInt(id);
    }
}
