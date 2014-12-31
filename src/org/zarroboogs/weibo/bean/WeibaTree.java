
package org.zarroboogs.weibo.bean;

import java.util.ArrayList;
import java.util.List;

public class WeibaTree implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3149252918422515949L;
    private String code = "";
    private String text = "";
    private List<WeiboWeiba> data = new ArrayList<WeiboWeiba>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<WeiboWeiba> getData() {
        return data;
    }

    public void setData(List<WeiboWeiba> data) {
        this.data = data;
    }

    /*
     * "code": "1", "text": "热门", "data": [ { "text": "iPhone 6 Plus", "code": "5yiHuw" }, { "text":
     * "iPhone 6", "code": "3o33sO" }, { "text": "iPhone 5s", "code": "3G5oUM" }, { "text":
     * "iPad客户端", "code": "4ACxed" }, { "text": "魅族MX4", "code": "503Oti" }, { "text": "索尼Z3",
     * "code": "1JALDw" }, { "text": "三星GALAXY Note 4", "code": "584Wm0" }, { "text":
     * "三星 GALAXY S5", "code": "yrOPq" } ]
     */
}
