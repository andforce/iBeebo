package org.zarroboogs.weibo.fragment;

import org.zarroboogs.asyncokhttpclient.AsyncOKHttpClient;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.bean.MessageListBean;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.io.IOException;

public abstract class BaseHotWeiboFragment extends AbsBaseTimeLineFragment<MessageListBean> implements MainTimeLineActivity.ScrollableListFragment{

    private MessageListBean mMessageListBean = new MessageListBean();
	private AsyncOKHttpClient mAsyncOKHttpClient = new AsyncOKHttpClient();
    private SharedPreferences mSharedPreference;
    
	@Override
	public MessageListBean getDataList() {
		// TODO Auto-generated method stub
		return mMessageListBean;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mSharedPreference = getActivity().getApplicationContext().getSharedPreferences(getActivity().getPackageName()	, Context.MODE_PRIVATE);
		
	}
	

	@Override
	public void scrollToTop() {
		// TODO Auto-generated method stub
		Utility.stopListViewScrollingAndScrollToTop(getListView());
	}
	
	public String getGsid(){
		String gsid = mSharedPreference.getString("gsid", "");
		return gsid;
	}
	abstract void onLoadDataSucess(String json);

	abstract void onLoadDataFailed(String errorStr);
	
	abstract void onLoadDataStart();
	
	abstract void onGsidLoadSuccess(String gsid);
	
	abstract void onGsidLoadFailed(String errorStr);
	
	abstract void onPageSelected();
	
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
	
	@Override
	protected void onTimeListViewItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void buildListAdapter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNewMsgLoaderSuccessCallback(MessageListBean newValue, Bundle loaderArgs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onOldMsgLoaderSuccessCallback(MessageListBean newValue) {
		// TODO Auto-generated method stub
		
	}

}
