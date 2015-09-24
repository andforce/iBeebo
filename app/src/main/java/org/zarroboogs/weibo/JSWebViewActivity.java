
package org.zarroboogs.weibo;


import org.zarroboogs.devutils.AssertLoader;
import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.injectjs.InjectJS;
import org.zarroboogs.injectjs.JSCallJavaInterface;
import org.zarroboogs.injectjs.InjectJS.OnLoadListener;
import org.zarroboogs.sinaweiboseniorapi.SeniorUrl;
import org.zarroboogs.utils.PatternUtils;
import org.zarroboogs.weibo.activity.AbstractAppActivity;
import org.zarroboogs.weibo.activity.OAuthActivity;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class JSWebViewActivity extends AbstractAppActivity implements IWeiboClientListener {

    private WebView mWebView;

    private View progressBar;

    private WeiboWebViewClient mWeiboWebViewClient;

    private AccountBean mAccountBean;

    private Toolbar mToolbar;

    private InjectJS mInjectJS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);

        mToolbar = (Toolbar) findViewById(R.id.webAuthToolbar);

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
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
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

    class JSCallBack extends JSCallJavaInterface {

        @Override
        public void onJSCallJava(String... arg0) {
            // TODO Auto-generated method stub
            DevLog.printLog("onJSCallJava Uname", "" + arg0[0]);
            DevLog.printLog("onJSCallJava Upassword", "" + arg0[1]);
        }

    }

    public void initData() {
        mWeiboWebViewClient = new WeiboWebViewClient();
        mWebView.setWebViewClient(mWeiboWebViewClient);
//        mWebView.loadUrl(getAuthoUrl());

        if (true) {

            mInjectJS.addJSCallJavaInterface(new JSCallBack(), "loginName.value", "loginPassword.value");
            mInjectJS.replaceDocument("<a href=\"javascript:;\" class=\"btn btnRed\" id = \"loginAction\">登录</a>",
                    "<a href=\"javascript:doAutoLogIn();\" class=\"btn btnRed\" id = \"loginAction\">登录</a>");
            mInjectJS.removeDocument("<a href=\"javascript:history.go(-1);\" class=\"close\">关闭</a>");
            mInjectJS.removeDocument("<a href=\"http://m.weibo.cn/reg/index?&vt=4&wm=3349&backURL=http%3A%2F%2Fwidget.weibo.com%2Fdialog%2FPublishMobile.php%3Fbutton%3Dpublic\">注册帐号</a><a href=\"http://m.weibo.cn/setting/forgotpwd?vt=4\">忘记密码</a>");
            mInjectJS.removeDocument("<p class=\"label\"><a href=\"https://passport.weibo.cn/signin/other?r=http%3A%2F%2Fwidget.weibo.com%2Fdialog%2FPublishMobile.php%3Fbutton%3Dpublic\">使用其他方式登录</a></p>");
            mInjectJS.injectUrl(getAuthoUrl(), new AssertLoader(this).loadJs("inject.js"), "gb2312");


            mInjectJS.setOnLoadListener(new OnLoadListener() {

                @Override
                public void onLoad() {
                    // TODO Auto-generated method stub
                    if (mAccountBean != null && !TextUtils.isEmpty(mAccountBean.getUname()) && !TextUtils.isEmpty(mAccountBean.getPwd())) {
                        mInjectJS.exeJsFunctionWithParam("fillAccount", mAccountBean.getUname(), mAccountBean.getPwd());
                        if (!JSWebViewActivity.this.isFinishing()) {
                            mInjectJS.exeJsFunction("doAutoLogIn()");
                        }
                    }
                }
            });


        }
    }

    class JSInterface {

        public JSInterface() {
            super();
            // TODO Auto-generated constructor stub
        }

        @JavascriptInterface
        public void saveAccountInfo(String uname, String upassword) {
            DevLog.printLog("saveAccountInfo ", "uname: " + uname + "  password:" + upassword);
        }
    }

    static String url = "https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fwidget.weibo.com%2Fdialog%2FPublishMobile.php%3Fbutton%3Dpublic";

    public String getAuthoUrl() {
        return url;
    }

    private void showProgress() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void hideProgress() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Auth cancel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete(Bundle values) {
        // TODO Auto-generated method stub
        CookieManager cookieManager = CookieManager.getInstance();

        String cookie = cookieManager.getCookie(url);

//         setWeiboCookie(CookieStr);
        String uid = "";
        String uname = "";
        AccountDatabaseManager manager = new AccountDatabaseManager(getApplicationContext());
        if (true) {
            String[] cookies = cookie.split("; ");
            for (String string : cookies) {
                // Log.d("Weibo-Cookie", "" + Uri.decode(Uri.decode(string)));
                String oneLine = Uri.decode(Uri.decode(string));
                String uidtmp = PatternUtils.macthUID(oneLine);
                if (!TextUtils.isEmpty(uidtmp)) {
                    uid = uidtmp;
                }
                uname = PatternUtils.macthUname(oneLine);
                // Log.d("Weibo-Cookie", "" + uid);
                // Log.d("Weibo-Cookie", "" + uname);
                // Log.d("Weibo-Cookie", "in db : uid = " + mAccountBean.getUid());

                if (!TextUtils.isEmpty(uname)) {
                    manager.updateAccount(AccountTable.ACCOUNT_TABLE, uid, AccountTable.USER_NAME, uname);
                }
            }
        }

        Log.d("Weibo-Cookie", "after for : " + uid);
        if (uid.equals(mAccountBean.getUid())) {
            manager.updateAccount(AccountTable.ACCOUNT_TABLE, uid, AccountTable.COOKIE, cookie);
            BeeboApplication.getInstance().updateAccountBean();
            finish();
        } else if (!TextUtils.isEmpty(uid)) {
            Toast.makeText(getApplicationContext(), "请登录昵称是[" + mAccountBean.getUsernick() + "]的微博！", Toast.LENGTH_LONG)
                    .show();
            mWebView.loadUrl(url);
        }
    }

    @Override
    public void onWeiboException(WeiboException e) {
        // TODO Auto-generated method stub
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
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            showProgress();
            if (url.startsWith(SeniorUrl.SeniorUrl_Public)) {
                view.stopLoading();
                handleRedirectUrl(view, url, JSWebViewActivity.this);
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
