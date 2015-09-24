package org.zarroboogs.weibo.service;

import org.zarroboogs.asyncokhttpclient.AsyncOKHttpClient;
import org.zarroboogs.asyncokhttpclient.SimpleHeaders;
import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.sinaweiboseniorapi.SeniorUrl;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class KeepCookieService extends Service implements Callback {

    private AsyncOKHttpClient mAsyncOKHttpClient = new AsyncOKHttpClient();

    private String mCookie;
    public static final String COOKIE_KEEP = "cookie_keep";
    private static final String TAG = "KeepCookieService";
    public static final int KeepCookie = 0x1100;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mCookie = bundle.getString(COOKIE_KEEP);
                if (!TextUtils.isEmpty(mCookie)) {
                    SimpleHeaders simpleHeaders = new SimpleHeaders();
                    simpleHeaders.addCookie(mCookie);

                    mAsyncOKHttpClient.asyncGet(SeniorUrl.SeniorUrl_Public, KeepCookieService.this);
                } else {
                    stopSelf();
                }
            } else {
                stopSelf();
            }

        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onFailure(Request request, IOException e) {

    }

    @Override
    public void onResponse(Response response) throws IOException {
        String r = response.body().string();
        DevLog.printLog(TAG, r);
        if (r.contains("sina_name")) {
            stopSelf();
        } else {
            stopSelf();
        }
    }
}
