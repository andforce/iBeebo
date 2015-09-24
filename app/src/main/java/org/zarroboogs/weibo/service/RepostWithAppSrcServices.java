package org.zarroboogs.weibo.service;


import java.io.IOException;


import org.zarroboogs.asyncokhttpclient.AsyncOKHttpClient;
import org.zarroboogs.asyncokhttpclient.SimpleHeaders;
import org.zarroboogs.devutils.Constaces;
import org.zarroboogs.devutils.DevLog;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.JSAutoLogin;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.WebViewActivity;
import org.zarroboogs.weibo.JSAutoLogin.AutoLogInListener;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.SendWeiboResultBean;
import org.zarroboogs.weibo.bean.WeiboWeiba;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.NotificationUtility;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

public class RepostWithAppSrcServices extends Service implements Callback {

    private AsyncOKHttpClient mAsyncOKHttpClient = new AsyncOKHttpClient();
    private AccountBean mAccountBean;
    private WeiboWeiba mAppSrc = null;
    private String mTextContent;
    private String mMid;
    private boolean isComment = false;
    private JSAutoLogin mJsAutoLogin;

    private String TAG = "SendWithAppSrcServices";
    public static final String APP_SRC = "mAppSrc";
    public static final String TEXT_CONTENT = "TEXT_CONTENT";
    public static final String WEIBO_MID = "mid";
    public static final String IS_COMMENT = "is_comment";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mAccountBean = BeeboApplication.getInstance().getAccountBean();
        mJsAutoLogin = new JSAutoLogin(getApplicationContext(), mAccountBean);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        mAppSrc = (WeiboWeiba) intent.getExtras().getSerializable(APP_SRC);
        mTextContent = intent.getExtras().getString(TEXT_CONTENT);
        mMid = intent.getExtras().getString(WEIBO_MID);
        isComment = intent.getExtras().getBoolean(IS_COMMENT);

        repostWeibo(mAppSrc.getCode(), mTextContent, getCookieIfHave(), mMid, isComment);
        return super.onStartCommand(intent, flags, startId);
    }


    public void repostWeibo(String app_src, String content, String cookie, String mid, boolean isComment) {
        cookie = getCookieIfHave();

        showSendingNotification();

        SimpleHeaders simpleHeaders = new SimpleHeaders();
        simpleHeaders.addAccept("*/*");
        simpleHeaders.addAcceptEncoding("gzip, deflate");
        simpleHeaders.addAcceptLanguage("zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        simpleHeaders.addConnection("keep-alive");
        simpleHeaders.addContentType("application/x-www-form-urlencoded");
        simpleHeaders.addHost("widget.weibo.com");
        simpleHeaders.addOrigin("http://widget.weibo.com");
        simpleHeaders.addReferer("http://widget.weibo.com/dialog/publish.php?button=forward&language=zh_cn&mid=" + mid +
                "&app_src=" + app_src + "&refer=1&rnd=14128245");
        simpleHeaders.addUserAgent(Constaces.User_Agent);
        simpleHeaders.add("X-Requested-With", "XMLHttpRequest");
        if (!TextUtils.isEmpty(cookie)) {
            simpleHeaders.add("Cookie", cookie);
        }


        RequestBody requestBody = new FormEncodingBuilder()
                .add("content", content)
                .add("visible", "0")
                .add("refer", "")

                .add("app_src", app_src)
                .add("mid", mid)
                .add("return_type", "2")

                .add("vsrc", "publish_web")
                .add("wsrc", "app_publish")
                .add("ext", "login=>1;url=>")
                .add("html_type", "2").add("is_comment", isComment ? "1" : "0").build();


        mAsyncOKHttpClient.asyncPost(Constaces.REPOST_WEIBO, simpleHeaders, requestBody, this);
    }


    private String getCookieIfHave() {
        String cookieInDB = BeeboApplication.getInstance().getAccountBean().getCookieInDB();
        if (!TextUtils.isEmpty(cookieInDB)) {
            return cookieInDB;
        }
        return "";
    }

    private Handler handler = new Handler();

    private void showSuccessfulNotification() {
        Notification.Builder builder = new Notification.Builder(RepostWithAppSrcServices.this)
                .setTicker(getString(R.string.send_successfully))
                .setContentTitle(getString(R.string.send_successfully)).setOnlyAlertOnce(true).setAutoCancel(true)
                .setSmallIcon(R.drawable.send_successfully).setOngoing(false);
        Notification notification = builder.getNotification();
        NotificationUtility.show(notification, R.string.send_successfully);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationUtility.cancel(R.string.send_successfully);
            }
        }, 3000);
    }

    private void showSendingNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setTicker(getString(R.string.repost))
                .setContentTitle(getString(R.string.wait_server_response))
                .setNumber(100).setProgress(100, 100, false)
                .setOnlyAlertOnce(true).setOngoing(true).setSmallIcon(R.drawable.upload_white);

        Notification notification = builder.getNotification();

        NotificationUtility.show(notification, R.string.repost);
    }

    public void clearAppsrc() {
        SharedPreferences appsrcPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        appsrcPreferences.edit().remove(Constants.KEY_NAME).remove(Constants.KEY_CODE).commit();
    }


    public void startWebLogin() {
        Intent intent = new Intent();
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(RepostWithAppSrcServices.this, WebViewActivity.class);
        startActivity(intent);
    }


    @Override
    public void onFailure(Request request, IOException e) {

    }

    @Override
    public void onResponse(Response response) throws IOException {
        SendWeiboResultBean sb = new Gson().fromJson(response.body().string(), SendWeiboResultBean.class);
        if (sb.isSuccess()) {

            NotificationUtility.cancel(R.string.repost);

            showSuccessfulNotification();
            clearAppsrc();
            this.stopSelf();
            DevLog.printLog(TAG, "发送成功！");
        } else {
            NotificationUtility.cancel(R.string.repost);

            DevLog.printLog(TAG, sb.getCode() + "    " + sb.getMsg());
            if (sb.getMsg().equals("未登录")) {
                mJsAutoLogin.exejs();
                mJsAutoLogin.setAutoLogInListener(new AutoLogInListener() {

                    @Override
                    public void onAutoLonin(boolean result) {
                        // TODO Auto-generated method stub
                        if (result) {
                            repostWeibo(mAppSrc.getCode(), mTextContent, getCookieIfHave(), mMid, isComment);
                        } else {
                            startWebLogin();
                        }
                    }
                });
            }
        }
    }
}
