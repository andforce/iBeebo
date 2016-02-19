package org.zarroboogs.weibo.service;


import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.http.AsyncHttpHeaders;
import org.zarroboogs.http.AsyncHttpRequest;
import org.zarroboogs.http.AsyncHttpResponse;
import org.zarroboogs.http.AsyncHttpResponseHandler;
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

public class KeepCookieService extends Service {

    private AsyncHttpRequest mAsyncOKHttpClient = new AsyncHttpRequest();

    private String mCookie;
    public static final String COOKIE_KEEP = "cookie_keep";
    private static final String TAG = "KeepCookieService";
    public static final int KeepCookie = 0x1100;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mCookie = bundle.getString(COOKIE_KEEP);
                if (!TextUtils.isEmpty(mCookie)) {
                    AsyncHttpHeaders simpleHeaders = new AsyncHttpHeaders();
                    simpleHeaders.addCookie(mCookie);

                    mAsyncOKHttpClient.get(SeniorUrl.SeniorUrl_Public, new AsyncHttpResponseHandler() {
                        @Override
                        public void onFailure(IOException e) {

                        }

                        @Override
                        public void onSuccess(AsyncHttpResponse response) {
                            String r = response.getBody();
                            DevLog.printLog(TAG, r);
                            if (r.contains("sina_name")) {
                                stopSelf();
                            } else {
                                stopSelf();
                            }
                        }
                    });
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
}
