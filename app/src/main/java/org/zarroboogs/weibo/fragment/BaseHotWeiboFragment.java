package org.zarroboogs.weibo.fragment;

import org.apache.http.Header;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.bean.MessageListBean;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

public abstract class BaseHotWeiboFragment extends AbsBaseTimeLineFragment<MessageListBean> implements MainTimeLineActivity.ScrollableListFragment{

    private MessageListBean mMessageListBean = new MessageListBean();
    private AsyncHttpClient mAsyncHttoClient = new AsyncHttpClient();
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

                if (responseBody != null){
                    Utility.printLongLog("loadData -> onFailure ", statusCode + new String(responseBody));
                } else {
                    Utility.printLongLog("loadData -> onFailure ", statusCode + "");
                }


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
