
package org.zarroboogs.weibo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.zarroboogs.weibo.support.utils.ObjectToStringUtility;

public class GroupBean implements Parcelable {

    private String id;
    private String idstr;
    private String name;
    private int member_count;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(idstr);
        dest.writeString(name);
        dest.writeInt(member_count);
    }

    public static final Creator<GroupBean> CREATOR = new Creator<GroupBean>() {
        public GroupBean createFromParcel(Parcel in) {
            GroupBean groupBean = new GroupBean();
            groupBean.id = in.readString();
            groupBean.idstr = in.readString();
            groupBean.name = in.readString();
            groupBean.member_count = in.readInt();
            return groupBean;
        }

        public GroupBean[] newArray(int size) {
            return new GroupBean[size];
        }
    };

    public int getMember_count() {
        return member_count;
    }

    public void setMember_count(int member_count) {
        this.member_count = member_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
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
