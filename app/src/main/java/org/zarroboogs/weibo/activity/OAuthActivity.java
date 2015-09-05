
package org.zarroboogs.weibo.activity;

import org.zarroboogs.devutils.AssertLoader;
import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.injectjs.InjectJS;
import org.zarroboogs.injectjs.InjectJS.OnLoadListener;
import org.zarroboogs.injectjs.JSCallJavaInterface;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiboOAuthConstances;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.OAuthDao;
import org.zarroboogs.weibo.db.AccountDatabaseManager;
import org.zarroboogs.weibo.db.table.AccountTable;
import org.zarroboogs.weibo.db.task.AccountDao;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


@SuppressLint("SetJavaScriptEnabled")
public class OAuthActivity extends AbstractAppActivity {

    private WebView webView;

    private ProgressBar mprogressbar;
    
    private boolean isAuthPro = false;

    private AccountBean mAccountBean;
    private InjectJS mInjectJS ;
    private Intent resultIntent;
    
    private String uName = "";
    private String uPassword = "";
    	
    
    public static Intent oauthIntent(Activity activity,boolean isHack, AccountBean ab) {
		Intent intent = new Intent(activity, OAuthActivity.class);
		intent.putExtra("isHack", isHack);
		intent.putExtra("accoubt_bean", ab);
		return intent;
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauthactivity_layout);
        this.isAuthPro = getIntent().getBooleanExtra("isHack", false);
        this.mAccountBean = getIntent().getParcelableExtra("accoubt_bean");

        Toolbar mToolbar = ViewUtility.findViewById(this, R.id.oauthToolbar);
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mToolbar.setNavigationOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        
        if (isAuthPro) {
			getSupportActionBar().setTitle("进阶授权");
		}else {
			getSupportActionBar().setTitle("普通授权");
		}
        
        webView = (WebView) findViewById(R.id.webView);
        mInjectJS = new InjectJS(webView);
        
        webView.setWebViewClient(new WeiboWebViewClient());

        mprogressbar = (ProgressBar) findViewById(R.id.oauthProgress);
        
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

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
        webView.clearView();
        webView.loadUrl("about:blank");
        webView.stopLoading();
//        webView.clearCache(true);
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
//
//        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh);
//        iv.startAnimation(rotation);

        String url = getWeiboOAuthUrl();
        webView.loadUrl(url);
        
        DevLog.printLog("OAUTH_ACTIVITY-refresh:", ""+ url);
    }

    private String getWeiboOAuthUrl() {

        Map<String, String> parameters = new HashMap<String, String>();
        
        if (isAuthPro) {
            parameters.put("client_id", WeiboOAuthConstances.HACK_APP_KEY);
            parameters.put("redirect_uri", WeiboOAuthConstances.HACK_DIRECT_URL);
            parameters.put("response_type", "code");
            parameters.put("scope", WeiboOAuthConstances.HACK_SINA_SCOPE);
            parameters.put("version", "0030105000");
            parameters.put("packagename", WeiboOAuthConstances.HACK_PACKAGE_NAME);
            parameters.put("key_hash", WeiboOAuthConstances.HACK_KEY_HASH);
		}else {
			parameters.put("client_id", WeiboOAuthConstances.APP_KEY);
            parameters.put("redirect_uri", WeiboOAuthConstances.DIRECT_URL);
            parameters.put("response_type", "code");
            parameters.put("scope", WeiboOAuthConstances.SINA_SCOPE);
            parameters.put("version", "0030105000");
            parameters.put("packagename", WeiboOAuthConstances.PACKAGE_NAME);
            parameters.put("key_hash", WeiboOAuthConstances.KEY_HASH);

		}
        
        return WeiboOAuthConstances.URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
    }
    
    class JSCallBack extends JSCallJavaInterface{

		@Override
		public void onJSCallJava(String... arg0) {
			// TODO Auto-generated method stub
			DevLog.printLog("onJSCallJava Uname", "" + arg0[0]);
			DevLog.printLog("onJSCallJava Upassword", "" + arg0[1]);
			uName = arg0[0].trim();
			uPassword = arg0[1].trim();
			
			updateUNamePassword(uName, uPassword);

		}

    }
    
	private void updateUNamePassword(String name , String password) {
		if (mAccountBean != null ) {
			DevLog.printLog("onJSCallJava U update Account: ", "+++++++++++++++++++++++ name :" + name + " pwd:" + password);
			AccountDatabaseManager manager = new AccountDatabaseManager(getApplicationContext());
			manager.updateAccount(AccountTable.ACCOUNT_TABLE, mAccountBean.getUid(),
					AccountTable.USER_NAME, name);
			manager.updateAccount(AccountTable.ACCOUNT_TABLE, mAccountBean.getUid(),
					AccountTable.USER_PWD, password);
		}
        BeeboApplication.getInstance().updateAccountBean();
	}

    private class WeiboWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            DevLog.printLog("OAUTH_ACTIVITY-shouldOverrideUrlLoading:", ""+ url);
            if (url.startsWith("https://passport.weibo.cn/signin/login")) {


            	mInjectJS.addJSCallJavaInterface(new JSCallBack(), "loginName.value","loginPassword.value");
            	mInjectJS.setOnLoadListener(new OnLoadListener() {
					
					@Override
					public void onLoad() {
						// TODO Auto-generated method stub
						if (mAccountBean != null) {
							mInjectJS.exeJsFunctionWithParam("fillAccount", mAccountBean.getUname(),mAccountBean.getPwd());
			            	if (isAuthPro && !OAuthActivity.this.isFinishing()) {
			            		mInjectJS.exeJsFunction("doAutoLogIn()");
							}
						}
					}
				});
                //<a href="javascript:;" class="btn btnRed" id = "loginAction">登录</a>
                //<a href="javascript:doAutoLogIn();" class="btn btnRed" id="loginAction">登录</a>
                mInjectJS.replaceDocument("<a href=\"javascript:;\" class=\"btn btnRed\" id = \"loginAction\">登录</a>", 
                		"<a href=\"javascript:doAutoLogIn();\" class=\"btn btnRed\" id = \"loginAction\">登录</a>");
            	mInjectJS.injectUrl(url, new AssertLoader(getApplicationContext()).loadJs("inject.js"), "gb2312");
			
			}else {
	            view.loadUrl(url);
			}
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	mprogressbar.setVisibility(View.VISIBLE);
        	DevLog.printLog("OAUTH_ACTIVITY-onPageStarted:", ""+ url);
        	if (isAuthPro) {
                if (url.startsWith(WeiboOAuthConstances.HACK_DIRECT_URL)) {

                    handleRedirectUrl(view, url);
                    view.stopLoading();
                    return;
                }
			}else {
	            if (url.startsWith(WeiboOAuthConstances.DIRECT_URL)) {

	                handleRedirectUrl(view, url);
	                view.stopLoading();
	                return;
	            }
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
            DevLog.printLog("OAUTH_ACTIVITY-onPageFinished:", ""+ url);
            
            if (!url.equals("about:blank")) {
            }
            mprogressbar.setVisibility(View.GONE);
        }
    }


    private void handleRedirectUrl(WebView view, String url) {
    	if (resultIntent == null) {
            Bundle values = Utility.parseUrl(url);
            resultIntent = new Intent();
            resultIntent.putExtras(values);
		}
    	
    	Bundle values = Utility.parseUrl(url);
        String error = values.getString("error");
        String error_code = values.getString("error_code");


        if (error == null && error_code == null) {
            String access_token = values.getString("access_token");
            String expires_time = values.getString("expires_in");
            new OAuthTask(this, isAuthPro).execute(access_token, expires_time);
        }

//
//    	Bundle values = Utility.parseUrl(url);
//
//        String error = values.getString("error");
//        String error_code = values.getString("error_code");
//        if (error == null && error_code == null) {
//
//        } else {
//            Toast.makeText(OAuthActivity.this, getString(R.string.you_cancel_login), Toast.LENGTH_SHORT).show();
//            finish();
//        }
        
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
        private boolean taskIsAuthPro;
        private OAuthTask(OAuthActivity activity, boolean isAuthPro) {
        	this.taskIsAuthPro = isAuthPro;
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
        	OAuthActivity activity = oAuthActivityWeakReference.get();
            String token = params[0];
            long expiresInSeconds = Long.valueOf(params[1]);

            try {
            	if (taskIsAuthPro) {
            		
            		if (activity.mAccountBean != null) {
            			return AccountDao.updateAccountHackToken(activity.mAccountBean, token, System.currentTimeMillis() + expiresInSeconds * 1000);
					}
                    AccountBean account = BeeboApplication.getInstance().getAccountBean();
                    return AccountDao.updateAccountHackToken(account, token, System.currentTimeMillis() + expiresInSeconds * 1000);
				}else {
	                UserBean user = new OAuthDao(token).getOAuthUserInfo();
	                AccountBean account = new AccountBean();
	                account.setAccess_token(token);
	                account.setExpires_time(System.currentTimeMillis() + expiresInSeconds * 1000);
	                account.setInfo(user);
	                account.setUname(activity.uName);
	                account.setPwd(activity.uPassword);
	                
	                if (activity.mAccountBean == null) {
		                activity.mAccountBean = account;
					}

	                AppLoggerUtils.e("token expires in " + Utility.calcTokenExpiresInDays(account) + " days");
	                return AccountDao.addOrUpdateAccount(account, false);
				}

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
            if (progressFragment != null && progressFragment.isVisible()) {
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
                    //Toast.makeText(activity, activity.getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    break;
                case update_successfully:
                    //Toast.makeText(activity, activity.getString(R.string.update_account_success), Toast.LENGTH_SHORT).show();
                    break;
            }

            if (taskIsAuthPro) {
            	activity.setResult(RESULT_OK, activity.resultIntent);
              activity.finish();
			}
            
            if (!taskIsAuthPro) {
            	activity.isAuthPro = true;
    			activity.refresh();
    			activity.getSupportActionBar().setTitle("进阶授权");
    		}
            
            activity.updateUNamePassword(activity.uName, activity.uPassword);

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

    public enum DBResult {
        add_successfuly, update_successfully
    }
}
