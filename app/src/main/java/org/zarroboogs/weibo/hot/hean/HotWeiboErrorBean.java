package org.zarroboogs.weibo.hot.hean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotWeiboErrorBean implements Parcelable {

    // {"errmsg":"你的帐号验证失败，请重新登录。您的登录名是：86118@163.com","errno":-100,"errtype":"DEFAULT_ERROR","isblock":false}

    private String errmsg = "";
    private int erron = 0;
    private String errtype = "";
    private boolean isblock = false;

    // ================================================================ //

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErron() {
        return erron;
    }

    public void setErron(int erron) {
        this.erron = erron;
    }

    public String getErrtype() {
        return errtype;
    }

    public void setErrtype(String errtype) {
        this.errtype = errtype;
    }

    public boolean isIsblock() {
        return isblock;
    }

    public void setIsblock(boolean isblock) {
        this.isblock = isblock;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

    }

}
