
package org.zarroboogs.util.net;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;

public class HttpGetFactory {

    /**
     * @param requestUrl
     * @param mHeaders
     * @return
     */
    public static HttpGet createHttpGet(String requestUrl, Header[] headers) {
        HttpGet httpGet = new HttpGet(requestUrl);
        if (headers != null) {
            httpGet.setHeaders(headers);
        }

        // httpGet.setHeader("User-Agent",
        // "Mozilla/5.0 (Linux; Android 4.3; Nexus 10 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.72 Safari/537.36");
        // httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        // httpGet.setHeader("Accept-Encoding","gzip,deflate,sdch");
        // httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2");
        // httpGet.setHeader("Connection", "keep-alive");
        // httpGet.addHeader("Content-Type","text/html; charset=UTF-8");

        return httpGet;
    }

}
