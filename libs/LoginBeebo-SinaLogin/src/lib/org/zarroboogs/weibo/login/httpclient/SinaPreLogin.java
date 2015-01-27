
package lib.org.zarroboogs.weibo.login.httpclient;

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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.zarroboogs.utils.http.HeaderList;
import org.zarroboogs.utils.http.HttpEntryList;

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
    	HeaderList headers = new HeaderList();
        headers.addHost("login.sina.com.cn");
        headers.addHeader("Cache-Control", "max-age=0");
        headers.addAccept("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.addOrigin("http://widget.weibo.com");
        headers.addUserAgent(Constaces.User_Agent);
        headers.addHeader("Content-Type", "application/x-www-form-urlencoded");
        headers.addReferer("http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH");
        headers.addAcceptEncoding("gzip,deflate");
        headers.addAcceptLanguage("zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        
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
        nvps.add(new BasicNameValuePair("url","http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
        nvps.add(new BasicNameValuePair("returntype", "META"));

        HttpPost post = HttpFactory.createHttpPost(Constaces.LOGIN_FIRST_URL, headers.build(), nvps);
        return post;
    }

    public HttpEntity afterPreLoginEntity(String encodeUserName, String encondePassWord, String door, PreLoginResult params) {

    	HttpEntryList entrys = new HttpEntryList();
    	
        entrys.addEntry("entry", "weibo");
        entrys.addEntry("gateway", "1");
        entrys.addEntry("from", "");
        entrys.addEntry("savestate", "7");
        entrys.addEntry("useticket", "1");
        entrys.addEntry("pagerefer", "");
        entrys.addEntry("pcid", params.getPcid());
        if (!TextUtils.isEmpty(door)) {
        	entrys.addEntry("door", door);
        }
        entrys.addEntry("vsnf", "1");
        entrys.addEntry("su", encodeUserName);
        entrys.addEntry("service", "miniblog");
        entrys.addEntry("servertime", params.getServertime() + "");
        entrys.addEntry("nonce", params.getNonce());
        entrys.addEntry("pwencode", "rsa2");
        entrys.addEntry("rsakv", params.getRsakv());
        entrys.addEntry("sp", encondePassWord);
        entrys.addEntry("encoding", "UTF-8");
        entrys.addEntry("prelt", "166");
        entrys.addEntry("url","http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack");
        entrys.addEntry("returntype", "META");
        return entrys.build();

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
        HeaderList headers = new HeaderList();
        headers.addHost("login.sina.com.cn");
        headers.addAccept("*/*");
        headers.addUserAgent(Constaces.User_Agent);
        headers.addReferer("http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH");
        headers.addAcceptEncoding("gzip,deflate,sdch");
        headers.addAcceptLanguage("zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        return headers.build();
    }

    public Header[] afterPreLoginHeaders() {
    	HeaderList headerList = new HeaderList();
        headerList.addHost("login.sina.com.cn");
        headerList.addHeader("Cache-Control", "max-age=0");
        headerList.addAccept("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headerList.addOrigin("http://widget.weibo.com");
        headerList.addUserAgent(Constaces.User_Agent);
        headerList.addHeader("Content-Type", "application/x-www-form-urlencoded");
        headerList.addReferer("http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH");
        headerList.addAcceptEncoding("gzip,deflate");
        headerList.addAcceptLanguage("zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
       return headerList.build();
    }

    public Header[] sendWeiboHeaders(String app_src, String cookie) {
    	HeaderList headerList = new HeaderList();
    	headerList.addAccept("*/*");
        headerList.addAcceptEncoding("gzip, deflate");
        headerList.addAcceptLanguage("zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        headerList.addHost("widget.weibo.com");
        headerList.addOrigin("http://widget.weibo.com");
        headerList.addReferer("http://widget.weibo.com/topics/topic_vote_base.php?tag=Weibo&app_src=" + app_src + "&isshowright=0&language=zh_cn");
        headerList.addUserAgent(Constaces.User_Agent);
        headerList.addHeader("Cookie", cookie);
        headerList.addHeader("X-Requested-With", "XMLHttpRequest");
        headerList.addHeader("Connection", "keep-alive");
        headerList.addHeader("Content-Type", "application/x-www-form-urlencoded");
        return headerList.build();
    }
    public HttpEntity sendWeiboEntity(String app_src, String content, String cookie, String pids) {
    	HttpEntryList sendWriteEntryList = new HttpEntryList();
    	sendWriteEntryList.addEntry("app_src", app_src);
    	sendWriteEntryList.addEntry("content", content);
    	if (!TextUtils.isEmpty(pids)) {
    		sendWriteEntryList.addEntry("pic_id", pids);
    	}
    	sendWriteEntryList.addEntry("return_type", "2");
    	sendWriteEntryList.addEntry("refer", "");
    	sendWriteEntryList.addEntry("vsrc", "base_topic");
    	sendWriteEntryList.addEntry("wsrc", "app_topic_base");
    	sendWriteEntryList.addEntry("ext", "login=>1;url=>");
    	sendWriteEntryList.addEntry("html_type", "2");
    	sendWriteEntryList.addEntry("_t", "0");
    	sendWriteEntryList.addEntry("html_type", "2");
    	sendWriteEntryList.addEntry("html_type", "2");
    	sendWriteEntryList.addEntry("html_type", "2");
    	sendWriteEntryList.addEntry("html_type", "2");
    	return sendWriteEntryList.build();
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
