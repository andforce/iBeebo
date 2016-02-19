package org.zarroboogs.weibo;


import org.zarroboogs.devutils.AssertLoader;
import org.zarroboogs.devutils.Constaces;
import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.http.AsyncHttpHeaders;
import org.zarroboogs.http.AsyncHttpRequest;
import org.zarroboogs.http.AsyncHttpResponse;
import org.zarroboogs.http.AsyncHttpResponseHandler;
import org.zarroboogs.http.post.AsyncHttpPostFormData;
import org.zarroboogs.injectjs.InjectJS;
import org.zarroboogs.injectjs.JSCallJavaInterface;
import org.zarroboogs.injectjs.InjectJS.OnLoadListener;
import org.zarroboogs.sinaweiboseniorapi.SeniorUrl;
import org.zarroboogs.utils.PatternUtils;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.CheckUserPasswordBean;
import org.zarroboogs.weibo.db.AccountDatabaseManager;
import org.zarroboogs.weibo.db.table.AccountTable;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class JSAutoLogin {

	private Context mContext;
	private WebView mWebView;
	private InjectJS mInjectJS;
	private AccountBean mAccountBean;
	private WeiboWebViewClient mWeiboWebViewClient;

	private AsyncHttpRequest mAsyncHttpRequest = new AsyncHttpRequest();
	private boolean isExecuted = false;
	private AutoLogInListener mListener;
	
	public interface AutoLogInListener{
		void onAutoLonin(boolean result);
	}
	
	public void setAutoLogInListener(AutoLogInListener l){
		this.mListener = l;
	}
	
	
	private CheckUserNamePasswordListener cNamePasswordListener;
	public interface CheckUserNamePasswordListener{
		void onChecked(String msg);
	}
	
	public JSAutoLogin(Context context,AccountBean ab) {

		this.mContext = context;
		this.mAccountBean = ab;
		
		this.mWebView = new WebView(mContext);
        mWeiboWebViewClient = new WeiboWebViewClient();
        mWebView.setWebViewClient(mWeiboWebViewClient);
        
		mInjectJS = new InjectJS(mWebView);

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSaveFormData(true);
		webSettings.setSupportZoom(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
	}
	
	
	
    static String url = "https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fwidget.weibo.com%2Fdialog%2FPublishMobile.php%3Fbutton%3Dpublic";

    
    class JSCallBack extends JSCallJavaInterface{

		@Override
		public void onJSCallJava(String... arg0) {
			DevLog.printLog("onJSCallJava Uname", "" + arg0[0]);
			DevLog.printLog("onJSCallJava Upassword", "" + arg0[1]);
		}
    	
    }


    public void checkUserPassword(String uname, String password , CheckUserNamePasswordListener listener){
    	this.cNamePasswordListener = listener;
    	
        AsyncHttpHeaders simpleHeaders = new AsyncHttpHeaders();
        simpleHeaders.addAccept("*/*");
        simpleHeaders.addAcceptEncoding("gzip,deflate,sdch");
        simpleHeaders.addAcceptLanguage("zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        simpleHeaders.addHost("passport.weibo.cn");
        simpleHeaders.addOrigin("https://passport.weibo.cn");
        simpleHeaders.addReferer("https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fwidget.weibo.com%2Fdialog%2FPublishMobile.php%3Fbutton%3Dpublic");
        simpleHeaders.addContentType("application/x-www-form-urlencoded");
        simpleHeaders.addUserAgent(Constaces.User_Agent);

        AsyncHttpPostFormData formData = new AsyncHttpPostFormData();
        formData.addFormData("username", uname);
        formData.addFormData("password", password);
        formData.addFormData("savestate", "1");
        formData.addFormData("ec", "0");
        formData.addFormData("entry", "mweibo");

        mAsyncHttpRequest.post("https://passport.weibo.cn/sso/login", simpleHeaders, formData, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onSuccess(AsyncHttpResponse response) {
                String r = response.getBody();
                DevLog.printLog("JSAutoLogin onPostSuccess", r);
                CheckUserPasswordBean cb = new Gson().fromJson(r, CheckUserPasswordBean.class);
                if (cNamePasswordListener != null) {
                    cNamePasswordListener.onChecked(cb.getMsg());
                }
            }
        });
    }

    
    
    public void exejs(){
        mInjectJS.addJSCallJavaInterface(new JSCallBack(), "loginName.value","loginPassword.value");
        mInjectJS.replaceDocument("<a href=\"javascript:;\" class=\"btn btnRed\" id = \"loginAction\">登录</a>", 
        		"<a href=\"javascript:doAutoLogIn();\" class=\"btn btnRed\" id = \"loginAction\">登录</a>");
        mInjectJS.removeDocument("<a href=\"javascript:history.go(-1);\" class=\"close\">关闭</a>");
        mInjectJS.removeDocument("<a href=\"http://m.weibo.cn/reg/index?&vt=4&wm=3349&backURL=http%3A%2F%2Fwidget.weibo.com%2Fdialog%2FPublishMobile.php%3Fbutton%3Dpublic\">注册帐号</a><a href=\"http://m.weibo.cn/setting/forgotpwd?vt=4\">忘记密码</a>");
        mInjectJS.removeDocument("<p class=\"label\"><a href=\"https://passport.weibo.cn/signin/other?r=http%3A%2F%2Fwidget.weibo.com%2Fdialog%2FPublishMobile.php%3Fbutton%3Dpublic\">使用其他方式登录</a></p>");
        mInjectJS.injectUrl(url, new AssertLoader(mContext).loadJs("inject.js"), "gb2312");
        

        mInjectJS.setOnLoadListener(new OnLoadListener() {
			
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				if (mAccountBean != null && !TextUtils.isEmpty(mAccountBean.getUname()) && !TextUtils.isEmpty(mAccountBean.getPwd())) {
					mInjectJS.exeJsFunctionWithParam("fillAccount", mAccountBean.getUname(),mAccountBean.getPwd());
	            	if (!isExecuted) {
	            		mInjectJS.exeJsFunction("doAutoLogIn()");
	            		isExecuted = true;
					}
				}
			}
		});
    }
    
    private class WeiboWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	DevLog.printLog("JSAutoLogin shouldOverrideUrlLoading", url);
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        	DevLog.printLog("JSAutoLogin onPageStarted", url);
        	
            if (url.startsWith(SeniorUrl.SeniorUrl_Public)) {
                view.stopLoading();

                CookieManager cookieManager = CookieManager.getInstance();

                String cookie = cookieManager.getCookie(url);

                String uid = "";
                String uname = "";
                
                String gsid = "";
                
                AccountDatabaseManager manager = new AccountDatabaseManager(mContext);
                if (!TextUtils.isEmpty(cookie)) {
                    String[] cookies = cookie.split("; ");
                    for (String string : cookies) {
                        String oneLine = Uri.decode(Uri.decode(string));
                        
                        if (oneLine.contains("SUB=")) {
        					DevLog.printLog("GSID", "" + oneLine);
        					gsid = oneLine.split("SUB=")[1];
        				}
                        
                        String uidtmp = PatternUtils.macthUID(oneLine);
                        if (!TextUtils.isEmpty(uidtmp)) {
                            uid = uidtmp;
                        }
                        uname = PatternUtils.macthUname(oneLine);
                        if (!TextUtils.isEmpty(uname)) {
                            manager.updateAccount(AccountTable.ACCOUNT_TABLE, uid, AccountTable.USER_NAME, uname);
                            manager.updateAccount(AccountTable.ACCOUNT_TABLE, uid, AccountTable.GSID, gsid);
                            BeeboApplication.getInstance().updateAccountBean();
                        }
                    }
                }

                Log.d("Weibo-Cookie", "after for : " + uid);
                if (uid.equals(mAccountBean.getUid())) {
                	if (mListener != null) {
						mListener.onAutoLonin(true);
					}
                    manager.updateAccount(AccountTable.ACCOUNT_TABLE, uid, AccountTable.COOKIE, cookie);
                    BeeboApplication.getInstance().updateAccountBean();
                } else if (!TextUtils.isEmpty(uid)) {
                    mWebView.loadUrl(url);
                }
                
                return;
            }

            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

}
