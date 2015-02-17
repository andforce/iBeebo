
package org.zarroboogs.weibo.activity;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.OAuthDao;
import org.zarroboogs.weibo.db.task.AccountDBTask;
import org.zarroboogs.weibo.support.utils.Utility;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class OAuthActivity extends AbstractAppActivity {

    private WebView webView;

    private ProgressBar mprogressbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauthactivity_layout);
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WeiboWebViewClient());

        mprogressbar = (ProgressBar) findViewById(R.id.oauthProgress);
        
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearCache(true);
    }


    public void refresh() {
//        webView.clearView();
//        webView.loadUrl("about:blank");
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
//
//        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh);
//        iv.startAnimation(rotation);

        webView.loadUrl(getWeiboOAuthUrl());
    }

    private String getWeiboOAuthUrl() {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", WeiBoURLs.APP_KEY);
//        parameters.put("response_type", Constants.TOKEN);
        parameters.put("redirect_uri", WeiBoURLs.SINA_REDIRECT_URL);
        parameters.put("response_type", "code");
//        parameters.put("display", "mobile");
        parameters.put("scope", WeiBoURLs.SINA_SCOPE);
        parameters.put("version", "0030105000");
        
        parameters.put("packagename", "com.eico.weico");
        parameters.put("key_hash", "1e6e33db08f9192306c4afa0a61ad56c");
        
        
        return WeiBoURLs.URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
    }

    private class WeiboWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	mprogressbar.setVisibility(View.VISIBLE);
            if (url.startsWith(WeiBoURLs.DIRECT_URL)) {

                handleRedirectUrl(view, url);
                view.stopLoading();
                return;
            }
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mprogressbar.setVisibility(View.GONE);
            new SinaWeiboErrorDialog().show(getSupportFragmentManager(), "");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!url.equals("about:blank")) {
            }
            mprogressbar.setVisibility(View.GONE);
        }
    }

    private void handleRedirectUrl(WebView view, String url) {
        Bundle values = Utility.parseUrl(url);

        String error = values.getString("error");
        String error_code = values.getString("error_code");

        Intent intent = new Intent();
        intent.putExtras(values);

        if (error == null && error_code == null) {

            String access_token = values.getString("access_token");
            String expires_time = values.getString("expires_in");
            setResult(RESULT_OK, intent);
            new OAuthTask(this).execute(access_token, expires_time);
        } else {
            Toast.makeText(OAuthActivity.this, getString(R.string.you_cancel_login), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Toast.makeText(OAuthActivity.this, getString(R.string.you_cancel_login), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private static class OAuthTask extends MyAsyncTask<String, UserBean, DBResult> {

        private WeiboException e;

        private ProgressFragment progressFragment = ProgressFragment.newInstance();

        private WeakReference<OAuthActivity> oAuthActivityWeakReference;

        private OAuthTask(OAuthActivity activity) {
            oAuthActivityWeakReference = new WeakReference<OAuthActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            progressFragment.setAsyncTask(this);

            OAuthActivity activity = oAuthActivityWeakReference.get();
            if (activity != null) {
//                progressFragment.show(activity.getSupportFragmentManager(), "");
            }

        }

        @Override
        protected DBResult doInBackground(String... params) {

            String token = params[0];
            long expiresInSeconds = Long.valueOf(params[1]);

            try {
                UserBean user = new OAuthDao(token).getOAuthUserInfo();
                AccountBean account = new AccountBean();
                account.setAccess_token(token);
                account.setExpires_time(System.currentTimeMillis() + expiresInSeconds * 1000);
                account.setInfo(user);
                AppLoggerUtils.e("token expires in " + Utility.calcTokenExpiresInDays(account) + " days");
                return AccountDBTask.addOrUpdateAccount(account, false);
            } catch (WeiboException e) {
                AppLoggerUtils.e(e.getError());
                this.e = e;
                cancel(true);
                return null;
            }

        }

        @Override
        protected void onCancelled(DBResult dbResult) {
            super.onCancelled(dbResult);
            if (progressFragment != null) {
                progressFragment.dismissAllowingStateLoss();
            }

            OAuthActivity activity = oAuthActivityWeakReference.get();
            if (activity == null) {
                return;
            }

            if (e != null) {
                Toast.makeText(activity, e.getError(), Toast.LENGTH_SHORT).show();
            }
            activity.webView.loadUrl(activity.getWeiboOAuthUrl());
        }

        @Override
        protected void onPostExecute(DBResult dbResult) {
            if (progressFragment.isVisible()) {
                progressFragment.dismissAllowingStateLoss();
            }
            OAuthActivity activity = oAuthActivityWeakReference.get();
            if (activity == null) {
                return;
            }
            switch (dbResult) {
                case add_successfuly:
                    Toast.makeText(activity, activity.getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    break;
                case update_successfully:
                    Toast.makeText(activity, activity.getString(R.string.update_account_success), Toast.LENGTH_SHORT).show();
                    break;
            }
            activity.finish();

        }
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
        if (isFinishing()) {
            webView.stopLoading();
        }
    }

    public static class ProgressFragment extends DialogFragment {

        MyAsyncTask asyncTask = null;

        public static ProgressFragment newInstance() {
            ProgressFragment frag = new ProgressFragment();
            frag.setRetainInstance(true);
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.oauthing));
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);

            return dialog;
        }

        @Override
        public void onCancel(DialogInterface dialog) {

            if (asyncTask != null) {
                asyncTask.cancel(true);
            }

            super.onCancel(dialog);
        }

        void setAsyncTask(MyAsyncTask task) {
            asyncTask = task;
        }
    }

    public static class SinaWeiboErrorDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.sina_server_error).setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }
    }

    public static enum DBResult {
        add_successfuly, update_successfully
    }
}
