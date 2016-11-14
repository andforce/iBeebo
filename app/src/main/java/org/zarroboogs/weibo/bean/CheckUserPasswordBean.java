package org.zarroboogs.weibo.bean;

public class CheckUserPasswordBean {
    private long retcode;
    private String msg;

    public long getCode() {
        return retcode;
    }

    public void setCode(long code) {
        this.retcode = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return "100000".equals(retcode);
    }
}
