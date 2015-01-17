
package org.zarroboogs.vup;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.zarroboogs.vup.utils.LogTool;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.text.TextUtils;

public class VideoUrlParser {

    private Context mContext;
    private AsyncHttpClient mAsyncHttpClient;
    public static final String FLVCD = "http://www.flvcd.com/parse.php?format=&kw=";
    private static final String TAG = "Log_VideoUrlParser";
    private static final boolean DEBUG = false;

    public interface OnParsedListener {
        public void onParseSuccess(String url, String name);

        public void onParseFailed();
    }

    public VideoUrlParser(Context context) {
        this.mContext = context;
        mAsyncHttpClient = new AsyncHttpClient();
    }

    public void parseVideoUrl(String orgUrl, final OnParsedListener listener) {
        mAsyncHttpClient.get(mContext, FLVCD + orgUrl, parseHeaders(null), null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    String responseString = null;
                    try {
                        responseString = new String(responseBody, "GBK");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String name = null;
                    String url = null;
                    
                    Document document = Jsoup.parse(responseString);
                    //<form action="get_m3u.php" method="post" name="m3uForm">
                    Elements realNameElement = document.select("form").attr("name", "m3uForm").attr("action", "get_m3u.php")
                            .attr("method", "post");
                    for (Element element : realNameElement) {
//                        <input type="hidden" name="inf" value="http://flv4.bn.netease.com/videolib3/1412/28/lpsVG2252/SD/lpsVG2252.flv|"/>
//                        <input type="hidden" name="filename" value="多地网友隔空拼酒1到6斤 医生称不可取"/>
                        
                        Elements inputElements = element.select("input").attr("type", "hidden");
                        
                        for (Element input : inputElements) {
                            if (input.attr("name").equals("inf")) {
                                url = input.attr("value").replace("|", "");
                                LogTool.printLog(TAG + "++++++++++++  value : ++++++++++++" + input.attr("value"));
                            }
                            if (input.attr("name").equals("filename")){
                                name = input.attr("value");
                            }
                        }
                       
                    }
                    if (DEBUG) {
                        String[] respones = responseString.split("\r\n");
                        for (String res : respones) {
                            LogTool.printLog(TAG + res);
                        }
                    }
                    if (TextUtils.isEmpty(url)) {
                        listener.onParseFailed();
                    }else {
                        listener.onParseSuccess(url, name);
                    }
                    
                }else {
                    listener.onParseFailed();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onParseFailed();
                }
            }
        });
    }
    
    private Header[] parseHeaders(String orgUrl){
        Header[] headers = new Header[]{
                new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
                new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"),
                new BasicHeader("Accept-Language", "h-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"),
                new BasicHeader("Cache-Control", "no-cache"),
                new BasicHeader("Connection", "keep-alive"),
                new BasicHeader("Host", "www.flvcd.com"),
                new BasicHeader("Pragma", "no-cache"),
//                new BasicHeader("Referer", ""),
                new BasicHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/36.0.1985.125 Chrome/36.0.1985.125 Safari/537.36"),
        };
        return headers;
        
    }
    
//    Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
//            Accept-Encoding:gzip,deflate,sdch
//            Accept-Language:zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4
//            Cache-Control:no-cache
//            Connection:keep-alive
//            Host:www.flvcd.com
//            Pragma:no-cache
//            Referer:http://www.flvcd.com/parse.php?format=&kw=http%3A%2F%2Fwww.meipai.com%2Fmedia%2F213733182
//            User-Agent:Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/36.0.1985.125 Chrome/36.0.1985.125 Safari/537.36
//            
}
