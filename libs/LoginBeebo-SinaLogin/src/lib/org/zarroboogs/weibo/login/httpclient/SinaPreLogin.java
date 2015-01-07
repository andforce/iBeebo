
package lib.org.zarroboogs.weibo.login.httpclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.org.zarroboogs.utils.net.BroserContent;
import lib.org.zarroboogs.utils.net.HttpFactory;
import lib.org.zarroboogs.weibo.login.javabean.HasloginBean;
import lib.org.zarroboogs.weibo.login.javabean.RequestResultParser;
import lib.org.zarroboogs.weibo.login.javabean.PreLoginResult;
import lib.org.zarroboogs.weibo.login.utils.Constaces;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;

import android.util.Log;

import com.google.gson.Gson;

public class SinaPreLogin {

    private BroserContent mBroserContent = BroserContent.getInstance();

    public PreLoginResult preLogin(String userName, String passWord) {
        String encodeName = encodeAccount(userName);
        HttpClient client = mBroserContent.getHttpClient();
        // 获得rsaPubkey,rsakv,servertime等参数值
        PreLoginResult params = preLogin(encodeName, client);
        return params;
    }

    public RequestResultParser doLoginAfterPreLogin(String userName, String passWord, String door, PreLoginResult params) {
        HttpClient client = mBroserContent.getHttpClient();

        HttpPost post = doLoginAfterLogIn(userName, passWord, door, params);
        HttpResponse response;
        RequestResultParser helper = null;
        try {
            response = client.execute(post);
            helper = new RequestResultParser(response.getEntity());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return helper;
    }

    public HasloginBean loginUserPage(RequestResultParser helper) {
        HttpClient client = mBroserContent.getHttpClient();

        HttpGet userPageGet = HttpFactory.createHttpGet(helper.getUserPageUrl(), null);
        HttpResponse userPageResponse;
        String responseString = null;
        try {
            userPageResponse = client.execute(userPageGet);
            responseString = EntityUtils.toString(userPageResponse.getEntity(), "GBK");
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String loginResponse = getJsonString(responseString) + "}";
        System.out.println("json: " + loginResponse);

        HasloginBean hasloginBean = gson.fromJson(loginResponse, HasloginBean.class);
        return hasloginBean;
    }

    public HttpPost doLoginAfterLogIn(String encodeUserName, String encondePassWord, String door, PreLoginResult params) {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Host", "login.sina.com.cn"));
        headers.add(new BasicHeader("Cache-Control", "max-age=0"));
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        headers.add(new BasicHeader("Origin", "http://widget.weibo.com"));
        headers.add(new BasicHeader("User-Agent", Constaces.User_Agent));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Referer",
                "http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("entry", "weibo"));
        nvps.add(new BasicNameValuePair("gateway", "1"));
        nvps.add(new BasicNameValuePair("from", ""));
        nvps.add(new BasicNameValuePair("savestate", "7"));
        nvps.add(new BasicNameValuePair("useticket", "1"));
        nvps.add(new BasicNameValuePair("pagerefer", ""));
        nvps.add(new BasicNameValuePair("pcid", params.getPcid()));
        if (!TextUtils.isEmpty(door)) {
            nvps.add(new BasicNameValuePair("door", door));
        }
        nvps.add(new BasicNameValuePair("vsnf", "1"));
        nvps.add(new BasicNameValuePair("su", encodeUserName));
        nvps.add(new BasicNameValuePair("service", "miniblog"));
        nvps.add(new BasicNameValuePair("servertime", params.getServertime() + ""));
        nvps.add(new BasicNameValuePair("nonce", params.getNonce()));
        nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
        nvps.add(new BasicNameValuePair("rsakv", params.getRsakv()));
        nvps.add(new BasicNameValuePair("sp", encondePassWord));
        nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
        nvps.add(new BasicNameValuePair("prelt", "166"));
        nvps.add(new BasicNameValuePair("url",
                "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
        nvps.add(new BasicNameValuePair("returntype", "META"));

        HttpPost post = HttpFactory.createHttpPost(Constaces.LOGIN_FIRST_URL, headers, nvps);
        return post;
    }

    public HttpEntity afterPreLoginEntity(String encodeUserName, String encondePassWord, String door, PreLoginResult params) {

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("entry", "weibo"));
        nvps.add(new BasicNameValuePair("gateway", "1"));
        nvps.add(new BasicNameValuePair("from", ""));
        nvps.add(new BasicNameValuePair("savestate", "7"));
        nvps.add(new BasicNameValuePair("useticket", "1"));
        nvps.add(new BasicNameValuePair("pagerefer", ""));
        nvps.add(new BasicNameValuePair("pcid", params.getPcid()));
        if (!TextUtils.isEmpty(door)) {
            nvps.add(new BasicNameValuePair("door", door));
        }
        nvps.add(new BasicNameValuePair("vsnf", "1"));
        nvps.add(new BasicNameValuePair("su", encodeUserName));
        nvps.add(new BasicNameValuePair("service", "miniblog"));
        nvps.add(new BasicNameValuePair("servertime", params.getServertime() + ""));
        nvps.add(new BasicNameValuePair("nonce", params.getNonce()));
        nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
        nvps.add(new BasicNameValuePair("rsakv", params.getRsakv()));
        nvps.add(new BasicNameValuePair("sp", encondePassWord));
        nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
        nvps.add(new BasicNameValuePair("prelt", "166"));
        nvps.add(new BasicNameValuePair("url",
                "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
        nvps.add(new BasicNameValuePair("returntype", "META"));

        UrlEncodedFormEntity mEncodedFormEntity = null;
        try {
            mEncodedFormEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return mEncodedFormEntity;
    }



    public boolean sendWeibo(BroserContent broserContent, String url, String app_src, String content, String cookie,
            String pid) {
        CloseableHttpClient httpClient = broserContent.getHttpClient();
        // http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH
        // http://widget.weibo.com/public/aj_addMblog.php

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "*/*"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Host", "widget.weibo.com"));
        headers.add(new BasicHeader("Origin", "http://widget.weibo.com"));
        headers.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
        headers.add(new BasicHeader("Referer", "http://widget.weibo.com/topics/topic_vote_base.php?" +
                "tag=Weibo&app_src=" + app_src + "&isshowright=0&language=zh_cn"));
        headers.add(new BasicHeader("User-Agent", Constaces.User_Agent));

        List<NameValuePair> loginParams = new ArrayList<NameValuePair>();
        loginParams.add(new BasicNameValuePair("app_src", app_src));
        loginParams.add(new BasicNameValuePair("content", content));
        if (!TextUtils.isEmpty(pid)) {
            loginParams.add(new BasicNameValuePair("pic_id", pid));
        }
        loginParams.add(new BasicNameValuePair("return_type", "2"));
        loginParams.add(new BasicNameValuePair("refer", ""));
        loginParams.add(new BasicNameValuePair("vsrc", "base_topic"));
        loginParams.add(new BasicNameValuePair("wsrc", "app_topic_base"));
        loginParams.add(new BasicNameValuePair("ext", "login=>1;url=>"));
        loginParams.add(new BasicNameValuePair("html_type", "2"));
        loginParams.add(new BasicNameValuePair("_t", "0"));
        loginParams.add(new BasicNameValuePair("Cookie", cookie));

        Log.d("sendWeibo-PIC", "[" + pid + "]");

        HttpPost logInPost = HttpFactory.createHttpPost(url, headers, loginParams);

        // logInPost.addHeader("Cookie", cookie);
        CloseableHttpResponse logInResponse = null;
        String allResponse = "";
        boolean isSuccess = false;
        try {
            logInResponse = httpClient.execute(logInPost);
            HttpEntity mEntity = logInResponse.getEntity();
            if (mEntity != null) {
                InputStream in;
                try {
                    in = mEntity.getContent();
                    String str = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    while ((str = br.readLine()) != null) {
                        allResponse += str;
                    }

                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("Error IllegalStateException----");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("Error IOException");
                }
            }

            Log.d("sendWeibo", "" + allResponse);
            Gson gson = new Gson();
            WeiBoPostResult result = gson.fromJson(allResponse, WeiBoPostResult.class);
            if (logInResponse.getStatusLine().getStatusCode() == 200) {
                if (result != null && result.getCode() == 100000) {
                    isSuccess = true;
                } else {
                    isSuccess = false;
                }
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isSuccess = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isSuccess = false;
        } finally {
            try {
                if (logInResponse != null) {
                    logInResponse.close();
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return isSuccess;
    }

    public static class WeiBoPostResult implements java.io.Serializable {
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

    public String buildPreLoginUrl(String su, String ssoLoginjs, String time) {
        // entry:weibo
        // callback:sinaSSOController.preloginCallBack
        // su:MTMyMDAyMjM1MiU0MHFxLmNvbQ==
        // rsakt:mod
        // checkpin:1
        // client:ssologin.js(v1.4.18)
        // _:1419780466785

        String url = "http://login.sina.com.cn/sso/prelogin.php?";
        url = url + "entry=weibo&";
        url = url + "callback=sinaSSOController.preloginCallBack&";
        url = url + "su=" + su + "&";
        url = url + "rsakt=mod&";
        url = url + "checkpin=0&";
        url = url + "client=" + ssoLoginjs + "&";
        // time = new Date().getTime();
        url = url + "_=" + time;
        return url;

    }

    public Header[] preloginHeaders() {
        Header[] getHeader = {
                new BasicHeader("Host", "login.sina.com.cn"),
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("User-Agent", Constaces.User_Agent),
                new BasicHeader("Referer", "http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH"),
                new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"),
                new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"),
        };
        return getHeader;
    }

    public Header[] afterPreLoginHeaders() {

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Host", "login.sina.com.cn"));
        headers.add(new BasicHeader("Cache-Control", "max-age=0"));
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        headers.add(new BasicHeader("Origin", "http://widget.weibo.com"));
        headers.add(new BasicHeader("User-Agent", Constaces.User_Agent));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Referer",
                "http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));
        Header[] resultHeaders = new Header[headers.size()];
        for (int i = 0; i < resultHeaders.length; i++) {
            resultHeaders[i] = headers.get(i);
        }
        return resultHeaders;
    }

    public Header[] sendWeiboHeaders(String app_src, String cookie) {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "*/*"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Host", "widget.weibo.com"));
        headers.add(new BasicHeader("Origin", "http://widget.weibo.com"));
        headers.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
        headers.add(new BasicHeader("Referer", "http://widget.weibo.com/topics/topic_vote_base.php?" +
                "tag=Weibo&app_src=" + app_src + "&isshowright=0&language=zh_cn"));
        headers.add(new BasicHeader("User-Agent", Constaces.User_Agent));
        headers.add(new BasicHeader("Cookie", cookie));
        
        Header[] resultHeaders = new Header[headers.size()];
        for (int i = 0; i < resultHeaders.length; i++) {
            resultHeaders[i] = headers.get(i);
        }
        return resultHeaders;
    }
    public HttpEntity sendWeiboEntity(String app_src, String content, String cookie, String pids) {
        List<NameValuePair> sendWeiboParam = new ArrayList<NameValuePair>();
        sendWeiboParam.add(new BasicNameValuePair("app_src", app_src));
        sendWeiboParam.add(new BasicNameValuePair("content", content));
        if (!TextUtils.isEmpty(pids)) {
            sendWeiboParam.add(new BasicNameValuePair("pic_id", pids));
        }
        sendWeiboParam.add(new BasicNameValuePair("return_type", "2"));
        sendWeiboParam.add(new BasicNameValuePair("refer", ""));
        sendWeiboParam.add(new BasicNameValuePair("vsrc", "base_topic"));
        sendWeiboParam.add(new BasicNameValuePair("wsrc", "app_topic_base"));
        sendWeiboParam.add(new BasicNameValuePair("ext", "login=>1;url=>"));
        sendWeiboParam.add(new BasicNameValuePair("html_type", "2"));
        sendWeiboParam.add(new BasicNameValuePair("_t", "0"));
//        if (!android.text.TextUtils.isEmpty(cookie)) {
//            sendWeiboParam.add(new BasicNameValuePair("Cookie", cookie));
//        }

        UrlEncodedFormEntity mEncodedFormEntity = null;
        try {
            mEncodedFormEntity = new UrlEncodedFormEntity(sendWeiboParam, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return mEncodedFormEntity;
    }
    
    /**
     * 新浪微博预登录，获取密码加密公钥
     * @param unameBase64
     * @return 返回从结果获取的参数的哈希表
     * @throws IOException
     */
    private PreLoginResult preLogin(String unameBase64, HttpClient client) {
        // 1416557391245
        long time = new Date().getTime();
        String url = buildPreLoginUrl(unameBase64, Constaces.SSOLOGIN_JS, time + "");

        HttpGet httpGet = HttpFactory.createHttpGet(url, preloginHeaders());
        HttpResponse httpResponse;
        String result = null;
        try {
            httpResponse = client.execute(httpGet);
            result = EntityUtils.toString(httpResponse.getEntity(), "GBK");
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        result = getJsonString(result);
        System.out.println("Pre Result : " + result);
        Gson gson = new Gson();
        PreLoginResult preLonginBean = gson.fromJson(result, PreLoginResult.class);

        return preLonginBean;
    }

    public PreLoginResult buildPreLoginResult(String result) {
        String realString = getJsonString(result);
        Gson gson = new Gson();
        PreLoginResult preLonginBean = gson.fromJson(realString, PreLoginResult.class);
        return preLonginBean;
    }

    private String getJsonString(String result) {
        Pattern p = Pattern.compile("\\{([^)]*?)\\}");
        Matcher matcher = p.matcher(result);
        if (matcher.find()) {
            result = matcher.group();
        }
        return result;
    }

    public String encodeAccount(String account) {
        String encodedString;
        try {
            encodedString = new String(Base64.encodeBase64(URLEncoder.encode(account, "UTF-8").getBytes()));
            String userName = encodedString.replace('+', '-').replace('/', '_');
            return userName;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
