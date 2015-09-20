package org.zarroboogs.weibo.fragment;

import org.zarroboogs.asyncokhttpclient.AsyncOKHttpClient;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

public abstract class BaseLoadDataFragment extends BaseStateFragment {
    private AsyncOKHttpClient mAsyncOKHttpClient = new AsyncOKHttpClient();


    abstract void onLoadDataSucess(String json);

    abstract void onLoadDataFailed(String errorStr);

    abstract void onLoadDataStart();

    protected void loadData(String url) {

        onLoadDataStart();
        mAsyncOKHttpClient.asyncGet(url, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                onLoadDataFailed(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                onLoadDataSucess(response.body().string());
            }
        });

    }

    protected void loadData(String url, Map<String, String> params) {

        onLoadDataStart();

        mAsyncOKHttpClient.asyncGet(url, params, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                onLoadDataFailed(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                onLoadDataSucess(response.body().string());
            }
        });

    }


}
