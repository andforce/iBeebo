
package org.zarroboogs.weibo.ui.actionmenu;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.WriteWeiboActivity;
import org.zarroboogs.weibo.adapter.UserListAdapter;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.FriendshipsDao;
import org.zarroboogs.weibo.fragment.AbstractUserListFragment;

/**
 * User: qii Date: 12-10-11
 */
public class MyFriendSingleChoiceModeListener implements ActionMode.Callback {
    private ListView listView;
    private UserListAdapter adapter;
    private Fragment fragment;
    private ActionMode mode;
    private UserBean bean;

    private UnFollowTask unfollowTask;

    public void finish() {
        if (mode != null)
            mode.finish();

        if (unfollowTask != null)
            unfollowTask.cancel(true);
    }

    public MyFriendSingleChoiceModeListener(ListView listView, UserListAdapter adapter, Fragment fragment, UserBean bean) {
        this.listView = listView;
        this.fragment = fragment;
        this.adapter = adapter;
        this.bean = bean;
    }

    private Activity getActivity() {
        return fragment.getActivity();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (this.mode == null)
            this.mode = mode;

        return true;

    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        menu.clear();

        inflater.inflate(R.menu.contextual_menu_myfriendlistfragment, menu);

        mode.setTitle(bean.getScreen_name());

        return true;

    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        int itemId = item.getItemId();
		if (itemId == R.id.menu_at) {
			Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
			intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getSpecialToken());
			intent.putExtra("content", "@" + bean.getScreen_name());
			intent.putExtra(Constants.ACCOUNT, GlobalContext.getInstance().getAccountBean());
			getActivity().startActivity(intent);
			listView.clearChoices();
			mode.finish();
		} else if (itemId == R.id.menu_unfollow) {
			if (unfollowTask == null || unfollowTask.getStatus() == MyAsyncTask.Status.FINISHED) {
			    unfollowTask = new UnFollowTask();
			    unfollowTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
			}
			listView.clearChoices();
			mode.finish();
		}

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.mode = null;
        listView.clearChoices();
        adapter.notifyDataSetChanged();
        ((AbstractUserListFragment) fragment).setmActionMode(null);

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
            if (!TextUtils.isEmpty(bean.getId())) {
                dao.setUid(bean.getId());
            } else {
                dao.setScreen_name(bean.getScreen_name());
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
        }

        @Override
        protected void onPostExecute(UserBean o) {
            super.onPostExecute(o);
            Toast.makeText(getActivity(), getActivity().getString(R.string.unfollow_successfully), Toast.LENGTH_SHORT)
                    .show();
            adapter.removeItem(bean);
        }
    }

}
