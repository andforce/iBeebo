package org.zarroboogs.weibo.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.zarroboogs.devutils.Constaces;
import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.devutils.http.AbsAsyncHttpService;
import org.zarroboogs.util.net.SinaLoginHelper;
import org.zarroboogs.util.net.UploadHelper;
import org.zarroboogs.util.net.UploadHelper.OnUpFilesListener;
import org.zarroboogs.util.net.WaterMark;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.SendBitmapWorkerTask;
import org.zarroboogs.utils.SendBitmapWorkerTask.OnCacheDoneListener;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.JSAutoLogin;
import org.zarroboogs.weibo.JSAutoLogin.AutoLogInListener;
import org.zarroboogs.weibo.JSAutoLogin.CheckUserNamePasswordListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.WebViewActivity;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.SendWeiboResultBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.WeiboWeiba;
import org.zarroboogs.weibo.db.task.AccountDao;
import org.zarroboogs.weibo.selectphoto.SendImgData;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.NotificationUtility;

import com.google.gson.Gson;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class SendWithAppSrcServices extends AbsAsyncHttpService {

	private SendImgData sendImgData = SendImgData.getInstance();
	private AccountBean mAccountBean;
	private WeiboWeiba mAppSrc = null;
	private String mTextContent;
    private SinaLoginHelper mSinaLoginHelper;
    private JSAutoLogin mJsAutoLogin;
    
	private String TAG = "SendWithAppSrcServices";
	public static final String APP_SRC = "mAppSrc";
	public static final String TEXT_CONTENT = "TEXT_CONTENT";
	
	private ArrayList<String> sendImgList = new ArrayList<String>();
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mSinaLoginHelper = new SinaLoginHelper();
		mAccountBean = BeeboApplication.getInstance().getAccountBean();
		mJsAutoLogin = new JSAutoLogin(getApplicationContext(), mAccountBean);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
        if (intent != null && intent.getExtras() != null){
            mAppSrc = (WeiboWeiba) intent.getExtras().getSerializable(APP_SRC);
            mTextContent = intent.getExtras().getString(TEXT_CONTENT);
        }
		startPicCacheAndSendWeibo();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void startPicCacheAndSendWeibo() {
		ArrayList<String> send = sendImgData.getSendImgs();
		final int count = send.size();

		DevLog.printLog("startPicCacheAndSendWeibo ", "SEND_COUNT: " + count);
		if (count > 0) {
		    for (int i = 0; i < send.size(); i++) {
		        SendBitmapWorkerTask sendBitmapWorkerTask = new SendBitmapWorkerTask(getApplicationContext(),
		                new OnCacheDoneListener() {
		                    @Override
		                    public void onCacheDone(String newFile) {
		                        // TODO Auto-generated method stub
		                    	sendImgList.add(newFile);
		                        DevLog.printLog("startPicCacheAndSendWeibo ", " Should Send Count : " + count + "  current Count :" + sendImgList.size());
		                        if (sendImgList.size() == count) {
		                            sendWeibo(sendImgData, mTextContent);
		                            DevLog.printLog("startPicCacheAndSendWeibo ", " Start Send==========");
		                        }
		                    }
		                });
		        sendBitmapWorkerTask.execute(send.get(i));
		    }
		} else {

		    sendWeibo(sendImgData, mTextContent);
		}
	}
	
    private void sendWeibo(SendImgData sendImgData, String text) {
    	showSendingNotification();
    	
        if (TextUtils.isEmpty(text)) {
            text = getString(R.string.default_text_pic_weibo);
        }

        UserBean userBean = AccountDao.getUserBean(mAccountBean.getUid());
        String url = "";
        if (!TextUtils.isEmpty(userBean.getDomain())) {
            url = "weibo.com/" + userBean.getDomain();
        } else {
            url = "weibo.com/u/" + mAccountBean.getUid();
        }
        WaterMark mark = new WaterMark(mAccountBean.getUsernick(), url);

        dosend( mark, mAppSrc.getCode(), text, sendImgList);
    }
    

    private void dosend(WaterMark mark, final String weiboCode, final String text, List<String> pics) {
        if (pics == null || pics.isEmpty()) {
            sendWeiboWidthPids(weiboCode, text, null);
        } else {
            UploadHelper mUploadHelper = new UploadHelper(getApplicationContext(), getAsyncHttpClient());
            mUploadHelper.uploadFiles(buildMark(mark), pics, new OnUpFilesListener() {

                @Override
                public void onUpSuccess(String pids) {
                	DevLog.printLog("UploadHelper onUpSuccess ", "" + pids);
                    sendWeiboWidthPids(weiboCode, text, pids);
                }

                @Override
                public void onUpLoadFailed() {
                    // TODO Auto-generated method stub
                    startWebLogin();
                }
            }, getCookieIfHave());
        }
    }
    
    public void startWebLogin() {
        Intent intent = new Intent();
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(SendWithAppSrcServices.this, WebViewActivity.class);
        startActivity(intent);
    }
    
    /**
     * @param weiboCode "ZwpYj"
     * @param pids
     */
    protected void sendWeiboWidthPids(String weiboCode, String text, String pids) {
    	String cookie = getCookieIfHave();
        HttpEntity sendEntity = mSinaLoginHelper.sendWeiboEntity(weiboCode, text, cookie, pids);
        asyncHttpPost(Constaces.ADDBLOGURL, mSinaLoginHelper.sendWeiboHeaders(weiboCode, cookie), sendEntity, "application/x-www-form-urlencoded");

    }
    
	private String getCookieIfHave() {
		String cookieInDB = BeeboApplication.getInstance().getAccountBean().getCookieInDB();
		if (!TextUtils.isEmpty(cookieInDB)) {
			return cookieInDB;
		}
		return "";
	}
    
    public String buildMark(WaterMark mark) {
        if (SettingUtils.getEnableWaterMark()) {
            String markpos = SettingUtils.getWaterMarkPos();
            String logo = SettingUtils.isWaterMarkWeiboICONShow() ? "1" : "0";
            String nick = SettingUtils.isWaterMarkScreenNameShow() ? "%40" + mark.getNick() : "";
            String url = SettingUtils.isWaterMarkWeiboURlShow() ? mark.getUrl() : "";
            return "&marks=1&markpos=" + markpos + "&logo=" + logo + "&nick=" + nick + "&url=" + url;
        } else {
            return "&marks=0";
        }
    }
    
    public void clearAppsrc(){
        SharedPreferences appsrcPreferences  = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        appsrcPreferences.edit().remove(Constants.KEY_NAME).remove(Constants.KEY_CODE).commit();
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
    	NotificationUtility.cancel(R.string.sending);
    	
        Notification.Builder builder = new Notification.Builder(SendWithAppSrcServices.this)
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
                    .setTicker(getString(R.string.sending))
                    .setContentTitle(getString(R.string.wait_server_response))
                    .setNumber(100).setProgress(100, 100, false)
                    .setOnlyAlertOnce(true).setOngoing(true).setSmallIcon(R.drawable.upload_white);

            Notification notification = builder.getNotification();

        NotificationUtility.show(notification, R.string.sending);
    }
    
	@Override
	public void onPostSuccess(String arg0) {
		// TODO Auto-generated method stub
		SendWeiboResultBean sb = new Gson().fromJson(arg0, SendWeiboResultBean.class);
		if (sb.isSuccess()) {
			
			deleteSendFile();
			showSuccessfulNotification();
			
			this.stopSelf();
			
			clearAppsrc();
			
			DevLog.printLog(TAG, "发送成功！");
		}else {
			NotificationUtility.cancel(R.string.sending);
			DevLog.printLog(TAG, sb.getCode() + "    " + sb.getMsg());
			if (sb.getMsg().equals("未登录")) {
				mJsAutoLogin.checkUserPassword(mAccountBean.getUname(), mAccountBean.getPwd(), new CheckUserNamePasswordListener() {
					
					@Override
					public void onChecked(String msg) {
						// TODO Auto-generated method stub
						DevLog.printLog("JSAutoLogin onChecked", msg.trim());
						if (TextUtils.isEmpty(msg)) {
							DevLog.printLog("JSAutoLogin onChecked", "startLogin");
							mJsAutoLogin.exejs();
							mJsAutoLogin.setAutoLogInListener(new AutoLogInListener() {
								
								@Override
								public void onAutoLonin(boolean result) {
									// TODO Auto-generated method stub
									startPicCacheAndSendWeibo();
								}
							});
						}
					}
				});
			}
		}
	}
	

    class WeiBaCacheFile implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {
            // TODO Auto-generated method stub
            return filename.startsWith("WEI-");
        }

    }
    
	public void deleteSendFile() {
		SendImgData sid = SendImgData.getInstance();
		sid.clearSendImgs();

		File[] cacheFiles = getExternalCacheDir().listFiles(
				new WeiBaCacheFile());
		for (File file : cacheFiles) {
			Log.d("LIST_CAXCHE", " " + file.getName());
			file.delete();
		}
	}

	@Override
	public void onRequestStart() {
		// TODO Auto-generated method stub
		
	}

}
