
package lib.org.zarroboogs.utils.net;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
//import org.apache.http.client.config.CookieSpecs;
//import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpParams;

public class HttpFactory {

    public static HttpGet createHttpGet(String requestUrl, Header[] headers) {
        HttpGet httpGet = new HttpGet(requestUrl);
        // RequestConfig getConfig =
        // RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
        // .setConnectTimeout(5000).setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
        // httpGet.setConfig(getConfig);
        if (headers != null) {
            httpGet.setHeaders(headers);
        }
        return httpGet;
    }

    public static HttpPost createHttpPost(String mPostURL, Header[] headers, List<NameValuePair> mFornData) {
        HttpPost mHttpPost = new HttpPost(mPostURL);

        // RequestConfig postConfig =
        // RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
        // .setConnectTimeout(5000).setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
        // mHttpPost.setConfig(postConfig);

        mHttpPost.setHeaders(headers);

        UrlEncodedFormEntity mEncodedFormEntity = null;
        try {
            mEncodedFormEntity = new UrlEncodedFormEntity(mFornData, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        mHttpPost.setEntity(mEncodedFormEntity);

        return mHttpPost;
    }

}
