
package org.zarroboogs.weibo.activity;

import java.lang.reflect.Type;
import java.util.List;

import lib.org.zarroboogs.weibo.login.javabean.RequestResultParser;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.zarroboogs.devutils.Constaces;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

public class BaseLoginActivity extends SharedPreferenceActivity {
    private RequestResultParser mRequestResultParser;

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


    private ResponseHandlerInterface mAutoRepostHandler;

    public void setAutoRepostWeiboListener(ResponseHandlerInterface rhi) {
        this.mAutoRepostHandler = rhi;
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
                String jsonString = resp;//.split("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")[1];
                Gson gson = new Gson();
                
                Type listType = new TypeToken<List<WeiboWeiba>>(){}.getType();
                List<WeiboWeiba> mAppsrc = gson.fromJson(jsonString, listType);
                
//                List<WeiboWeiba> mAppsrc = Arrays.asList(gson.fromJson(jsonString,WeiboWeiba.class));

                if (mFetchAppSrcListener != null) {
                    mFetchAppSrcListener.onSuccess(mAppsrc);
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
