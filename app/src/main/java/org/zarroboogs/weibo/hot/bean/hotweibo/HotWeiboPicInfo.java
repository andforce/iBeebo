package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;

import android.os.Parcel;
import android.os.Parcelable;


public class HotWeiboPicInfo implements Parcelable {

    private String url;
    private long cutType;
    private String width;
    private String type;
    private String height;


    public HotWeiboPicInfo() {

    }


    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCutType() {
        return this.cutType;
    }

    public void setCutType(long cutType) {
        this.cutType = cutType;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(url);
        dest.writeLong(cutType);
        dest.writeString(width);
        dest.writeString(type);
        dest.writeString(height);
    }


    public static final Creator<HotWeiboPicInfo> CREATOR = new Creator<HotWeiboPicInfo>() {
        public HotWeiboPicInfo createFromParcel(Parcel in) {
            HotWeiboPicInfo picUrls = new HotWeiboPicInfo();
            picUrls.url = in.readString();
            picUrls.cutType = in.readLong();
            picUrls.width = in.readString();
            picUrls.type = in.readString();
            picUrls.height = in.readString();
            return picUrls;
        }

        public HotWeiboPicInfo[] newArray(int size) {
            return new HotWeiboPicInfo[size];
        }
    };

}
