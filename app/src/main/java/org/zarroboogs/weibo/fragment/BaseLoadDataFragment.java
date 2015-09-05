package org.zarroboogs.weibo.fragment;

import org.apache.http.Header;
import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public abstract class BaseLoadDataFragment extends BaseStateFragment {
	private AsyncHttpClient mAsyncHttoClient = new AsyncHttpClient();

	public AsyncHttpClient getAsyncHttpClient() {
		return mAsyncHttoClient;
	}

	abstract void onLoadDataSucess(String json);

	abstract void onLoadDataFailed(String errorStr);

	abstract void onLoadDataStart();

	protected void loadData(String url) {

		onLoadDataStart();

		mAsyncHttoClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// TODO Auto-generated method stub
				onLoadDataSucess(new String(responseBody));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				onLoadDataFailed(error.getLocalizedMessage());
			}
		});
	}

	protected void loadData(String url, RequestParams params) {

		onLoadDataStart();

		mAsyncHttoClient.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// TODO Auto-generated method stub
				String groups = new String(responseBody);
				DevLog.printLog("LOAD_GROUPS: " , groups);
				onLoadDataSucess(groups);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				onLoadDataFailed(error.getLocalizedMessage());
			}
		});
	}

}
