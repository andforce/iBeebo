
package org.zarroboogs.weibo.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.org.zarroboogs.weibo.login.httpclient.RealLibrary;
import lib.org.zarroboogs.weibo.login.httpclient.SinaPreLogin;
import lib.org.zarroboogs.weibo.login.httpclient.UploadHelper;
import lib.org.zarroboogs.weibo.login.httpclient.UploadHelper.OnUpFilesListener;
import lib.org.zarroboogs.weibo.login.httpclient.WaterMark;
import lib.org.zarroboogs.weibo.login.javabean.DoorImageAsyncTask;
import lib.org.zarroboogs.weibo.login.javabean.PreLoginResult;
import lib.org.zarroboogs.weibo.login.javabean.RequestResultParser;
import lib.org.zarroboogs.weibo.login.javabean.SendResultBean;
import lib.org.zarroboogs.weibo.login.javabean.DoorImageAsyncTask.OnDoorOpenListener;
import lib.org.zarroboogs.weibo.login.utils.Constaces;
import lib.org.zarroboogs.weibo.login.utils.LogTool;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.WebViewActivity;
import org.zarroboogs.weibo.bean.WeibaGson;
import org.zarroboogs.weibo.bean.WeibaTree;
import org.zarroboogs.weibo.bean.WeiboWeiba;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

public class BaseLoginActivity extends SharedPreferenceActivity {
    private static final String TAG = "Beebo_Login: ";
    private SinaPreLogin mSinaPreLogin;
    private PreLoginResult mPreLoginResult;

    private JsEvaluator mJsEvaluator;
    private String rsaPwd;

    private RequestResultParser mRequestResultParser;

    private String mUserName;
    private String mPassword;
    private String mDoor = null;

    private WaterMark mWaterMark;
    private String mWeibaCode;
    private String mWeiboText;
    private List<String> mPics;
    private ProgressDialog mDialog;
    private AlertDialog mDoorAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJsEvaluator = new JsEvaluator(getApplicationContext());

        mSinaPreLogin = new SinaPreLogin();

        mRequestResultParser = new RequestResultParser();

        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.send_wei_ing));
        mDialog.setCancelable(false);

        Builder builder = new Builder(BaseLoginActivity.this);
        mDoorAlertDialog = builder.create();

    }

    public void showDialogForWeiBo() {
        if (!mDialog.isShowing()) {
            mDialog.show();
        }

    }

    public void hideDialogForWeiBo() {
        mDialog.cancel();
        mDialog.hide();
    }

    public RequestResultParser getRequestResultParser() {
        return mRequestResultParser;
    }

    public void executeSendWeibo(String uname, String upwd, WaterMark mark, final String weiboCode, final String text,
            List<String> pics) {
        this.mUserName = uname;
        this.mPassword = upwd;

        this.mWaterMark = mark;
        this.mWeibaCode = weiboCode;
        this.mWeiboText = text;
        this.mPics = pics;
        LogTool.D("sendWeibo   start" + " name:" + uname + "   password:" + upwd + "  weiba:" + weiboCode);

        // doPreLogin(this.mUserName, this.mPassword);
        dosend(mark, weiboCode, text, pics);

    }

    private void dosend(WaterMark mark, final String weiboCode, final String text, List<String> pics) {
        if (pics == null || pics.isEmpty()) {
            sendWeiboWidthPids(weiboCode, text, null);
            // sendWeiboWidthPids("ZwpYj", "Test: " + SystemClock.uptimeMillis() + "", null);
            LogTool.D(TAG + "uploadFile     Not Upload");
        } else {
            LogTool.D(TAG + "uploadFile    upload");
            UploadHelper mUploadHelper = new UploadHelper(getApplicationContext(), getAsyncHttpClient());
            mUploadHelper.uploadFiles(buildMark(mark), pics, new OnUpFilesListener() {

                @Override
                public void onUpSuccess(String pids) {
                    LogTool.D(TAG + " UploadPic:  [onUpSuccess] " + pids);
                    sendWeiboWidthPids(weiboCode, text, pids);
                }

                @Override
                public void onUpLoadFailed() {
                    // TODO Auto-generated method stub
                    startAutoPreLogin(mUserName, mPassword);
                    LogTool.D(TAG + " UploadPic:  [onUpLoadFailed] doPreLogin");
                }
            });
        }
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

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
	            case Constaces.MSG_ENCODE_PWD_ERROR:{
	            	if (mLoginHandler != null) {
		            	((AsyncHttpResponseHandler)mLoginHandler).onFailure(0, null, null, null);
					}
	            	break;
	            }
                case Constaces.MSG_ENCODE_PWD: {
                    encodePassword(mPassword, mPreLoginResult);
                    break;
                }
                case Constaces.MSG_SHOW_DOOR: {
                    showDoorDialog();
                    break;
                }

                case Constaces.MSG_ENCODE_PWD_DONW: {

                    if (mPreLoginResult.getShowpin() == 1) {
                        LogTool.D(TAG + "   需要验证码");
                        showDoorDialog();
                    } else {
                        LogTool.D(TAG + "   不不需要验证码");
                        doAutoAfterPreLogin(mPreLoginResult, null);
                    }

                    break;
                }
                case Constaces.MSG_AFTER_LOGIN_DONE: {
                    doLogin();
                    break;
                }
                case Constaces.MSG_LONGIN_SUCCESS: {
                    // sendWeibo("");
                    // dosend(mWaterMark, mWeibaCode, mWeiboText, mPics);
                    // sendWeiboWidthPids("ZwpYj", "Test: " + SystemClock.uptimeMillis() + "",
                    // null);
                    break;
                }

                default:
                    break;
            }
        }
    };

    protected void sendWeibo(String pid) {
    	String cookie = getCookieIfHave();
		LogTool.D(TAG + "sendWeibo Cookie:     " + cookie);
        HttpEntity sendEntity = mSinaPreLogin.sendWeiboEntity("ZwpYj", SystemClock.uptimeMillis() + "", cookie, pid);
        getAsyncHttpClient().post(getApplicationContext(), Constaces.ADDBLOGURL, mSinaPreLogin.sendWeiboHeaders("ZwpYj", cookie),
                sendEntity,
                "application/x-www-form-urlencoded", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        SendResultBean sendResultBean = mRequestResultParser.parse(responseBody, SendResultBean.class);
                        LogTool.D("sendWeibo   onSuccess" + sendResultBean.getMsg());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        LogTool.D("sendWeibo   onFailure" + error.getLocalizedMessage());
                    }
                });
    }

    /**
     * @param weiboCode "ZwpYj"
     * @param pid
     */
    protected void sendWeiboWidthPids(String weiboCode, String text, String pids) {
    	String cookie = getCookieIfHave();
		LogTool.D(TAG + "sendWeiboWidthPids Cookie:     " + cookie);
        HttpEntity sendEntity = mSinaPreLogin.sendWeiboEntity(weiboCode, text, cookie, pids);
        getAsyncHttpClient().post(getApplicationContext(), Constaces.ADDBLOGURL, mSinaPreLogin.sendWeiboHeaders(weiboCode, cookie),
                sendEntity,
                "application/x-www-form-urlencoded", this.mAutoSendWeiboListener);
    }

	private String getCookieIfHave() {
		String cookieInDB = GlobalContext.getInstance().getAccountBean().getCookieInDB();
		if (!TextUtils.isEmpty(cookieInDB)) {
			return cookieInDB;
		}
		return "";
	}

    private ResponseHandlerInterface mAutoSendWeiboListener;

    public void setAutoSendWeiboListener(ResponseHandlerInterface rhi) {
        this.mAutoSendWeiboListener = rhi;
    }

    public void repostWeibo(String app_src, String content, String cookie, String mid) {
    	cookie = getCookieIfHave();
		
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
        LogTool.D("RepostWeiboMainActivity : repost-content: " + content);
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
        nvs.add(new BasicNameValuePair("is_comment", "1"));
        nvs.add(new BasicNameValuePair("_t", "0"));

        UrlEncodedFormEntity repostEntity = null;
        try {
            repostEntity = new UrlEncodedFormEntity(nvs, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        getAsyncHttpClient().post(getApplicationContext(), Constaces.REPOST_WEIBO, repostHeaders, repostEntity,
                "application/x-www-form-urlencoded", mAutoRepostHandler);
    }

    private ResponseHandlerInterface mAutoRepostHandler;

    public void setAutoRepostWeiboListener(ResponseHandlerInterface rhi) {
        this.mAutoRepostHandler = rhi;
    }

    private void doLogin() {
        getAsyncHttpClient().get(mRequestResultParser.getUserPageUrl(), mLoginHandler);
    }

    private ResponseHandlerInterface mLoginHandler;;

    public void setAutoLogInLoginListener(ResponseHandlerInterface rhi) {
        this.mLoginHandler = rhi;
    }

    private void hideDoorDialog() {
        mDoorAlertDialog.hide();
    }

    private void showDoorDialog() {

        mDoorAlertDialog.show();
        mDoorAlertDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mDoorAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mDoorAlertDialog.getWindow().setContentView(R.layout.door_img_dialog_layout);

        executeDoor(mPreLoginResult, (ImageView) mDoorAlertDialog.findViewById(R.id.doorImageView));

        final EditText doorEdittext = (EditText) mDoorAlertDialog.findViewById(R.id.doorEditText);
        Button checkButton = (Button) mDoorAlertDialog.findViewById(R.id.doorCheckBtn);
        checkButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialogForWeiBo();
                doAutoAfterPreLogin(mPreLoginResult, doorEdittext.getText().toString().trim());
                hideDoorDialog();
            }
        });
    }

    private void executeDoor(PreLoginResult preLoginResult, final ImageView iv) {
        DoorImageAsyncTask doorImageAsyncTask = new DoorImageAsyncTask();
        doorImageAsyncTask.setOnDoorOpenListener(new OnDoorOpenListener() {

            @Override
            public void onDoorOpen(android.graphics.Bitmap result) {
                // TODO Auto-generated method stub
                iv.setImageBitmap(result);
            }
        });
        doorImageAsyncTask.execute(preLoginResult.getPcid());
    }

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

        mHandler.sendEmptyMessageDelayed(Constaces.MSG_ENCODE_PWD_ERROR, 5 * 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mJsEvaluator.evaluate("file:///android_asset/ssologin.html", jsMethod, new JsCallback() {

                @Override
                public void onResult(String value) {
                    // TODO Auto-generated method stub
                	mHandler.removeMessages(Constaces.MSG_ENCODE_PWD_ERROR);
                	
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
                	mHandler.removeMessages(Constaces.MSG_ENCODE_PWD_ERROR);
                	
                    // TODO Auto-generated method stub
                    Message msg = new Message();
                    rsaPwd = value;
                    msg.what = Constaces.MSG_ENCODE_PWD_DONW;
                    mHandler.sendMessage(msg);

                }
            });
        }
    }

    /**
     * 登陆的第一步，主要获取PreLoginResult中的一些参数 下一步是根据参数获取加密之后的密码 之后是afterPrelogin
     * @param uname
     * @param upwd
     */
    public void startAutoPreLogin(String uname, String upwd) {
        this.mUserName = uname;
        this.mPassword = upwd;

        long time = new Date().getTime();
        String encodeName = mSinaPreLogin.encodeAccount(mUserName);
        String url = mSinaPreLogin.buildPreLoginUrl(encodeName, Constaces.SSOLOGIN_JS, time + "");
        getAsyncHttpClient().get(getApplicationContext(), url, mSinaPreLogin.preloginHeaders(), null,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        LogTool.D(TAG + " PreLogin:  [Success] " + new String(responseBody));
                        mPreLoginResult = mSinaPreLogin.buildPreLoginResult(new String(responseBody));
                        mHandler.sendEmptyMessage(Constaces.MSG_ENCODE_PWD);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        LogTool.D(TAG + " PreLogin:  [Failure] " + error.getLocalizedMessage());
                    }
                });
    }

    private void doAutoAfterPreLogin(PreLoginResult preLoginResult, String door) {
        HttpEntity httpEntity = mSinaPreLogin.afterPreLoginEntity(encodeAccount(mUserName), rsaPwd, door, preLoginResult);
        getAsyncHttpClient().post(getApplicationContext(), Constaces.LOGIN_FIRST_URL, mSinaPreLogin.afterPreLoginHeaders(),
                httpEntity, "application/x-www-form-urlencoded", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        String response = null;
                        String result = null;
                        try {
                            response = new String(responseBody, "GBK");
                            result = URLDecoder.decode(response, "GBK");
                            result = URLDecoder.decode(response, "GBK");

                            String[] s = result.split("\n\t\t");
                            for (String string : s) {
                                LogTool.D(TAG + " 网络正常返回，结果是: " + string);
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mRequestResultParser = new RequestResultParser(response);

                        LogTool.D(TAG + " 网络正常返回，ReplaceLocation：  " + mRequestResultParser.getLocationReplace());

                        if (mRequestResultParser.isLogin()) {
                            LogTool.D(TAG + " 网络正常返回，并成功登陆");
                            mHandler.sendEmptyMessage(Constaces.MSG_AFTER_LOGIN_DONE);
                        } else {
                            if (mRequestResultParser.getErrorReason().contains("验证码")) {
                                LogTool.D(TAG + " 网络正常返回，登陆失败，需要验证码！");
                                hideDialogForWeiBo();
                                showDoorDialog();
                            } else {
                                hideDialogForWeiBo();
                                startWebLogin();
                                if (mRequestResultParser.getErrorReason().equals("抱歉！登录失败，请稍候再试")) {
                					
                				}
                                LogTool.D(TAG + " 网络正常返回，登陆失败，原因是：" + mRequestResultParser.getErrorReason());
                            }
                            Toast.makeText(getApplicationContext(), mRequestResultParser.getErrorReason(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        LogTool.D(TAG + " AfterPreLogin:  [onFailure] " + error.getLocalizedMessage());
                        startWebLogin();

                    }
                });
    };

    public void startWebLogin() {
        Intent intent = new Intent();
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
        intent.setClass(BaseLoginActivity.this, WebViewActivity.class);
        startActivity(intent);
    }
    public interface OnFetchAppSrcListener {
        public void onStart();

        public void onSuccess(List<WeiboWeiba> appsrcs);

        public void onFailure();
    }

    private OnFetchAppSrcListener mFetchAppSrcListener;

    protected void fetchWeiBa(OnFetchAppSrcListener listener) {
        this.mFetchAppSrcListener = listener;
        if (mFetchAppSrcListener != null) {
            mFetchAppSrcListener.onStart();
        }

        showDialogForWeiBo();
        String url = "http://appsrc.sinaapp.com/";
        Header[] srcHeaders = new Header[] {
                new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
                new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"),
                new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"),
                new BasicHeader("Cache-Control", "no-cache"),
                new BasicHeader("Connection", "keep-alive"),
                new BasicHeader("Host", "appsrc.sinaapp.com"),
                new BasicHeader("Pragma", "no-cache"),
                new BasicHeader("User-Agent", Constaces.User_Agent),
        };
        getAsyncHttpClient().get(getApplicationContext(), url, srcHeaders, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp = new String(responseBody);
                String jsonString = resp.split("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")[1];
                Gson gson = new Gson();
                WeibaGson weibaGson = gson.fromJson(jsonString, WeibaGson.class);
                List<WeibaTree> weibaTrees = weibaGson.getData();

                List<WeiboWeiba> weibas = new ArrayList<WeiboWeiba>();
                for (WeibaTree weibaTree : weibaTrees) {
                    weibas.addAll(weibaTree.getData());
                }

                if (mFetchAppSrcListener != null) {
                    mFetchAppSrcListener.onSuccess(weibas);
                }
                hideDialogForWeiBo();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (mFetchAppSrcListener != null) {
                    mFetchAppSrcListener.onFailure();
                }
            }
        });
    }
}
