package com.app.legend.shootingcodetalker.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 字幕文件，具体文件
 */
public class SubFile implements Parcelable {

    private String name;
    private String size;
    private int download=-1;
    private int id;
    private String net_id;
    private String net_part;
    private String net_name;
    private String downloadLink;

    protected SubFile(Parcel in) {
        name = in.readString();
        size = in.readString();
        download = in.readInt();
        id = in.readInt();
        net_id = in.readString();
        net_part = in.readString();
        net_name = in.readString();
        downloadLink = in.readString();
    }


    public static final Creator<SubFile> CREATOR = new Creator<SubFile>() {
        @Override
        public SubFile createFromParcel(Parcel in) {
            return new SubFile(in);
        }

        @Override
        public SubFile[] newArray(int size) {
            return new SubFile[size];
        }
    };

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }



    public String getNet_id() {
        return net_id;
    }

    public void setNet_id(String net_id) {
        this.net_id = net_id;
    }

    public String getNet_part() {
        return net_part;
    }

    public void setNet_part(String net_part) {
        this.net_part = net_part;
    }

    public String getNet_name() {
        return net_name;
    }

    public void setNet_name(String net_name) {
        this.net_name = net_name;
    }

    public SubFile() {
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(size);
        dest.writeInt(download);
        dest.writeInt(id);
        dest.writeString(net_id);
        dest.writeString(net_part);
        dest.writeString(net_name);
        dest.writeString(downloadLink);
    }
}
