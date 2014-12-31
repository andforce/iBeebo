package org.zarroboogs.weibo.asynctask;

import java.lang.ref.WeakReference;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.weibo.activity.BlackMagicActivity;
import org.zarroboogs.weibo.activity.BlackMagicActivity.ProgressFragment;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.BMOAuthDao;
import org.zarroboogs.weibo.dao.OAuthDao;
import org.zarroboogs.weibo.db.task.AccountDBTask;
import org.zarroboogs.weibo.support.utils.Utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class BlackMagicLoginTask extends MyAsyncTask<Void, Void, String[]> {

	private WeiboException e;

	private ProgressFragment progressFragment = ProgressFragment.newInstance();

	private WeakReference<FragmentActivity> mBlackMagicActivityWeakReference;

	private String username;

	private String password;

	private String appkey;

	private String appSecret;


	public BlackMagicLoginTask(FragmentActivity activity, String username, String password, String appkey, String appSecret) {
		mBlackMagicActivityWeakReference = new WeakReference<FragmentActivity>(activity);
		this.username = username;
		this.password = password;
		this.appkey = appkey;
		this.appSecret = appSecret;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressFragment.setAsyncTask(this);

		FragmentActivity activity = mBlackMagicActivityWeakReference.get();
		if (activity != null) {
			progressFragment.show(activity.getSupportFragmentManager(), "");
		}
	}

	@Override
	protected String[] doInBackground(Void... params) {
		try {
			String[] result = new BMOAuthDao(username, password, appkey, appSecret).login();
			UserBean user = new OAuthDao(result[0]).getOAuthUserInfo();
			AccountBean account = new AccountBean();
			account.setAccess_token(result[0]);
			account.setInfo(user);
			account.setExpires_time(System.currentTimeMillis() + Long.valueOf(result[1]) * 1000);
			account.setPwd(password);
			account.setUname(username);
			
			AccountDBTask.addOrUpdateAccount(account, true);
			AppLoggerUtils.e("token expires in " + Utility.calcTokenExpiresInDays(account) + " days");
			return result;
		} catch (WeiboException e) {
			this.e = e;
			cancel(true);
		}
		return null;
	}

	@Override
	protected void onCancelled(String[] s) {
		super.onCancelled(s);
		if (progressFragment != null) {
			progressFragment.dismissAllowingStateLoss();
		}

		FragmentActivity activity = mBlackMagicActivityWeakReference.get();
		if (activity == null) {
			return;
		}

		if (e != null) {
			Toast.makeText(activity, e.getError(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPostExecute(String[] s) {
		super.onPostExecute(s);
		if (progressFragment != null) {
			progressFragment.dismissAllowingStateLoss();
		}

		FragmentActivity activity = mBlackMagicActivityWeakReference.get();
		if (activity == null) {
			return;
		}

		Bundle values = new Bundle();
		values.putString("expires_in", s[1]);
		Intent intent = new Intent();
		intent.putExtras(values);
		activity.setResult(Activity.RESULT_OK, intent);
		activity.finish();
	}
}
