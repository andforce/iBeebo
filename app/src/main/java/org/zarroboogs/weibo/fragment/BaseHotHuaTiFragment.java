package org.zarroboogs.weibo.fragment;

import org.apache.http.Header;
import org.zarroboogs.utils.WeiBoURLs;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

public abstract class BaseHotHuaTiFragment extends BaseLoadDataFragment {

	private SharedPreferences mSharedPreference;
	private AsyncHttpClient mAsyncHttoClient = new AsyncHttpClient();

	abstract void onGsidLoadSuccess(String gsid);

	abstract void onGsidLoadFailed(String errorStr);
	
	abstract void onPageSelected();
	
	abstract public ListView getListView();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mSharedPreference = getActivity().getApplicationContext().getSharedPreferences(getActivity().getPackageName()	, Context.MODE_PRIVATE);
		
	}
	
	@Override
	void onLoadDataSucess(String json) {
		// TODO Auto-generated method stub

	}

	@Override
	void onLoadDataFailed(String errorStr) {
		// TODO Auto-generated method stub

	}

	@Override
	void onLoadDataStart() {
		// TODO Auto-generated method stub

	}

	public String getGsid() {
		String gsid = mSharedPreference.getString("gsid", "");
		return gsid;
	}

	public void loadGsid() {
		mAsyncHttoClient.get(WeiBoURLs.GSID, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// TODO Auto-generated method stub
				mSharedPreference.edit().putString("gsid", new String(responseBody)).commit();
				onGsidLoadSuccess(new String(responseBody));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				onGsidLoadFailed(error.getLocalizedMessage());
			}
		});
	}
}
