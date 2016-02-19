package org.zarroboogs.weibo.fragment;

import org.zarroboogs.http.AsyncHttpRequest;
import org.zarroboogs.http.AsyncHttpResponse;
import org.zarroboogs.http.AsyncHttpResponseHandler;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;

import java.io.IOException;
import java.util.Map;

public abstract class BaseLoadDataFragment extends BaseStateFragment {
    private AsyncHttpRequest mAsyncOKHttpClient = new AsyncHttpRequest();

    private static final int SUCCESS = 0;
    private static final int FAIL = 1;

    abstract void onLoadDataSucess(String json);

    abstract void onLoadDataFailed(String errorStr);

    abstract void onLoadDataStart();

    protected void loadData(String url) {

        onLoadDataStart();
        mAsyncOKHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(IOException e) {
                onLoadDataFailed(e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(AsyncHttpResponse response) {
                onLoadDataSucess(response.getBody());
            }
        });

    }


    protected void loadData(String url, Map<String, String> params) {

        onLoadDataStart();

        mAsyncOKHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(IOException e) {
                onLoadDataFailed(e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(AsyncHttpResponse response) {
                onLoadDataSucess(response.getBody());
            }
        });
    }


}
