package org.zarroboogs.weibo.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.zarroboogs.devutils.Constaces;
import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.devutils.http.AbsAsyncHttpService;
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

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

public class RepostWithAppSrcServices extends AbsAsyncHttpService {

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
    	
        List<Header> headerList = new ArrayList<Header>();
	        headerList.add(new BasicHeader("Accept", "*/*"));
	        headerList.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
	        headerList.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));
	        headerList.add(new BasicHeader("Connection", "keep-alive"));
	        headerList.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
	        headerList.add(new BasicHeader("Host", "widget.weibo.com"));
	        headerList.add(new BasicHeader("Origin", "http://widget.weibo.com"));
	        headerList.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
	        headerList.add(new BasicHeader("Referer",
	                "http://widget.weibo.com/dialog/publish.php?button=forward&language=zh_cn&mid=" + mid +
	                        "&app_src=" + app_src + "&refer=1&rnd=14128245"));
	        headerList.add(new BasicHeader("User-Agent", Constaces.User_Agent));
        if (!TextUtils.isEmpty(cookie)) {
            headerList.add(new BasicHeader("Cookie", cookie));
		}

        
        Header[] repostHeaders = new Header[headerList.size()];
        headerList.toArray(repostHeaders);

        List<NameValuePair> nvs = new ArrayList<NameValuePair>();
	        nvs.add(new BasicNameValuePair("content", content));
	        nvs.add(new BasicNameValuePair("visible", "0"));
	        nvs.add(new BasicNameValuePair("refer", ""));
	
	        nvs.add(new BasicNameValuePair("app_src", app_src));
	        nvs.add(new BasicNameValuePair("mid", mid));
	        nvs.add(new BasicNameValuePair("return_type", "2"));
	
	        nvs.add(new BasicNameValuePair("vsrc", "publish_web"));
	        nvs.add(new BasicNameValuePair("wsrc", "app_publish"));
	        nvs.add(new BasicNameValuePair("ext", "login=>1;url=>"));
	        nvs.add(new BasicNameValuePair("html_type", "2"));
	        if (isComment) {
	        	nvs.add(new BasicNameValuePair("is_comment", "1"));
			}else {
				nvs.add(new BasicNameValuePair("is_comment", "0"));
			}
        
        nvs.add(new BasicNameValuePair("_t", "0"));

        UrlEncodedFormEntity repostEntity = null;
        try {
            repostEntity = new UrlEncodedFormEntity(nvs, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        asyncHttpPost(Constaces.REPOST_WEIBO, repostHeaders, repostEntity, "application/x-www-form-urlencoded");
    }
    
    
	private String getCookieIfHave() {
		String cookieInDB = BeeboApplication.getInstance().getAccountBean().getCookieInDB();
		if (!TextUtils.isEmpty(cookieInDB)) {
			return cookieInDB;
		}
		return "";
	}
    
    
	@Override
	public void onGetFailed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		DevLog.printLog(TAG, arg0);
	}

	@Override
	public void onGetSuccess(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostFailed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
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
    
    private void showSendingNotification(){
        Notification.Builder builder = new Notification.Builder(this)
                .setTicker(getString(R.string.repost))
                .setContentTitle(getString(R.string.wait_server_response))
                .setNumber(100).setProgress(100, 100, false)
                .setOnlyAlertOnce(true).setOngoing(true).setSmallIcon(R.drawable.upload_white);

        Notification notification = builder.getNotification();

    NotificationUtility.show(notification, R.string.repost);
}
    
    public void clearAppsrc(){
        SharedPreferences appsrcPreferences  = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        appsrcPreferences.edit().remove(Constants.KEY_NAME).remove(Constants.KEY_CODE).commit();
    }
    
	@Override
	public void onPostSuccess(String arg0) {
		// TODO Auto-generated method stub
		SendWeiboResultBean sb = new Gson().fromJson(arg0, SendWeiboResultBean.class);
		if (sb.isSuccess()) {
			
			NotificationUtility.cancel(R.string.repost);
			
			showSuccessfulNotification();
			clearAppsrc();
			this.stopSelf();
			DevLog.printLog(TAG, "发送成功！");
		}else {
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
						}else {
							startWebLogin();
						}
					}
				});
			}
		}
	}
	
    public void startWebLogin() {
        Intent intent = new Intent();
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(RepostWithAppSrcServices.this, WebViewActivity.class);
        startActivity(intent);
    }

    class WeiBaCacheFile implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {
            // TODO Auto-generated method stub
            return filename.startsWith("WEI-");
        }

    }
    

	@Override
	public void onRequestStart() {
		// TODO Auto-generated method stub
		
	}

}
