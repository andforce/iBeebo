package org.zarroboogs.util.net;


import lib.org.zarroboogs.utils.net.BroserContent;
import android.content.Context;
import android.os.AsyncTask;

public class LoginWeiboAsyncTask extends AsyncTask<Context, Integer, Boolean> {

	public static interface LoginCallBack {
		public void reSizeWeiboPictures(boolean isSuccess);
	}

	public LoginCallBack mInSuccessListener;

	private String mCookie = "";

	public LoginWeiboAsyncTask(LoginCallBack listener, String cookie) {
		// TODO Auto-generated constructor stub
		this.mInSuccessListener = listener;
		this.mCookie = cookie;
	}

	@Override
	protected Boolean doInBackground(Context... params) {
		// TODO Auto-generated method stub
		BroserContent mBroserContent = BroserContent.getInstance();
		HttpGetHelper mGetHelper = new HttpGetHelper();
		String url = "http://widget.weibo.com/dialog/PublishWeb.php?button=public";
		return mGetHelper.isLogIn(mBroserContent, url, mCookie);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		this.mInSuccessListener.reSizeWeiboPictures(result);
	}

}
