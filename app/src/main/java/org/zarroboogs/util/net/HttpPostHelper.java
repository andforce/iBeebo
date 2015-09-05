
package org.zarroboogs.util.net;

import java.io.Serializable;

public class HttpPostHelper {

    public static final String NOT_LOGIN = "\u672a\u767b\u5f55";

    public static class WeiBoPostResult implements Serializable {
        private static final long serialVersionUID = 2670736249286930507L;
        private int code = 0;
        private String msg = "";

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

    }






}
