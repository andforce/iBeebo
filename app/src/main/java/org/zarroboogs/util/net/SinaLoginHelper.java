
package org.zarroboogs.util.net;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.zarroboogs.devutils.Constaces;
import org.zarroboogs.devutils.http.request.HeaderList;
import org.zarroboogs.devutils.http.request.HttpEntryList;

import android.text.TextUtils;

public class SinaLoginHelper {



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

}
