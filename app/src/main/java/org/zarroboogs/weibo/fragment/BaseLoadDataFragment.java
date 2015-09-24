package org.zarroboogs.weibo.fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.zarroboogs.asyncokhttpclient.AsyncOKHttpClient;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

public abstract class BaseLoadDataFragment extends BaseStateFragment {
    private AsyncOKHttpClient mAsyncOKHttpClient = new AsyncOKHttpClient();

    private static final int SUCCESS = 0;
    private static final int FAIL = 1;
    abstract void onLoadDataSucess(String json);

    abstract void onLoadDataFailed(String errorStr);

    abstract void onLoadDataStart();

    protected void loadData(String url) {

        onLoadDataStart();
        mAsyncOKHttpClient.asyncGet(url, new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                Message message = new Message();
                message.obj = e.getLocalizedMessage();
                message.what = FAIL;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.obj = string;
                message.what = SUCCESS;
                mHandler.sendMessage(message);
            }
        });

    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SUCCESS){
                onLoadDataSucess((String) msg.obj);
            } else if (msg.what == FAIL){
                onLoadDataFailed((String) msg.obj);
            }
        }
    };

    protected void loadData(String url, Map<String, String> params) {

        onLoadDataStart();

        mAsyncOKHttpClient.asyncGet(url, params, new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                Message message = new Message();
                message.obj = e.getLocalizedMessage();
                message.what = FAIL;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.obj = string;
                message.what = SUCCESS;
                mHandler.sendMessage(message);
            }
        });

    }


}
