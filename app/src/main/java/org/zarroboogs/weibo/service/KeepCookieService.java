package org.zarroboogs.weibo.service;

import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.devutils.http.AbsAsyncHttpService;
import org.zarroboogs.devutils.http.request.HeaderList;
import org.zarroboogs.sinaweiboseniorapi.SeniorUrl;
import org.zarroboogs.weibo.othercomponent.BeeboAlermManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

public class KeepCookieService extends AbsAsyncHttpService {

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
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                mCookie = bundle.getString(COOKIE_KEEP);
                if (!TextUtils.isEmpty(mCookie)) {
                    HeaderList headerList = new HeaderList();
                    headerList.addHeader("Cookie",mCookie);
                    asyncHttpGet(SeniorUrl.SeniorUrl_Public, headerList.build(), null);
                }else {
                    stopSelf();
                }
            }else {
                stopSelf();
            }

        }else {
            stopSelf();
        }
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onGetFailed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetSuccess(String arg0) {
		// TODO Auto-generated method stub
		DevLog.printLog(TAG, arg0);
		if (arg0.contains("sina_name")) {
//			BeeboAlermManager.keepCookie(this.getApplicationContext(), mCookie);
			stopSelf();
		}else {
			stopSelf();
		}
	}

	@Override
	public void onPostFailed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostSuccess(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestStart() {
		// TODO Auto-generated method stub
		
	}
}
