
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

    public boolean repostWeibo(BroserContent broserContent, String url, String app_src, String content, String cookie,
            String mid) {
        CloseableHttpClient httpClient = broserContent.getHttpClient();
        // http://widget.weibo.com/public/aj_repost.php

        List<Header> requestHeaders = new ArrayList<Header>();
        requestHeaders.add(new BasicHeader("Accept", "*/*"));
        requestHeaders.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        requestHeaders.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));
        requestHeaders.add(new BasicHeader("Connection", "keep-alive"));
        requestHeaders.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        requestHeaders.add(new BasicHeader("Host", "widget.weibo.com"));
        requestHeaders.add(new BasicHeader("Origin", "http://widget.weibo.com"));
        requestHeaders.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
        requestHeaders.add(new BasicHeader("Referer",
                "http://widget.weibo.com/dialog/publish.php?button=forward&language=zh_cn&mid=" + mid +
                        "&app_src=" + app_src + "&refer=1&rnd=14128245"));
        requestHeaders.add(new BasicHeader("User-Agent", Constaces.User_Agent));

        List<NameValuePair> loginParams = new ArrayList<NameValuePair>();
        loginParams.add(new BasicNameValuePair("content", content));
        loginParams.add(new BasicNameValuePair("visible", "0"));
        loginParams.add(new BasicNameValuePair("refer", ""));

        loginParams.add(new BasicNameValuePair("app_src", app_src));
        loginParams.add(new BasicNameValuePair("mid", mid));
        loginParams.add(new BasicNameValuePair("return_type", "2"));

        loginParams.add(new BasicNameValuePair("vsrc", "publish_web"));
        loginParams.add(new BasicNameValuePair("wsrc", "app_publish"));
        loginParams.add(new BasicNameValuePair("ext", "login=>1;url=>"));
        loginParams.add(new BasicNameValuePair("html_type", "2"));
        loginParams.add(new BasicNameValuePair("is_comment", "1"));
        loginParams.add(new BasicNameValuePair("_t", "0"));
        // loginParams.add(new BasicNameValuePair("Cookie", cookie));

        HttpPost logInPost = HttpFactory.createHttpPost(url, requestHeaders, loginParams);

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
            Log.d("RES_CODE_POST: ", allResponse);
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

    public String uploadPicToWeibo(BroserContent broserContent, String url, String cookie, String picurl) {
        // http://picupload.service.weibo.com/interface/pic_upload.php?app=miniblog&data=1&mime=image/png&ct=0.2805887470021844
        CloseableHttpClient httpClient = broserContent.getHttpClient();

        List<Header> requestHeaders = new ArrayList<Header>();
        requestHeaders.add(new BasicHeader("Host", "picupload.service.weibo.com"));
        requestHeaders.add(new BasicHeader("Origin", "http://tjs.sjs.sinajs.cn"));
        requestHeaders.add(new BasicHeader("User-Agent", Constaces.User_Agent));
        requestHeaders.add(new BasicHeader("Content-Type", "application/octet-stream"));
        requestHeaders.add(new BasicHeader("Accept", "*/*"));
        requestHeaders.add(new BasicHeader("Referer",
                "http://tjs.sjs.sinajs.cn/open/widget/static/swf/MultiFilesUpload.swf?" + "version=1411256448572"));
        requestHeaders.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        requestHeaders.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));

        List<NameValuePair> loginParams = new ArrayList<NameValuePair>();

        HttpPost logInPost = HttpFactory.createHttpPost(url, requestHeaders, loginParams);

        File file = new File(picurl);
        FileEntity reqEntity = new FileEntity(file, "binary/octet-stream");

        logInPost.setEntity(reqEntity);
        CloseableHttpResponse logInResponse = null;
        String allResponse = null;
        boolean isSuccess = false;
        try {
            logInResponse = httpClient.execute(logInPost);
            HttpEntity mEntity = logInResponse.getEntity();
            allResponse = null;
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
            Log.d("START_SEND_WEIBO  ",
                    "" + logInResponse.getStatusLine().getStatusCode() + "result: " + allResponse + "\r\n"
                            + PatternUtils.preasePid(allResponse));

            if (logInResponse.getStatusLine().getStatusCode() == 200) {
                isSuccess = true;
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
        Gson mGson = new Gson();

        UploadPicResult ur = mGson.fromJson(PatternUtils.preasePid(allResponse), UploadPicResult.class);
        Log.d("RES_CODE_POST_PIC", ur.getPid());
        if (isSuccess) {
            return ur.getPid();

        }
        return "";
    }

    public boolean loginNetEase(BroserContent broserContent, String url, String name, String password) {
        CloseableHttpClient httpClient = broserContent.getHttpClient();

        List<Header> requestHeaders = new ArrayList<Header>();
        requestHeaders.add(new BasicHeader("Accept", CourseHeader.Accept));
        requestHeaders.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
        requestHeaders.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2"));
        requestHeaders.add(new BasicHeader("Cache-Control", "max-age=0"));
        requestHeaders.add(new BasicHeader("Connection", "keep-alive"));
        requestHeaders.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        requestHeaders.add(new BasicHeader("Host", "reg.163.com"));
        requestHeaders.add(new BasicHeader("Origin", "http://study.163.com"));
        requestHeaders.add(new BasicHeader("Referer", "http://study.163.com/"));
        requestHeaders.add(new BasicHeader("User-Agent", CourseHeader.User_Agent));

        List<NameValuePair> loginParams = new ArrayList<NameValuePair>();
        loginParams.add(new BasicNameValuePair("product", "study"));
        loginParams.add(new BasicNameValuePair("url", "http://study.163.com?from=study"));
        loginParams.add(new BasicNameValuePair("savelogin", "1"));
        loginParams.add(new BasicNameValuePair("domains", "163.com"));
        loginParams.add(new BasicNameValuePair("type", "0"));
        loginParams.add(new BasicNameValuePair("append", "1"));
        loginParams.add(new BasicNameValuePair("username", name));
        loginParams.add(new BasicNameValuePair("password", password));

        HttpPost logInPost = HttpFactory.createHttpPost(url, requestHeaders, loginParams);

        CloseableHttpResponse logInResponse = null;
        boolean isSuccess = false;
        try {
            logInResponse = httpClient.execute(logInPost);
            if (logInResponse.getStatusLine().getStatusCode() == 200) {
                isSuccess = true;
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

    public String getVideoAuthorityToken4Mobile(BroserContent broserContent, String videoUrl) {
        HttpClient httpClient = broserContent.getHttpClient();
        CookieStore cookieStore = broserContent.getCookieStore();

        String videoToaken = getVideoAuthorityToken4MobileResponse(httpClient, cookieStore, videoUrl);
        return PatternUtils.getVideoToaken(videoToaken);

    }

    private String getVideoAuthorityToken4MobileResponse(HttpClient httpClient, CookieStore cookieStore, String videoUrl) {

        String httpSessionId = CookieUtils.getHttpSessionId(cookieStore);

        List<Header> requestHeaders = new ArrayList<Header>();
        requestHeaders.add(new BasicHeader("Accept", "*/*"));
        requestHeaders.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
        requestHeaders.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2"));
        requestHeaders.add(new BasicHeader("Cache-Control", "max-age=0"));
        requestHeaders.add(new BasicHeader("Connection", "keep-alive"));
        requestHeaders.add(new BasicHeader("Content-Type", "text/plain"));
        requestHeaders.add(new BasicHeader("Host", "study.163.com"));
        requestHeaders.add(new BasicHeader("Origin", "http://study.163.com"));
        requestHeaders.add(new BasicHeader("Referer", "http://study.163.com/find.htm"));
        requestHeaders.add(new BasicHeader("User-Agent", CourseHeader.User_Agent));

        List<NameValuePair> allCourceParam = new ArrayList<NameValuePair>();
        allCourceParam.add(new BasicNameValuePair("callCount", "1"));
        allCourceParam.add(new BasicNameValuePair("scriptSessionId", "${scriptSessionId}190"));
        allCourceParam.add(new BasicNameValuePair("httpSessionId", httpSessionId));

        allCourceParam.add(new BasicNameValuePair("c0-scriptName", "VideoBean"));
        allCourceParam.add(new BasicNameValuePair("c0-methodName", "getVideoAuthorityToken4MobileBrowser"));

        allCourceParam.add(new BasicNameValuePair("c0-id", "0"));
        allCourceParam.add(new BasicNameValuePair("c0-param0", "string:" + URLEncoder.encode(videoUrl)));
        allCourceParam.add(new BasicNameValuePair("batchId", "757817"));

        // HttpPost allCoursePost = HttpPostFactory.createHttpPost(CourseUrls.VideoToaken,
        // allCourseHeaders, allCourceParam);
        HttpPost allCoursePost = HttpFactory.createHttpPost(CourseUrls.VideoToaken, requestHeaders, allCourceParam);

        CloseableHttpResponse postResult = null;

        String responseString = null;
        try {
            postResult = (CloseableHttpResponse) httpClient.execute(allCoursePost);
            responseString = ResponseUtils.getResponseLines(false, postResult);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (postResult != null) {
                    postResult.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return responseString;
    }

    private String getCategoryTreeString(HttpClient httpClient, CookieStore cookieStore) {

        String httpSessionId = CookieUtils.getHttpSessionId(cookieStore);

        List<Header> requestHeaders = new ArrayList<Header>();
        requestHeaders.add(new BasicHeader("Accept", "*/*"));
        requestHeaders.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
        requestHeaders.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2"));
        requestHeaders.add(new BasicHeader("Cache-Control", "max-age=0"));
        requestHeaders.add(new BasicHeader("Connection", "keep-alive"));
        requestHeaders.add(new BasicHeader("Content-Type", "text/plain"));
        requestHeaders.add(new BasicHeader("Host", "study.163.com"));
        requestHeaders.add(new BasicHeader("Origin", "http://study.163.com"));
        requestHeaders.add(new BasicHeader("Referer", "http://study.163.com/find.htm"));
        requestHeaders.add(new BasicHeader("User-Agent", CourseHeader.User_Agent));

        List<NameValuePair> allCourceParam = new ArrayList<NameValuePair>();
        allCourceParam.add(new BasicNameValuePair("callCount", "1"));
        allCourceParam.add(new BasicNameValuePair("scriptSessionId", "${scriptSessionId}190"));
        allCourceParam.add(new BasicNameValuePair("httpSessionId", httpSessionId));

        allCourceParam.add(new BasicNameValuePair("c0-scriptName", "CategoryBean"));
        allCourceParam.add(new BasicNameValuePair("c0-methodName", "getFilterRecommendCategoryTree"));

        allCourceParam.add(new BasicNameValuePair("c0-id", "0"));
        allCourceParam.add(new BasicNameValuePair("c0-param0", "number:1"));
        allCourceParam.add(new BasicNameValuePair("batchId", "757817"));

        // HttpPost allCoursePost = HttpPostFactory.createHttpPost(CourseUrls.CategroyTree,
        // allCourseHeaders, allCourceParam);
        HttpPost allCoursePost = HttpFactory.createHttpPost(CourseUrls.CategroyTree, requestHeaders, allCourceParam);
        CloseableHttpResponse postResult = null;
        String responseString = null;
        try {
            postResult = (CloseableHttpResponse) httpClient.execute(allCoursePost);
            responseString = ResponseUtils.getResponseLines(false, postResult);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (postResult != null) {
                    postResult.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return responseString;
    }

    public String getVideoInfo(HttpClient httpClient, CookieStore cookieStore, int courseID, int lessionID) {

        String httpSessionId = CookieUtils.getHttpSessionId(cookieStore);

        List<Header> requestHeaders = new ArrayList<Header>();
        requestHeaders.add(new BasicHeader("Accept", "*/*"));
        requestHeaders.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
        requestHeaders.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2"));
        requestHeaders.add(new BasicHeader("Cache-Control", "max-age=0"));
        requestHeaders.add(new BasicHeader("Connection", "keep-alive"));
        requestHeaders.add(new BasicHeader("Content-Type", "text/plain"));
        requestHeaders.add(new BasicHeader("Host", "study.163.com"));
        requestHeaders.add(new BasicHeader("Origin", "http://study.163.com"));
        requestHeaders.add(new BasicHeader("Referer", "http://study.163.com/find.htm"));
        requestHeaders.add(new BasicHeader("User-Agent", CourseHeader.User_Agent));

        List<NameValuePair> getVideoInfoParams = new ArrayList<NameValuePair>();
        getVideoInfoParams.add(new BasicNameValuePair("callCount", "1"));
        getVideoInfoParams.add(new BasicNameValuePair("scriptSessionId", "${scriptSessionId}190"));
        getVideoInfoParams.add(new BasicNameValuePair("httpSessionId", httpSessionId));
        getVideoInfoParams.add(new BasicNameValuePair("c0-scriptName", "LessonLearnBean"));
        getVideoInfoParams.add(new BasicNameValuePair("c0-methodName", "getVideoLearnInfo"));
        getVideoInfoParams.add(new BasicNameValuePair("c0-id", "0"));
        getVideoInfoParams.add(new BasicNameValuePair("c0-param0", "string:" + lessionID));
        getVideoInfoParams.add(new BasicNameValuePair("c0-param1", "string:" + courseID));
        getVideoInfoParams.add(new BasicNameValuePair("batchId", "905319"));

        // HttpPost getVideoInfoHttpPost = HttpPostFactory.createHttpPost(CourseUrls.GetVideoInfo,
        // getVideoInfoHeaders, getVideoInfoParams);
        HttpPost getVideoInfoHttpPost = HttpFactory.createHttpPost(CourseUrls.GetVideoInfo, requestHeaders,
                getVideoInfoParams);

        CloseableHttpResponse mHttpResponse = null;
        String responseString = null;
        try {
            mHttpResponse = (CloseableHttpResponse) httpClient.execute(getVideoInfoHttpPost);
            responseString = ResponseUtils.getResponseLines(false, mHttpResponse);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (mHttpResponse != null) {
                    mHttpResponse.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return responseString;
    }

}
