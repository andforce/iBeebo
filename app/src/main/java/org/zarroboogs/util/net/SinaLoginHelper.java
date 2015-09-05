
package org.zarroboogs.util.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.org.zarroboogs.weibo.login.javabean.PreLoginResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.zarroboogs.devutils.Constaces;
import org.zarroboogs.devutils.http.request.HeaderList;
import org.zarroboogs.devutils.http.request.HttpEntryList;

import android.text.TextUtils;

import com.google.gson.Gson;

public class SinaLoginHelper {

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
