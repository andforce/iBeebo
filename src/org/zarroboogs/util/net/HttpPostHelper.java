
package org.zarroboogs.util.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import lib.org.zarroboogs.utils.net.BroserContent;
import lib.org.zarroboogs.utils.net.HttpFactory;
import lib.org.zarroboogs.weibo.login.utils.Constaces;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.zarroboogs.utils.CookieUtils;
import org.zarroboogs.utils.CourseHeader;
import org.zarroboogs.utils.CourseUrls;
import org.zarroboogs.utils.PatternUtils;
import org.zarroboogs.weibo.bean.UploadPicResult;

import com.google.gson.Gson;

import android.util.Log;

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
