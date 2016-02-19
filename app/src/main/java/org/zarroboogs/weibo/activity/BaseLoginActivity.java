
package org.zarroboogs.weibo.activity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import lib.org.zarroboogs.weibo.login.javabean.RequestResultParser;

import org.zarroboogs.devutils.Constaces;
import org.zarroboogs.http.AsyncHttpHeaders;
import org.zarroboogs.http.AsyncHttpRequest;
import org.zarroboogs.http.AsyncHttpResponse;
import org.zarroboogs.http.AsyncHttpResponseHandler;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.WebViewActivity;
import org.zarroboogs.weibo.bean.WeiboWeiba;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BaseLoginActivity extends SharedPreferenceActivity {
    private RequestResultParser mRequestResultParser;
    private AsyncHttpRequest mAsyncOKHttpClient = new AsyncHttpRequest();
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestResultParser = new RequestResultParser();

        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.send_wei_ing));
        mDialog.setCancelable(false);

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

    private String getCookieIfHave() {
        String cookieInDB = BeeboApplication.getInstance().getAccountBean().getCookieInDB();
        if (!TextUtils.isEmpty(cookieInDB)) {
            return cookieInDB;
        }
        return "";
    }


    public void startWebLogin() {
        Intent intent = new Intent();
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
        intent.setClass(BaseLoginActivity.this, WebViewActivity.class);
        startActivity(intent);
    }

    public interface OnFetchAppSrcListener {
        void onStart();

        void onSuccess(List<WeiboWeiba> appsrcs);

        void onFailure();
    }

    private OnFetchAppSrcListener mFetchAppSrcListener;

    protected void fetchWeiBa(OnFetchAppSrcListener listener) {
        this.mFetchAppSrcListener = listener;
        if (mFetchAppSrcListener != null) {
            mFetchAppSrcListener.onStart();
        }

        String url = Constants.APPSRC;


        AsyncHttpHeaders simpleHeadersBuilder = new AsyncHttpHeaders();
        simpleHeadersBuilder.addAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        simpleHeadersBuilder.addAcceptEncoding("gzip,deflate,sdch");
        simpleHeadersBuilder.addAcceptLanguage("zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        simpleHeadersBuilder.addConnection("keep-alive");
        simpleHeadersBuilder.addHost("appsrc.sinaapp.com");
        simpleHeadersBuilder.addUserAgent(Constaces.User_Agent);
        simpleHeadersBuilder.add("Cache-Control", "no-cache");
        simpleHeadersBuilder.add("Pragma", "no-cache");


        mAsyncOKHttpClient.get(url, simpleHeadersBuilder, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(IOException e) {
                if (mFetchAppSrcListener != null) {
                    mFetchAppSrcListener.onFailure();
                }
            }

            @Override
            public void onSuccess(AsyncHttpResponse response) {
                String resp = response.getBody();
                String jsonString = resp;//.split("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")[1];
                Gson gson = new Gson();

                Type listType = new TypeToken<List<WeiboWeiba>>() {
                }.getType();
                List<WeiboWeiba> mAppsrc = gson.fromJson(jsonString, listType);
                if (mFetchAppSrcListener != null) {
                    mFetchAppSrcListener.onSuccess(mAppsrc);
                }
                hideDialogForWeiBo();
            }
        });
    }
}
