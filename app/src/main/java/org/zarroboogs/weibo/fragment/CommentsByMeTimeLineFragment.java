
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.IRemoveItem;
import org.zarroboogs.weibo.adapter.CommentListAdapter;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.CommentListBean;
import org.zarroboogs.weibo.bean.CommentTimeLineData;
import org.zarroboogs.weibo.bean.TimeLinePosition;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.DestroyCommentDao;
import org.zarroboogs.weibo.db.task.CommentByMeTimeLineDBTask;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.loader.CommentsByMeDBLoader;
import org.zarroboogs.weibo.loader.CommentsByMeMsgLoader;
import org.zarroboogs.weibo.support.utils.Utility;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class CommentsByMeTimeLineFragment extends AbsBaseTimeLineFragment<CommentListBean> implements IRemoveItem {

    private AccountBean accountBean;

    private UserBean userBean;

    private String token;

    private RemoveTask removeTask;

    private CommentListBean bean = new CommentListBean();

    private TimeLinePosition timeLinePosition;

    @Override
    public CommentListBean getDataList() {
        return bean;
    }

    public CommentsByMeTimeLineFragment() {

    }

    public CommentsByMeTimeLineFragment(AccountBean accountBean, UserBean userBean) {
        this.accountBean = accountBean;
        this.userBean = userBean;
        this.token = accountBean.getAccess_token_hack();
    }

    protected void clearAndReplaceValue(CommentListBean value) {
        getDataList().getItemList().clear();
        getDataList().getItemList().addAll(value.getItemList());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Constants.ACCOUNT, accountBean);
        outState.putParcelable(Constants.USERBEAN, userBean);
        outState.putString(Constants.TOKEN, token);

        if (getActivity().isChangingConfigurations()) {
            outState.putParcelable(Constants.BEAN, bean);
            outState.putSerializable("timeLinePosition", timeLinePosition);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onListViewScrollStop() {
        super.onListViewScrollStop();
        timeLinePosition = Utility.getCurrentPositionFromListView(getListView());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!getActivity().isChangingConfigurations()) {
            CommentByMeTimeLineDBTask.asyncUpdatePosition(timeLinePosition, accountBean.getUid());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                userBean = savedInstanceState.getParcelable(Constants.USERBEAN);
                accountBean = savedInstanceState.getParcelable(Constants.ACCOUNT);
                token = savedInstanceState.getString(Constants.TOKEN);
                timeLinePosition = (TimeLinePosition) savedInstanceState.getSerializable("timeLinePosition");

                Loader<CommentTimeLineData> loader = getLoaderManager().getLoader(DB_CACHE_LOADER_ID);
                if (loader != null) {
                    getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
                }

                CommentListBean savedBean = savedInstanceState.getParcelable(Constants.BEAN);
                if (savedBean != null && savedBean.getSize() > 0) {
                    clearAndReplaceValue(savedBean);
                    timeLineAdapter.notifyDataSetChanged();
                    setListViewPositionFromPositionsCache();
                } else {
                    getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
                }
                break;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible() && isVisibleToUser) {
            // ((MainTimeLineActivity) getActivity()).setCurrentFragment(this);
        }
    }

    @Override
    public void removeItem(int position) {
        if (removeTask == null || removeTask.getStatus() == MyAsyncTask.Status.FINISHED) {
            Log.d("commentsByME: removeItem", "toaken:" + BeeboApplication.getInstance().getAccessToken() + "  ID: " + getDataList().getItemList().get(position)
                    .getId() + "   pos:" + position);
            removeTask = new RemoveTask(BeeboApplication.getInstance().getAccessToken(), getDataList().getItemList().get(position)
                    .getId(), position);
            removeTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void removeCancel() {
    }

    class RemoveTask extends MyAsyncTask<Void, Void, Boolean> {

        String token;

        String id;

        int positon;

        WeiboException e;

        public RemoveTask(String token, String id, int positon) {
            this.token = token;
            this.id = id;
            this.positon = positon;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DestroyCommentDao dao = new DestroyCommentDao(token, id);
            try {
                return dao.destroy();
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
                return false;
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            if (Utility.isAllNotNull(getActivity(), this.e)) {
                Toast.makeText(getActivity(), e.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                ((CommentListAdapter) timeLineAdapter).removeItem(positon);

            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(false);
    }

    private void setListViewPositionFromPositionsCache() {
        Utility.setListViewSelectionFromTop(getListView(), timeLinePosition != null ? timeLinePosition.position : 0,
                timeLinePosition != null ? timeLinePosition.top : 0);
    }

    @Override
    protected void buildListAdapter() {
        timeLineAdapter = new CommentListAdapter(this, getDataList().getItemList(), getListView(), true, false);
        mPullToRefreshListView.setAdapter(timeLineAdapter);
    }

    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {
    }

    @Override
    protected void onNewMsgLoaderSuccessCallback(CommentListBean newValue, Bundle loaderArgs) {
        if (newValue != null && newValue.getItemList() != null && newValue.getItemList().size() > 0) {
            getDataList().addNewData(newValue);
            getAdapter().notifyDataSetChanged();
            getListView().setSelectionAfterHeaderView();
            CommentByMeTimeLineDBTask.asyncReplace(getDataList(), accountBean.getUid());
        }
    }

    @Override
    protected void onOldMsgLoaderSuccessCallback(CommentListBean newValue) {
        if (newValue != null && newValue.getItemList().size() > 1) {
            getDataList().addOldData(newValue);
            getAdapter().notifyDataSetChanged();
            CommentByMeTimeLineDBTask.asyncReplace(getDataList(), accountBean.getUid());
        }
    }

    private LoaderManager.LoaderCallbacks<CommentTimeLineData> dbCallback = new LoaderManager.LoaderCallbacks<CommentTimeLineData>() {
        @Override
        public Loader<CommentTimeLineData> onCreateLoader(int id, Bundle args) {
            getListView().setVisibility(View.INVISIBLE);
            return new CommentsByMeDBLoader(getActivity(), BeeboApplication.getInstance().getCurrentAccountId());
        }

        @Override
        public void onLoadFinished(Loader<CommentTimeLineData> loader, CommentTimeLineData result) {
            if (result != null) {
                clearAndReplaceValue(result.cmtList);
                timeLinePosition = result.position;
            }

            getListView().setVisibility(View.VISIBLE);
            getAdapter().notifyDataSetChanged();
            setListViewPositionFromPositionsCache();

            /**
             * when this account first open app,if he don't have any data in database,fetch data
             * from server automally
             */
            if (getDataList().getSize() == 0) {
//                getPullToRefreshListView().setRefreshing();
                getSwipeRefreshLayout().setRefreshing(true);
                loadNewMsg();
            }

            getLoaderManager().destroyLoader(loader.getId());

        }

        @Override
        public void onLoaderReset(Loader<CommentTimeLineData> loader) {

        }
    };

    protected Loader<AsyncTaskLoaderResult<CommentListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String accountId = accountBean.getUid();
        String token = accountBean.getAccess_token();
        String sinceId = null;
        if (getDataList().getItemList().size() > 0) {
            sinceId = getDataList().getItemList().get(0).getId();
        }
        return new CommentsByMeMsgLoader(getActivity(), accountId, token, sinceId, null);
    }

    protected Loader<AsyncTaskLoaderResult<CommentListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String accountId = accountBean.getUid();
        String token = accountBean.getAccess_token();
        String maxId = null;
        if (getDataList().getItemList().size() > 0) {
            maxId = getDataList().getItemList().get(getDataList().getItemList().size() - 1).getId();
        }
        return new CommentsByMeMsgLoader(getActivity(), accountId, token, null, maxId);
    }
}
