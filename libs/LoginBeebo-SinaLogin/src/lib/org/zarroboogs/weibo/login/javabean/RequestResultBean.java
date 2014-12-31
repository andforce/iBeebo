
package lib.org.zarroboogs.weibo.login.javabean;

import java.io.Serializable;

public class RequestResultBean implements Serializable {

    private static final long serialVersionUID = 6577751371495335956L;
    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
