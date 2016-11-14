
package org.zarroboogs.util.net;

import org.zarroboogs.utils.file.FileDownloaderHttpHelper;
import org.zarroboogs.utils.file.FileUploaderHttpHelper;

import java.util.Map;

public class HttpUtility {

    public enum HttpMethod {
        Post, Get, Get_AVATAR_File, Get_PICTURE_FILE
    }

    private static HttpUtility httpUtility = new HttpUtility();

    private HttpUtility() {
    }

    public static HttpUtility getInstance() {
        return httpUtility;
    }

    public String executeNormalTask(HttpMethod httpMethod, String url, Map<String, String> param) throws WeiboException {
        return new JavaHttpUtility().executeNormalTask(httpMethod, url, param);
    }

    public boolean executeDownloadTask(String url, String path, FileDownloaderHttpHelper.DownloadListener downloadListener) {
        return !Thread.currentThread().isInterrupted() && new JavaHttpUtility().doGetSaveFile(url, path, downloadListener);
    }

    public boolean executeUploadTask(String url, Map<String, String> param, String path, String imageParamName,
                                     FileUploaderHttpHelper.ProgressListener listener)
            throws WeiboException {
        return !Thread.currentThread().isInterrupted()
                && new JavaHttpUtility().doUploadFile(url, param, path, imageParamName, listener);
    }
}
