package org.zarroboogs.weibo.bean.hack.like;

/**
 * Created by wangdiyuan on 15-9-30.
 */
public class UnLike {
    private int ok;
    private String msg;
    private UnLikeData data;

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UnLikeData getData() {
        return data;
    }

    public void setData(UnLikeData data) {
        this.data = data;
    }
}
