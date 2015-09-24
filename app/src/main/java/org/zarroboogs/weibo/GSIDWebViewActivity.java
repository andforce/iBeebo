package org.zarroboogs.weibo;

import org.zarroboogs.devutils.AssertLoader;
import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.injectjs.InjectJS;
import org.zarroboogs.injectjs.JSCallJavaInterface;
import org.zarroboogs.injectjs.InjectJS.OnLoadListener;
import org.zarroboogs.sinaweiboseniorapi.SeniorUrl;
import org.zarroboogs.weibo.activity.AbstractAppActivity;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.db.AccountDatabaseManager;
import org.zarroboogs.weibo.db.table.AccountTable;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class GSIDWebViewActivity extends AbstractAppActivity implements IWeiboClientListener {

    private WebView mWebView;

    private View progressBar;

    private AccountBean mAccountBean;

    private InjectJS mInjectJS ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.webAuthToolbar);
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mWebView.stopLoading();
                finish();
            }
        });
        
        
        mAccountBean = getIntent().getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        if (mAccountBean == null) {
            mAccountBean = BeeboApplication.getInstance().getAccountBean();
        }

        initView();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    public void initView() {
        mWebView = (WebView) findViewById(R.id.webview);
        
        mInjectJS = new InjectJS(mWebView);
        
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.requestFocus();

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSaveFormData(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        progressBar = findViewById(R.id.show_request_progress_bar);

    }

    class JSCallBack extends JSCallJavaInterface{

		@Override
		public void onJSCallJava(String... arg0) {
			DevLog.printLog("onJSCallJava Uname", "" + arg0[0]);
			DevLog.printLog("onJSCallJava Upassword", "" + arg0[1]);
		}
    	
    }
    public void initData() {
        WeiboWebViewClient mWeiboWebViewClient = new WeiboWebViewClient();
        mWebView.setWebViewClient(mWeiboWebViewClient);
        
        mInjectJS.addJSCallJavaInterface(new JSCallBack(), "loginName.value","loginPassword.value");
        //<a href="javascript:;" class="btn btnRed" id = "loginAction">登录</a>
        //<a href="javascript:doAutoLogIn();" class="btn btnRed" id="loginAction">登录</a>
        mInjectJS.replaceDocument("<a href=\"javascript:;\" class=\"btn btnRed\" id = \"loginAction\">登录</a>", 
        		"<a href=\"javascript:doAutoLogIn();\" class=\"btn btnRed\" id = \"loginAction\">登录</a>");
        mInjectJS.removeDocument("<a href=\"javascript:history.go(-1);\" class=\"close\">关闭</a>");
        mInjectJS.removeDocument("<a href=\"http://m.weibo.cn/reg/index?&vt=4&wm=3349&backURL=http%3A%2F%2Fm.weibo.cn\">注册帐号</a><a href=\"http://m.weibo.cn/setting/forgotpwd?vt=4\">忘记密码</a>");

        mInjectJS.removeDocument("<a href=\"https://passport.weibo.cn/signin/other?r=http%3A%2F%2Fm.weibo.cn\">第三方帐号</a>");
        mInjectJS.removeDocument("<span style=\"margin:0 5px;color:#c8c8c8\">|</span>");
        mInjectJS.removeDocument("<a id=\"logByAppAuth\" href=\"javascript:;\" weibo-data-onekey-param=\"sinaweibo://browser?url=http%3A%2F%2Fm.weibo.cn\" weibo-data-os-name=\"\">一键登录</a>");
        mInjectJS.removeDocument("使用其他方式登录");
        mInjectJS.removeDocument("<a href=\"http://m.weibo.cn/reg/index?&vt=4&wm=3349&backURL=http%3A%2F%2Fm.weibo.cn\">注册帐号</a><a href=\"https://passport.weibo.cn/forgot/forgot?entry=wapsso&from=0\">忘记密码</a>");
        mInjectJS.injectUrl(SeniorUrl.SeniorUrl_SeniorLogin, new AssertLoader(this).loadJs("inject.js"), "gb2312");
        

        mInjectJS.setOnLoadListener(new OnLoadListener() {
			
			@Override
			public void onLoad() {
				if (mAccountBean != null && !TextUtils.isEmpty(mAccountBean.getUname()) && !TextUtils.isEmpty(mAccountBean.getPwd())) {
					mInjectJS.exeJsFunctionWithParam("fillAccount", mAccountBean.getUname(),mAccountBean.getPwd());
				}
			}
		});
    }


    private void showProgress() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void hideProgress() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onCancel() {
        Toast.makeText(this, "Auth cancel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete(Bundle values) {
        CookieManager cookieManager = CookieManager.getInstance();

        String cookie = cookieManager.getCookie(SeniorUrl.SeniorUrl_SeniorLogin);
        String pubCookie = cookieManager.getCookie(SeniorUrl.SeniorUrl_Public);

        String passPortCookie = cookieManager.getCookie("https://passport.weibo.cn");

        DevLog.printLog("Weibo-CookieStr cookie: ", cookie);
        DevLog.printLog("Weibo-CookieStr pubCookie: ", pubCookie);
        DevLog.printLog("Weibo-CookieStr passPortCookie: ", passPortCookie);

        String uid = "";
        String gsid = "";
        
        if (!TextUtils.isEmpty(cookie)) {
            String[] cookies = cookie.split("; ");
            for (String string : cookies) {
                String oneLine = Uri.decode(Uri.decode(string));
                
                if (oneLine.contains("SUB=")) {
					DevLog.printLog("GSID", "" + oneLine);
					gsid = oneLine.split("SUB=")[1];
				}
                
                if (oneLine.contains("SSOLoginState")){
                    uid = oneLine.split("=")[1];
                    DevLog.printLog("GSID-UID", uid);
                }
            }
        }

        if (!TextUtils.isEmpty(uid)) {
            AccountDatabaseManager manager = new AccountDatabaseManager(getApplicationContext());
            manager.updateAccount(AccountTable.ACCOUNT_TABLE, mAccountBean.getUid(), AccountTable.COOKIE, pubCookie);
            manager.updateAccount(AccountTable.ACCOUNT_TABLE, mAccountBean.getUid(), AccountTable.GSID, gsid);
            BeeboApplication.getInstance().updateAccountBean();
            
            finish();
        } else if (!TextUtils.isEmpty(uid)) {
            Toast.makeText(getApplicationContext(), "请登录昵称是[" + mAccountBean.getUsernick() + "]的微博！", Toast.LENGTH_LONG)
                    .show();
            mWebView.loadUrl(SeniorUrl.SeniorUrl_SeniorLogin);
        }
    }

    @Override
    public void onWeiboException(WeiboException e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private class WeiboWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            showProgress();
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            showProgress();
            if (url.startsWith(SeniorUrl.SeniorUrl_Public)) {
                view.stopLoading();
                handleRedirectUrl(view, url, GSIDWebViewActivity.this);
                return;
            }

            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            hideProgress();

            super.onPageFinished(view, url);
        }

        private boolean handleRedirectUrl(WebView view, String url, IWeiboClientListener listener) {
            listener.onComplete(null);

            return false;
        }
    }


}