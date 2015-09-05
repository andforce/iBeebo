
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.IRemoveItem;
import org.zarroboogs.weibo.adapter.CommentListAdapter;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.CommentBean;
import org.zarroboogs.weibo.bean.CommentListBean;
import org.zarroboogs.weibo.bean.CommentTimeLineData;
import org.zarroboogs.weibo.bean.TimeLinePosition;
import org.zarroboogs.weibo.bean.UnreadBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.ClearUnreadDao;
import org.zarroboogs.weibo.dao.DestroyCommentDao;
import org.zarroboogs.weibo.db.task.MentionCommentsTimeLineDBTask;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.loader.MentionsCommentDBLoader;
import org.zarroboogs.weibo.loader.MentionsCommentMsgLoader;
import org.zarroboogs.weibo.service.NotificationServiceHelper;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.TopTipsView;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class MentionsCommentTimeLineFragment extends AbsBaseTimeLineFragment<CommentListBean> implements IRemoveItem {

    private AccountBean accountBean;

    private UserBean userBean;

    private String token;

    private RemoveTask removeTask;

    private CommentListBean bean = new CommentListBean();

    private UnreadBean unreadBean;

    private TimeLinePosition timeLinePosition;

    private final int POSITION_IN_PARENT_FRAGMENT = 1;

    @Override
    public CommentListBean getDataList() {
        return bean;
    }

    public MentionsCommentTimeLineFragment() {

    }

    public MentionsCommentTimeLineFragment(AccountBean accountBean, UserBean userBean) {
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
            outState.putParcelable("unreadBean", unreadBean);
            outState.putSerializable("timeLinePosition", timeLinePosition);
        }
    }

    @Override
    protected void onListViewScrollStop() {
        super.onListViewScrollStop();
        timeLinePosition = Utility.getCurrentPositionFromListView(getListView());
    }

    @Override
    public void onResume() {
        super.onResume();
        setListViewPositionFromPositionsCache();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(newBroadcastReceiver,
                new IntentFilter(AppEventAction.NEW_MSG_BROADCAST));
        // setActionBarTabCount(newMsgTipBar.getValues().size());
        getNewMsgTipBar().setOnChangeListener(new TopTipsView.OnChangeListener() {
            @Override
            public void onChange(int count) {
//                ((MainTimeLineActivity) getActivity()).setMentionsCommentCount(count);
                // setActionBarTabCount(count);
            }
        });
        checkUnreadInfo();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!getActivity().isChangingConfigurations()) {
            saveTimeLinePositionToDB();
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(newBroadcastReceiver);
    }

    private void saveTimeLinePositionToDB() {
        timeLinePosition = Utility.getCurrentPositionFromListView(getListView());
        timeLinePosition.newMsgIds = newMsgTipBar.getValues();
        MentionCommentsTimeLineDBTask.asyncUpdatePosition(timeLinePosition, accountBean.getUid());
    }

    private void checkUnreadInfo() {
        Loader loader = getLoaderManager().getLoader(DB_CACHE_LOADER_ID);
        if (loader != null) {
            return;
        }
        Intent intent = getActivity().getIntent();
        AccountBean intentAccount = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        CommentListBean mentionsComment = intent.getParcelableExtra(BundleArgsConstants.MENTIONS_COMMENT_EXTRA);
        UnreadBean unreadBeanFromNotification = intent.getParcelableExtra(BundleArgsConstants.UNREAD_EXTRA);

        if (accountBean.equals(intentAccount) && mentionsComment != null) {
            addUnreadMessage(mentionsComment);
            clearUnreadMentions(unreadBeanFromNotification);
            CommentListBean nullObject = null;
            intent.putExtra(BundleArgsConstants.MENTIONS_COMMENT_EXTRA, nullObject);
            getActivity().setIntent(intent);
        }
    }

    // private void setActionBarTabCount(int count) {
    // MentionsTimeLineFragment parent = (MentionsTimeLineFragment) getParentFragment();
    // ActionBar.Tab tab = parent.getCommentTab();
    // if (tab == null) {
    // return;
    // }
    // String tabTag = (String) tab.getTag();
    // if (MentionsCommentTimeLineFragment.class.getName().equals(tabTag)) {
    // View customView = tab.getCustomView();
    // TextView countTV = (TextView) customView.findViewById(R.id.tv_home_count);
    // countTV.setText(String.valueOf(count));
    // if (count > 0) {
    // countTV.setVisibility(View.VISIBLE);
    // } else {
    // countTV.setVisibility(View.GONE);
    // }
    // }
    // }

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
                unreadBean = savedInstanceState.getParcelable("unreadBean");
                timeLinePosition = (TimeLinePosition) savedInstanceState.getSerializable("timeLinePosition");
                CommentListBean savedBean = savedInstanceState.getParcelable(Constants.BEAN);

                Loader<CommentTimeLineData> loader = getLoaderManager().getLoader(DB_CACHE_LOADER_ID);
                if (loader != null) {
                    getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
                }

                if (savedBean != null && savedBean.getSize() > 0) {
                    clearAndReplaceValue(savedBean);
                    timeLineAdapter.notifyDataSetChanged();
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
        newMsgTipBar.setType(TopTipsView.Type.ALWAYS);

    }

    @Override
    public void removeItem(int position) {
        if (removeTask == null || removeTask.getStatus() == MyAsyncTask.Status.FINISHED) {
            removeTask = new RemoveTask(token, getDataList().getItemList().get(position)
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
                timeLinePosition != null ? timeLinePosition.top : 0, new Runnable() {
                    @Override
                    public void run() {
                        setListViewUnreadTipBar(timeLinePosition);
                    }
                });

    }

    private void setListViewUnreadTipBar(TimeLinePosition p) {
        if (p != null && p.newMsgIds != null) {
            newMsgTipBar.setValue(p.newMsgIds);
            // setActionBarTabCount(newMsgTipBar.getValues().size());
//            ((MainTimeLineActivity) getActivity()).setMentionsCommentCount(newMsgTipBar.getValues().size());

        }
    }

    @Override
    protected void buildListAdapter() {
        CommentListAdapter adapter = new CommentListAdapter(this, getDataList().getItemList(), getListView(), true, false);
        adapter.setTopTipBar(newMsgTipBar);
        timeLineAdapter = adapter;
        mPullToRefreshListView.setAdapter(timeLineAdapter);
    }

    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {
    }

    @Override
    protected void onNewMsgLoaderSuccessCallback(CommentListBean newValue, Bundle loaderArgs) {
        if (newValue != null && newValue.getItemList().size() > 0) {
            addNewDataAndRememberPosition(newValue);
        }

        unreadBean = null;

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationServiceHelper.getMentionsCommentNotificationId(BeeboApplication.getInstance()
                .getAccountBean()));
    }

    private void addNewDataAndRememberPosition(final CommentListBean newValue) {

        int initSize = getDataList().getSize();

        if (getActivity() != null && newValue.getSize() > 0) {
            final boolean jumpToTop = getDataList().getSize() == 0;

            getDataList().addNewData(newValue);
            if (!jumpToTop) {
                int index = getListView().getFirstVisiblePosition();
                getAdapter().notifyDataSetChanged();
                int finalSize = getDataList().getSize();
                final int positionAfterRefresh = index + finalSize - initSize + getListView().getHeaderViewsCount();
                // use 1 px to show newMsgTipBar
                Utility.setListViewSelectionFromTop(getListView(), positionAfterRefresh, 1, new Runnable() {

                    @Override
                    public void run() {
                        newMsgTipBar.setValue(newValue, jumpToTop);
                    }
                });

            } else {
                newMsgTipBar.setValue(newValue, jumpToTop);

                newMsgTipBar.clearAndReset();
                getAdapter().notifyDataSetChanged();
                getListView().setSelection(0);
            }
            MentionCommentsTimeLineDBTask.asyncReplace(getDataList(), accountBean.getUid());
            saveTimeLinePositionToDB();
        }

    }

    protected void onMiddleMsgLoaderSuccessCallback(int position, CommentListBean newValue, boolean towardsBottom) {

        if (newValue != null) {
            int size = newValue.getSize();

            if (getActivity() != null && newValue.getSize() > 0) {
                getDataList().addMiddleData(position, newValue, towardsBottom);

                if (towardsBottom) {
                    getAdapter().notifyDataSetChanged();
                } else {

                    View v = Utility.getListViewItemViewFromPosition(getListView(), position + 1 + 1);
                    int top = (v == null) ? 0 : v.getTop();
                    getAdapter().notifyDataSetChanged();
                    int ss = position + 1 + size - 1;
                    getListView().setSelectionFromTop(ss, top);
                }
            }
        }
    }

    @Override
    protected void onOldMsgLoaderSuccessCallback(CommentListBean newValue) {
        if (newValue != null && newValue.getItemList().size() > 1) {
            getDataList().addOldData(newValue);
            getAdapter().notifyDataSetChanged();
            MentionCommentsTimeLineDBTask.asyncReplace(getDataList(), accountBean.getUid());
        }
    }

    private LoaderManager.LoaderCallbacks<CommentTimeLineData> dbCallback = new LoaderManager.LoaderCallbacks<CommentTimeLineData>() {
        @Override
        public Loader<CommentTimeLineData> onCreateLoader(int id, Bundle args) {
            getListView().setVisibility(View.INVISIBLE);
            return new MentionsCommentDBLoader(getActivity(), BeeboApplication.getInstance().getCurrentAccountId());
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

            checkUnreadInfo();
        }

        @Override
        public void onLoaderReset(Loader<CommentTimeLineData> loader) {

        }
    };

    protected Loader<AsyncTaskLoaderResult<CommentListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String accountId = accountBean.getUid();
        String sinceId = null;
        if (getDataList().getItemList().size() > 0) {
            sinceId = getDataList().getItemList().get(0).getId();
        }
        return new MentionsCommentMsgLoader(getActivity(), accountId, token, sinceId, null);
    }

    protected Loader<AsyncTaskLoaderResult<CommentListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String accountId = accountBean.getUid();
        String maxId = null;
        if (getDataList().getItemList().size() > 0) {
            maxId = getDataList().getItemList().get(getDataList().getItemList().size() - 1).getId();
        }
        return new MentionsCommentMsgLoader(getActivity(), accountId, token, null, maxId);
    }

    private BroadcastReceiver newBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AccountBean intentAccount = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
            final UnreadBean unreadBean = intent.getParcelableExtra(BundleArgsConstants.UNREAD_EXTRA);
            if (intentAccount == null || !accountBean.equals(intentAccount)) {
                return;
            }
            CommentListBean data = intent.getParcelableExtra(BundleArgsConstants.MENTIONS_COMMENT_EXTRA);
            addUnreadMessage(data);
            clearUnreadMentions(unreadBean);
        }
    };

    private void addUnreadMessage(CommentListBean data) {
        if (data != null && data.getSize() > 0) {
            CommentBean last = data.getItem(data.getSize() - 1);
            boolean dup = getDataList().getItemList().contains(last);
            if (!dup) {
                addNewDataAndRememberPosition(data);
            }
        }
    }

    private void clearUnreadMentions(final UnreadBean data) {
        new MyAsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    new ClearUnreadDao(token)
                            .clearMentionCommentUnread(data, BeeboApplication
                                    .getInstance().getAccountBean().getUid());
                } catch (WeiboException ignored) {

                }
                return null;
            }
        }.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    protected void onNewMsgLoaderFailedCallback(WeiboException exception) {
        if (exception.getError().trim().equals("用户请求超过上限")) {
            token = accountBean.getAccess_token_hack();
        }
        Toast.makeText(getActivity(), exception.getError(), Toast.LENGTH_SHORT).show();
    }
}
