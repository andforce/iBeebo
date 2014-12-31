
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.BrowserWeiboMsgActivity;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.FavListBean;
import org.zarroboogs.weibo.bean.FavouriteTimeLineData;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageReCmtCountBean;
import org.zarroboogs.weibo.bean.TimeLinePosition;
import org.zarroboogs.weibo.dao.TimeLineReCmtCountDao;
import org.zarroboogs.weibo.db.task.FavouriteDBTask;
import org.zarroboogs.weibo.fragment.base.AbsTimeLineFragment;
import org.zarroboogs.weibo.loader.MyFavMsgLoader;
import org.zarroboogs.weibo.support.utils.Utility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * User: qii Date: 12-8-18 this class need to refactor
 */
public class MyFavListFragment extends AbsTimeLineFragment<FavListBean> implements
        MainTimeLineActivity.ScrollableListFragment {

    private int page = 1;

    private FavListBean bean = new FavListBean();

    private TimeLinePosition position;

    private DBCacheTask dbTask;

    private AccountBean account;

    @Override
    public FavListBean getList() {
        return bean;
    }

    public static MyFavListFragment newInstance() {
        MyFavListFragment fragment = new MyFavListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public MyFavListFragment() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putSerializable(Constances.BEAN, bean);
        // outState.putInt("page", page);
    }

    // @Override
    // public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // super.onCreateOptionsMenu(menu, inflater);
    // inflater.inflate(R.menu.actionbar_menu_myfavlistfragment, menu);
    // }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        account = GlobalContext.getInstance().getAccountBean();
        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                readDBCache();
                break;
            case SCREEN_ROTATE:
                // nothing
                refreshLayout(bean);
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                readDBCache();
                break;
        }

        if ((((MainTimeLineActivity) getActivity()).getLeftMenuFragment()).getCurrentIndex() == LeftMenuFragment.FAV_INDEX) {
            buildActionBarAndViewPagerTitles();
        }
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

        // if (Utility.isDevicePort()) {
        // ((MainTimeLineActivity) getActivity()).setTitle(getString(R.string.favourite));
        // getBaseToolbar().setLogo(R.drawable.ic_menu_fav);
        // } else {
        // ((MainTimeLineActivity) getActivity()).setTitle(getString(R.string.favourite));
        // getBaseToolbar().setLogo(R.drawable.beebo_launcher);
        // }

    }

    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {
        startActivityForResult(BrowserWeiboMsgActivity.newIntent(GlobalContext.getInstance().getAccountBean(),
                bean.getItem(position), GlobalContext
                        .getInstance().getSpecialToken()),
                MainTimeLineActivity.REQUEST_CODE_UPDATE_MY_FAV_TIMELINE_COMMENT_REPOST_COUNT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // use Up instead of Back to reach this fragment
        if (data == null) {
            return;
        }
        final MessageBean msg = (MessageBean) data.getParcelableExtra("msg");
        if (msg != null) {
            for (int i = 0; i < getList().getSize(); i++) {
                if (msg.equals(getList().getItem(i))) {
                    MessageBean ori = getList().getItem(i);
                    if (ori.getComments_count() != msg.getComments_count()
                            || ori.getReposts_count() != msg.getReposts_count()) {
                        ori.setReposts_count(msg.getReposts_count());
                        ori.setComments_count(msg.getComments_count());
                        FavouriteDBTask.asyncReplace(getList(), page, account.getUid());
                        getAdapter().notifyDataSetChanged();
                    }
                    break;
                }
            }
        }
    }

    private void buildActionBarSubtitle() {
        if (bean != null) {
            int newSize = bean.getTotal_number();
            String number = bean.getSize() + "/" + newSize;
            // getActivity().getActionBar().setSubtitle(number);
        }
    }

    private void readDBCache() {
        if (Utility.isTaskStopped(dbTask) && getList().getSize() == 0) {
            dbTask = new DBCacheTask();
            dbTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_refresh:
                getPullToRefreshListView().setRefreshing();
                loadNewMsg();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!getActivity().isChangingConfigurations()) {
            savePositionToDB();
        }
    }

    @Override
    protected void newMsgLoaderSuccessCallback(FavListBean newValue, Bundle loaderArgs) {
        if (newValue != null && getActivity() != null && newValue.getSize() > 0) {
            addNewDataWithoutRememberPosition(newValue);
            buildActionBarSubtitle();
            FavouriteDBTask.asyncReplace(getList(), page, account.getUid());

        }

    }

    @Override
    protected void oldMsgLoaderSuccessCallback(FavListBean newValue) {

        if (newValue != null && newValue.getSize() > 0) {
            getList().addOldData(newValue);
            getAdapter().notifyDataSetChanged();
            buildActionBarSubtitle();
            page++;
            FavouriteDBTask.asyncReplace(getList(), page, account.getUid());
        }
    }

    private void addNewDataWithoutRememberPosition(FavListBean newValue) {
        getList().replaceData(newValue);
        getAdapter().notifyDataSetChanged();
        getListView().setSelectionAfterHeaderView();
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<FavListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String token = GlobalContext.getInstance().getSpecialToken();
        page = 1;
        return new MyFavMsgLoader(getActivity(), token, String.valueOf(page));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<FavListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String token = GlobalContext.getInstance().getSpecialToken();
        return new MyFavMsgLoader(getActivity(), token, String.valueOf(page + 1));
    }

    @Override
    public void scrollToTop() {
        Utility.stopListViewScrollingAndScrollToTop(getListView());
    }

    @Override
    protected void onListViewScrollStop() {
        savePositionToPositionsCache();

    }

    private void savePositionToDB() {
        if (position == null) {
            savePositionToPositionsCache();
        }
        position.newMsgIds = newMsgTipBar.getValues();
        FavouriteDBTask.asyncUpdatePosition(position, account.getUid());
    }

    private void savePositionToPositionsCache() {
        position = Utility.getCurrentPositionFromListView(getListView());
    }

    private void setListViewPositionFromPositionsCache() {
        TimeLinePosition p = position;
        if (p != null) {
            getListView().setSelectionFromTop(p.position + 1, p.top);
        } else {
            getListView().setSelectionFromTop(0, 0);
        }

    }

    private class DBCacheTask extends MyAsyncTask<Void, FavouriteTimeLineData, FavouriteTimeLineData> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getPullToRefreshListView().setVisibility(View.INVISIBLE);
        }

        @Override
        protected FavouriteTimeLineData doInBackground(Void... params) {

            return FavouriteDBTask.getFavouriteMsgList(account.getUid());
        }

        @Override
        protected void onPostExecute(FavouriteTimeLineData result) {
            super.onPostExecute(result);
            if (getActivity() == null) {
                return;
            }
            getPullToRefreshListView().setVisibility(View.VISIBLE);

            if (result != null) {
                bean.replaceData(result.favList);
                page = result.page;
                position = result.position;
                getAdapter().notifyDataSetChanged();
                setListViewPositionFromPositionsCache();

            }

            refreshLayout(getList());

            if (getList().getSize() == 0) {
                getPullToRefreshListView().setRefreshing();
                loadNewMsg();
            } else {
                new RefreshReCmtCountTask().executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    private class RefreshReCmtCountTask extends MyAsyncTask<Void, List<MessageReCmtCountBean>, List<MessageReCmtCountBean>> {

        List<String> msgIds;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            msgIds = new ArrayList<String>();
            List<MessageBean> msgList = getList().getItemList();
            for (MessageBean msg : msgList) {
                if (msg != null) {
                    msgIds.add(msg.getId());
                }
            }
        }

        @Override
        protected List<MessageReCmtCountBean> doInBackground(Void... params) {
            try {
                return new TimeLineReCmtCountDao(GlobalContext.getInstance().getSpecialToken(), msgIds).get();
            } catch (WeiboException e) {
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MessageReCmtCountBean> value) {
            super.onPostExecute(value);
            if (getActivity() == null || value == null) {
                return;
            }

            for (int i = 0; i < value.size(); i++) {
                MessageBean msg = getList().getItem(i);
                MessageReCmtCountBean count = value.get(i);
                if (msg != null && msg.getId().equals(count.getId())) {
                    msg.setReposts_count(count.getReposts());
                    msg.setComments_count(count.getComments());
                }
            }
            FavouriteDBTask.asyncReplace(getList(), page, account.getUid());
            getAdapter().notifyDataSetChanged();

        }

    }
}
