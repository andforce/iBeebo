
package lib.org.zarroboogs.sinalogin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lib.org.zarroboogs.weibo.login.httpclient.RealLibrary;
import lib.org.zarroboogs.weibo.login.httpclient.SinaPreLogin;
import lib.org.zarroboogs.weibo.login.httpclient.UploadHelper;
import lib.org.zarroboogs.weibo.login.httpclient.UploadHelper.OnUpFilesListener;
import lib.org.zarroboogs.weibo.login.javabean.RequestResultParser;
import lib.org.zarroboogs.weibo.login.javabean.PreLoginResult;
import lib.org.zarroboogs.weibo.login.utils.Constaces;
import lib.org.zarroboogs.weibo.login.utils.LogTool;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginBeebo extends Activity {

    private Button mLoginButton;
    private AsyncHttpClient mAsyncHttoClient;
    private CookieStore cookieStore;

    private SinaPreLogin mSinaPreLogin;
    private PreLoginResult mPreLoginResult;

    private JsEvaluator mJsEvaluator;
    private String rsaPwd;

    private RequestResultParser mHelper;

    public static final String UNAME = "86898@163.com";
    public static final String PASSWORD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sina_login_activity_main);

        mJsEvaluator = new JsEvaluator(getApplicationContext());

        mSinaPreLogin = new SinaPreLogin();

        cookieStore = new PersistentCookieStore(getApplicationContext());
        mAsyncHttoClient = new AsyncHttpClient();

        mAsyncHttoClient.setCookieStore(cookieStore);

        mLoginButton = (Button) findViewById(R.id.sinaLoginBtn);
        mLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doPreLogin();

                UploadHelper mUploadHelper = new UploadHelper(getApplicationContext(), mAsyncHttoClient);
                List<String> files = new ArrayList<String>();
                files.add("/sdcard/tencent/zebrasdk/Photoplus.jpg");
                files.add("/sdcard/tencent/zebrasdk/Photoplus~01.jpg");
                files.add("/sdcard/tencent/zebrasdk/Photoplus~02.jpg");
                mUploadHelper.uploadFiles(null, files, new OnUpFilesListener() {

                    @Override
                    public void onUpSuccess(String pids) {
                        LogTool.D("uploadFile pids: " + pids);
                        sendWeibo(pids);
                    }

                    @Override
                    public void onUpLoadFailed() {
                        // TODO Auto-generated method stub

                    }
                });
            }
        });
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constaces.MSG_ENCODE_PWD: {
                    encodePassword(PASSWORD, mPreLoginResult);
                    break;
                }

                case Constaces.MSG_ENCODE_PWD_DONW: {
                    doAfterPreLogin();
                    break;
                }
                case Constaces.MSG_AFTER_LOGIN_DONE: {
                    doLogin();
                    break;
                }
                // case Constaces.MSG_LONGIN_SUCCESS:{
                // sendWeibo();
                // }

                default:
                    break;
            }
        }
    };

    protected void sendWeibo(String pid) {
        HttpEntity sendEntity = mSinaPreLogin.sendWeiboEntity("ZwpYj", SystemClock.uptimeMillis() + "",
                cookieStore.toString(), pid);
        mAsyncHttoClient.post(getApplicationContext(), Constaces.ADDBLOGURL, mSinaPreLogin.sendWeiboHeaders("ZwpYj", null),
                sendEntity,
                "application/x-www-form-urlencoded", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        LogTool.D("sendWeibo   onSuccess" + new String(responseBody));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        LogTool.D("sendWeibo   onFailure" + new String(responseBody));
                    }
                });
    }

    protected void doLogin() {
        mAsyncHttoClient.get(mHelper.getUserPageUrl(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogTool.D("doLogin onSuccess" + " " + new String(responseBody));
                mHandler.sendEmptyMessage(Constaces.MSG_LONGIN_SUCCESS);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void doAfterPreLogin() {
        HttpEntity httpEntity = mSinaPreLogin.afterPreLoginEntity(encodeAccount(UNAME), rsaPwd, null, mPreLoginResult);
        mAsyncHttoClient.post(getApplicationContext(), Constaces.LOGIN_FIRST_URL, mSinaPreLogin.afterPreLoginHeaders(),
                httpEntity, "application/x-www-form-urlencoded", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        String response = null;
                        try {
                            response = new String(responseBody, "GBK");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mHelper = new RequestResultParser(response);
                        if (mHelper.isLogin()) {
                            LogTool.D("doAfterPrelogin onSuccess" + " AfterLogin Success");
                        } else {
                            LogTool.D("doAfterPrelogin onSuccess" + " AfterLogin Failed : " + mHelper.getErrorReason());
                        }
                        mHandler.sendEmptyMessage(Constaces.MSG_AFTER_LOGIN_DONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        LogTool.D("doAfterPrelogin onFailure" + statusCode + new String(responseBody));
                    }
                });
    };

    private String encodeAccount(String account) {
        String encodedString;
        try {
            encodedString = new String(Base64.encodeBase64(URLEncoder.encode(account, "UTF-8").getBytes()));
            String userName = encodedString.replace('+', '-').replace('/', '_');
            return userName;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void encodePassword(String password, PreLoginResult preLonginBean) {
        RealLibrary realLibrary = new RealLibrary(getApplicationContext());
        String js = realLibrary.getRsaJs();

        String pwd = "\"" + password + "\"";
        String servertime = "\"" + preLonginBean.getServertime() + "\"";
        String nonce = "\"" + preLonginBean.getNonce() + "\"";
        String pubkey = "\"" + preLonginBean.getPubkey() + "\"";
        String call = " var rsaPassWord = getRsaPassWord(" + pwd + ", " + servertime + ", " + nonce + ", " + pubkey
                + "); rsaPassWord; ";
        String jsMethod = "getRsaPassWord(" + pwd + ", " + servertime + ", " + nonce + ", " + pubkey + ")";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mJsEvaluator.evaluate("file:///android_asset/ssologin.html", jsMethod, new JsCallback() {

                @Override
                public void onResult(String value) {
                    // TODO Auto-generated method stub
                    Log.d("mJsEvaluator", "[" + value + "]");
                    Message msg = new Message();
                    rsaPwd = value.replace("\"", "");
                    msg.what = Constaces.MSG_ENCODE_PWD_DONW;
                    mHandler.sendMessage(msg);
                }
            });
        } else {
            mJsEvaluator.evaluate(js + call, new JsCallback() {

                @Override
                public void onResult(String value) {

                    // TODO Auto-generated method stub
                    Message msg = new Message();
                    rsaPwd = value;
                    msg.what = Constaces.MSG_ENCODE_PWD_DONW;
                    mHandler.sendMessage(msg);

                }
            });
        }
    }

    private void doPreLogin() {
        long time = new Date().getTime();
        String encodeName = mSinaPreLogin.encodeAccount(UNAME);
        String url = mSinaPreLogin.buildPreLoginUrl(encodeName, Constaces.SSOLOGIN_JS, time + "");
        mAsyncHttoClient.get(getApplicationContext(), url, mSinaPreLogin.preloginHeaders(), null,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        LogTool.D("LoginBeebo " + "onSuccess " + statusCode + new String(responseBody));
                        mPreLoginResult = mSinaPreLogin.buildPreLoginResult(new String(responseBody));
                        mHandler.sendEmptyMessage(Constaces.MSG_ENCODE_PWD);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        LogTool.D("LoginBeebo " + "onFailure " + statusCode + new String(responseBody)
                                + error.getLocalizedMessage());
                    }
                });
    }
}
