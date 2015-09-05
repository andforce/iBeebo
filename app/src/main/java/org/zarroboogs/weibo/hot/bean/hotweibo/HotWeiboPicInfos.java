package org.zarroboogs.weibo.hot.bean.hotweibo;

import android.os.Parcel;
import android.os.Parcelable;

public class HotWeiboPicInfos implements Parcelable{
	private HotWeiboPicInfo thumbnail;
	private HotWeiboPicInfo bmiddle;
	private HotWeiboPicInfo large;
	private HotWeiboPicInfo original;
	private HotWeiboPicInfo largest;
	private String object_id = "1042018:f6483b83663b9eba3fee6cc352c71e89";
	private String pic_id = "71b2cbb3gw1eqvzojm1woj20c85b7qgd";
	private int photo_tag = 0;

	public HotWeiboPicInfo getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(HotWeiboPicInfo thumbnail) {
		this.thumbnail = thumbnail;
	}

	public HotWeiboPicInfo getBmiddle() {
		return bmiddle;
	}

	public void setBmiddle(HotWeiboPicInfo bmiddle) {
		this.bmiddle = bmiddle;
	}

	public HotWeiboPicInfo getLarge() {
		return large;
	}

	public void setLarge(HotWeiboPicInfo large) {
		this.large = large;
	}

	public HotWeiboPicInfo getOriginal() {
		return original;
	}

	public void setOriginal(HotWeiboPicInfo original) {
		this.original = original;
	}

	public HotWeiboPicInfo getLargest() {
		return largest;
	}

	public void setLargest(HotWeiboPicInfo largest) {
		this.largest = largest;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public String getPic_id() {
		return pic_id;
	}

	public void setPic_id(String pic_id) {
		this.pic_id = pic_id;
	}

	public int getPhoto_tag() {
		return photo_tag;
	}

	public void setPhoto_tag(int photo_tag) {
		this.photo_tag = photo_tag;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeParcelable(thumbnail, flags);
		dest.writeParcelable(bmiddle, flags);
		dest.writeParcelable(large, flags);
		dest.writeParcelable(original, flags);
		dest.writeParcelable(largest, flags);
		dest.writeString(object_id);
		dest.writeString(pic_id);
		dest.writeInt(photo_tag);
	}
	
	 public static final Creator<HotWeiboPicInfos> CREATOR = new Creator<HotWeiboPicInfos>() {
         public HotWeiboPicInfos createFromParcel(Parcel in) {
        	 HotWeiboPicInfos picUrls = new HotWeiboPicInfos();
             picUrls.thumbnail= in.readParcelable(HotWeiboPicInfo.class.getClassLoader());
             picUrls.bmiddle= in.readParcelable(HotWeiboPicInfo.class.getClassLoader());
             picUrls.large= in.readParcelable(HotWeiboPicInfo.class.getClassLoader());
             picUrls.original= in.readParcelable(HotWeiboPicInfo.class.getClassLoader());
             picUrls.largest= in.readParcelable(HotWeiboPicInfo.class.getClassLoader());
             picUrls.object_id = in.readString();
             
             picUrls.pic_id = in.readString();
             picUrls.photo_tag = in.readInt();
             return picUrls;
         }

         public HotWeiboPicInfos[] newArray(int size) {
             return new HotWeiboPicInfos[size];
         }
     };
}
