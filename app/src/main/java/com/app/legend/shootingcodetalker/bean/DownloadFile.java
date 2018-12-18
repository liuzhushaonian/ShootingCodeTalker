package com.app.legend.shootingcodetalker.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 下载文件
 */
public class DownloadFile implements Parcelable{

    private String name;
    private String path;
    private String link;

    public DownloadFile(String name, String path, String link) {
        this.name = name;
        this.path = path;
        this.link = link;
    }

    public String getLink() {

        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    protected DownloadFile(Parcel in) {
        name = in.readString();
        path = in.readString();
    }

    public static final Creator<DownloadFile> CREATOR = new Creator<DownloadFile>() {
        @Override
        public DownloadFile createFromParcel(Parcel in) {
            return new DownloadFile(in);
        }

        @Override
        public DownloadFile[] newArray(int size) {
            return new DownloadFile[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



    public DownloadFile() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
    }
}
