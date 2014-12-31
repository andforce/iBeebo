package org.zarroboogs.weibo.dialogfragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.Toast;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.ErrorCode;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.WriteWeiboActivity;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.FriendshipsDao;
import org.zarroboogs.weibo.db.task.FilterDBTask;

/**
 * User: qii Date: 13-3-10
 */
@SuppressLint("ValidFragment")
public class UserDialog extends DialogFragment {

	private UserBean user;

	public UserDialog() {

	}

	public UserDialog(UserBean user) {
		this.user = user;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("user", user);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			user = savedInstanceState.getParcelable("user");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		CharSequence[] friendItems = { getString(R.string.at_him), getString(R.string.manage_group), getString(R.string.add_to_app_filter),
				getString(R.string.unfollow_him) };
		CharSequence[] strangerItems = { getString(R.string.at_him), getString(R.string.follow_him), getString(R.string.add_to_app_filter) };
		if (user.isFollowing()) {
			builder.setTitle(user.getScreen_name()).setItems(friendItems, new FriendOnClicker());
		} else {
			builder.setTitle(user.getScreen_name()).setItems(strangerItems, new StrangerOnClick());
		}

		return builder.create();
	}

	private class StrangerOnClick implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
				intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getSpecialToken());
				intent.putExtra("content", "@" + user.getScreen_name());
				intent.putExtra(Constants.ACCOUNT, GlobalContext.getInstance().getAccountBean());
				startActivity(intent);
				break;
			case 1:
				new FollowTask().executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
				break;
			case 2:
				FilterDBTask.addFilterKeyword(FilterDBTask.TYPE_USER, user.getScreen_name());
				FilterDBTask.addFilterKeyword(FilterDBTask.TYPE_KEYWORD, user.getScreen_name());
				Toast.makeText(getActivity(), getString(R.string.filter_successfully), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	private class FriendOnClicker implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
				intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getSpecialToken());
				intent.putExtra("content", "@" + user.getScreen_name());
				intent.putExtra(Constants.ACCOUNT, GlobalContext.getInstance().getAccountBean());
				startActivity(intent);
				break;
			case 1:
				ManageGroupDialog manageGroupDialog = new ManageGroupDialog(GlobalContext.getInstance().getGroup(), user.getId());
				manageGroupDialog.show(getFragmentManager(), "");
				break;
			case 2:
				FilterDBTask.addFilterKeyword(FilterDBTask.TYPE_USER, user.getScreen_name());
				FilterDBTask.addFilterKeyword(FilterDBTask.TYPE_KEYWORD, user.getScreen_name());
				Toast.makeText(getActivity(), getString(R.string.filter_successfully), Toast.LENGTH_SHORT).show();
				break;
			case 3:
				new UnFollowTask().executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
				break;
			}
		}
	}

	private class FollowTask extends MyAsyncTask<Void, UserBean, UserBean> {
		WeiboException e;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected UserBean doInBackground(Void... params) {

			FriendshipsDao dao = new FriendshipsDao(GlobalContext.getInstance().getSpecialToken());
			if (!TextUtils.isEmpty(user.getId())) {
				dao.setUid(user.getId());
			} else {
				dao.setScreen_name(user.getScreen_name());
			}

			try {
				return dao.followIt();
			} catch (WeiboException e) {
				AppLoggerUtils.e(e.getError());
				this.e = e;
				cancel(true);
				return null;
			}
		}

		@Override
		protected void onCancelled(UserBean userBean) {
			super.onCancelled(userBean);
		}

		@Override
		protected void onPostExecute(UserBean o) {
			super.onPostExecute(o);
			Toast.makeText(GlobalContext.getInstance(), GlobalContext.getInstance().getString(R.string.follow_successfully), Toast.LENGTH_SHORT).show();
			user.setFollowing(true);
		}
	}

	private class UnFollowTask extends MyAsyncTask<Void, UserBean, UserBean> {
		WeiboException e;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected UserBean doInBackground(Void... params) {

			FriendshipsDao dao = new FriendshipsDao(GlobalContext.getInstance().getSpecialToken());
			if (!TextUtils.isEmpty(user.getId())) {
				dao.setUid(user.getId());
			} else {
				dao.setScreen_name(user.getScreen_name());
			}

			try {
				return dao.unFollowIt();
			} catch (WeiboException e) {
				AppLoggerUtils.e(e.getError());
				this.e = e;
				cancel(true);
				return null;
			}
		}

		@Override
		protected void onCancelled(UserBean userBean) {
			super.onCancelled(userBean);
			if (this.e != null) {
				if (this.e.getError_code() == ErrorCode.NOT_FOLLOWED) {
					user.setFollowing(false);
				}
			}
		}

		@Override
		protected void onPostExecute(UserBean o) {
			super.onPostExecute(o);
			Toast.makeText(GlobalContext.getInstance(), GlobalContext.getInstance().getString(R.string.unfollow_successfully), Toast.LENGTH_SHORT).show();
			user.setFollowing(false);
		}
	}

}
