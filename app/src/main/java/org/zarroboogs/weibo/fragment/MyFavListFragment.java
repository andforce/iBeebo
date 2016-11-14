
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.BeeboApplication;
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

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class MyFavListFragment extends AbsTimeLineFragment<FavListBean> implements
        MainTimeLineActivity.ScrollableListFragment {

    private int page = 1;

    private FavListBean bean = new FavListBean();

    private TimeLinePosition position;

    private DBCacheTask dbTask;

    private AccountBean account;

    @Override
    public FavListBean getDataList() {
        return bean;
    }

    public static MyFavListFragment newInstance() {
        MyFavListFragment fragment = new MyFavListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        account = BeeboApplication.getInstance().getAccountBean();
        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                readDBCache();
                break;
            case SCREEN_ROTATE:
                // nothing
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
    }

    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {
        startActivityForResult(BrowserWeiboMsgActivity.newIntent(BeeboApplication.getInstance().getAccountBean(),
                bean.getItem(position), BeeboApplication
                        .getInstance().getAccessToken()),
                MainTimeLineActivity.REQUEST_CODE_UPDATE_MY_FAV_TIMELINE_COMMENT_REPOST_COUNT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // use Up instead of Back to reach this fragment
        if (data == null) {
            return;
        }
        final MessageBean msg = data.getParcelableExtra("msg");
        if (msg != null) {
            for (int i = 0; i < getDataList().getSize(); i++) {
                if (msg.equals(getDataList().getItem(i))) {
                    MessageBean ori = getDataList().getItem(i);
                    if (ori.getComments_count() != msg.getComments_count()
                            || ori.getReposts_count() != msg.getReposts_count()) {
                        ori.setReposts_count(msg.getReposts_count());
                        ori.setComments_count(msg.getComments_count());
                        FavouriteDBTask.asyncReplace(getDataList(), page, account.getUid());
                        getAdapter().notifyDataSetChanged();
                    }
                    break;
                }
            }
        }
    }

    private void readDBCache() {
        if (Utility.isTaskStopped(dbTask) && getDataList().getSize() == 0) {
            dbTask = new DBCacheTask();
            dbTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_refresh) {
//			getPullToRefreshListView().setRefreshing();
            getSwipeRefreshLayout().setRefreshing(true);
            loadNewMsg();
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
    protected void onNewMsgLoaderSuccessCallback(FavListBean newValue, Bundle loaderArgs) {
        if (newValue != null && getActivity() != null && newValue.getSize() > 0) {
            addNewDataWithoutRememberPosition(newValue);
            FavouriteDBTask.asyncReplace(getDataList(), page, account.getUid());

        }

    }

    @Override
    protected void onOldMsgLoaderSuccessCallback(FavListBean newValue) {

        if (newValue != null && newValue.getSize() > 0) {
            getDataList().addOldData(newValue);
            getAdapter().notifyDataSetChanged();
            page++;
            FavouriteDBTask.asyncReplace(getDataList(), page, account.getUid());
        }
    }

    private void addNewDataWithoutRememberPosition(FavListBean newValue) {
        getDataList().replaceData(newValue);
        getAdapter().notifyDataSetChanged();
        getListView().setSelectionAfterHeaderView();
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<FavListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessToken();
        page = 1;
        return new MyFavMsgLoader(getActivity(), token, String.valueOf(page));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<FavListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessToken();
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
            getListView().setVisibility(View.INVISIBLE);
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
            getListView().setVisibility(View.VISIBLE);

            if (result != null) {
                bean.replaceData(result.favList);
                page = result.page;
                position = result.position;
                getAdapter().notifyDataSetChanged();
                setListViewPositionFromPositionsCache();

            }

            if (getDataList().getSize() == 0) {
//                getPullToRefreshListView().setRefreshing();
                getSwipeRefreshLayout().setRefreshing(true);
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
            msgIds = new ArrayList<>();
            List<MessageBean> msgList = getDataList().getItemList();
            for (MessageBean msg : msgList) {
                if (msg != null) {
                    msgIds.add(msg.getId());
                }
            }
        }

        @Override
        protected List<MessageReCmtCountBean> doInBackground(Void... params) {
            try {
                return new TimeLineReCmtCountDao(BeeboApplication.getInstance().getAccessToken(), msgIds).get();
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
                MessageBean msg = getDataList().getItem(i);
                MessageReCmtCountBean count = value.get(i);
                if (msg != null && msg.getId().equals(count.getId())) {
                    msg.setReposts_count(count.getReposts());
                    msg.setComments_count(count.getComments());
                }
            }
            FavouriteDBTask.asyncReplace(getDataList(), page, account.getUid());
            getAdapter().notifyDataSetChanged();

        }

    }
}
