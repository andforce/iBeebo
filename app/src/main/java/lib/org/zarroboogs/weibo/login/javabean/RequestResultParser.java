
package lib.org.zarroboogs.weibo.login.javabean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.google.gson.Gson;

import android.text.TextUtils;

public class RequestResultParser {

    private boolean mIsLogin = false;
    private String mErrorReason = "";
    private String mResponseString;
    private String mLocationReplace;

    public String getLocationReplace() {
        return mLocationReplace;
    }

    public void setLocationReplace(String mLocationReplace) {
        this.mLocationReplace = mLocationReplace;
    }

    private Gson mGson;

    public RequestResultParser() {
        mGson = new Gson();
    }

    public <T extends Object> T parse(byte[] response, Class<T> bean) {
        if (response == null) {
            return null;
        }
        String responseString = new String(response);
        if (TextUtils.isEmpty(responseString)) {
            return null;
        }
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson.fromJson(responseString, bean);
    }


    public boolean isLogin() {
        return mIsLogin;
    }

    public String getErrorReason() {
        return mErrorReason;
    }

    public String getResponseString() {
        return this.mResponseString;
    }

    public String getUserPageUrl() {
        if (mIsLogin) {
            String regx = "url=";
            String tmp = getResponseString().split(regx)[1];
            String url = tmp.split("retcode=0")[0] + "retcode=0";
            try {
                url = URLDecoder.decode(url, "GBK");
                url = URLDecoder.decode(url, "GBK");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            return url;
        }
        return null;
    }

}
