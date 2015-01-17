
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.DMActivity;
import org.zarroboogs.weibo.activity.DMSelectUserActivity;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.adapter.DMUserListAdapter;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.data.DMUserListBean;
import org.zarroboogs.weibo.db.task.DMDBTask;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.loader.DMUserLoader;
import org.zarroboogs.weibo.support.utils.Utility;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

/**
 * User: qii Date: 12-11-14
 */
public class DMUserListFragment extends AbsBaseTimeLineFragment<DMUserListBean> implements
        MainTimeLineActivity.ScrollableListFragment {

    private DMUserListBean bean = new DMUserListBean();

    private DBCacheTask dbTask;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Toolbar writeDMToolbar;

    public static DMUserListFragment newInstance() {
        DMUserListFragment fragment = new DMUserListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public DMUserListBean getList() {
        return bean;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BEAN, bean);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                if (Utility.isTaskStopped(dbTask)) {
                    dbTask = new DBCacheTask();
                    dbTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
            case SCREEN_ROTATE:
                // nothing
                refreshLayout(getList());
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                bean.addNewData((DMUserListBean) savedInstanceState.getParcelable(Constants.BEAN));
                getAdapter().notifyDataSetChanged();
                refreshLayout(getList());
                break;
        }
        if ((((MainTimeLineActivity) getActivity()).getLeftMenuFragment()).getCurrentIndex() == LeftMenuFragment.DM_INDEX) {
            buildActionBarAndViewPagerTitles();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.cancelTasks(dbTask);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDivider(null);

        writeDMToolbar = (Toolbar) ((MainTimeLineActivity) getActivity()).findViewById(R.id.mainTimeLineToolBar);
        writeDMToolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                int itemId = arg0.getItemId();
				if (itemId == R.id.menu_write_dm) {
					Intent intent = new Intent(getActivity(), DMSelectUserActivity.class);
					startActivityForResult(intent, 0);
				}
                return false;
            }
        });
    }

    public void showWriteDmMenu() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                writeDMToolbar.getMenu().clear();
                writeDMToolbar.inflateMenu(R.menu.actionbar_menu_dmuserlistfragment);
            }
        }, 200);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            buildActionBarAndViewPagerTitles();
        }
    }

    public void buildActionBarAndViewPagerTitles() {
        ((MainTimeLineActivity) getActivity()).setCurrentFragment(this);

        if (Utility.isDevicePort()) {
            ((MainTimeLineActivity) getActivity()).setTitle(getString(R.string.dm));
            // getBaseToolbar().setLogo(R.drawable.ic_menu_message);
        } else {
            ((MainTimeLineActivity) getActivity()).setTitle(getString(R.string.dm));
            // getBaseToolbar().setLogo(R.drawable.beebo_launcher);
        }

        // getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        // getActivity().getActionBar().removeAllTabs();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		if (itemId == R.id.menu_write_dm) {
			Intent intent = new Intent(getActivity(), DMSelectUserActivity.class);
			startActivityForResult(intent, 0);
		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), DMActivity.class);
        intent.putExtra("user", bean.getItem(position).getUser());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        Intent intent = new Intent(getActivity(), DMActivity.class);
        intent.putExtra("user", data.getParcelableExtra("user"));
        startActivity(intent);
    }

    @Override
    protected void buildListAdapter() {
        timeLineAdapter = new DMUserListAdapter(this, getList().getItemList(), getListView());
        getListView().setAdapter(timeLineAdapter);
    }

    private class DBCacheTask extends MyAsyncTask<Void, DMUserListBean, DMUserListBean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getPullToRefreshListView().setVisibility(View.INVISIBLE);
        }

        @Override
        protected DMUserListBean doInBackground(Void... params) {
            return DMDBTask.get(GlobalContext.getInstance().getCurrentAccountId());
        }

        @Override
        protected void onPostExecute(DMUserListBean result) {
            super.onPostExecute(result);
            if (result != null) {
                getList().addNewData(result);
            }
            getPullToRefreshListView().setVisibility(View.VISIBLE);
            getAdapter().notifyDataSetChanged();
            refreshLayout(getList());

            if (getList().getSize() == 0) {
                getPullToRefreshListView().setRefreshing();
                loadNewMsg();
            }
        }
    }

    @Override
    protected void newMsgLoaderSuccessCallback(DMUserListBean newValue, Bundle loaderArgs) {
        if (newValue != null && newValue.getSize() > 0 && getActivity() != null) {
            getList().addNewData(newValue);
            getAdapter().notifyDataSetChanged();
            getListView().setSelectionAfterHeaderView();
            DMDBTask.asyncReplace(getList(), GlobalContext.getInstance().getCurrentAccountId());

        }

    }

    @Override
    protected void oldMsgLoaderSuccessCallback(DMUserListBean newValue) {
        if (newValue != null && newValue.getSize() > 0 && getActivity() != null) {
            getList().addOldData(newValue);
            getAdapter().notifyDataSetChanged();
        }
    }

    protected Loader<AsyncTaskLoaderResult<DMUserListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String token = GlobalContext.getInstance().getSpecialToken();
        String cursor = String.valueOf(0);
        return new DMUserLoader(getActivity(), token, cursor);
    }

    protected Loader<AsyncTaskLoaderResult<DMUserListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String token = GlobalContext.getInstance().getSpecialToken();
        String cursor = null;
        if (getList().getSize() > 0 && Integer.valueOf(getList().getNext_cursor()) == 0) {
            return null;
        }

        if (getList().getSize() > 0) {
            cursor = String.valueOf(getList().getNext_cursor());
        }

        return new DMUserLoader(getActivity(), token, cursor);
    }

    @Override
    public void scrollToTop() {
        Utility.stopListViewScrollingAndScrollToTop(getListView());
    }
}
