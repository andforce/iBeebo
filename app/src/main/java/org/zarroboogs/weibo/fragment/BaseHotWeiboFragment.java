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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import java.io.IOException;

public abstract class BaseHotWeiboFragment extends AbsBaseTimeLineFragment<MessageListBean> implements MainTimeLineActivity.ScrollableListFragment{

    private MessageListBean mMessageListBean = new MessageListBean();
	private AsyncOKHttpClient mAsyncOKHttpClient = new AsyncOKHttpClient();
    private SharedPreferences mSharedPreference;

	private static final int SUCCESS = 0;
	private static final int FAIL = 1;
    
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
