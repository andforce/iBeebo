
package org.zarroboogs.weibo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.zarroboogs.weibo.support.utils.ObjectToStringUtility;

public class TagBean implements Parcelable {

    private int id;
    private String name;
    private String weight;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(weight);
    }

    public static final Creator<TagBean> CREATOR = new Creator<TagBean>() {
        public TagBean createFromParcel(Parcel in) {
            TagBean tagBean = new TagBean();
            tagBean.id = in.readInt();
            tagBean.name = in.readString();
            tagBean.weight = in.readString();
            return tagBean;
        }

        public TagBean[] newArray(int size) {
            return new TagBean[size];
        }
    };

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ObjectToStringUtility.toString(this);
    }
}
