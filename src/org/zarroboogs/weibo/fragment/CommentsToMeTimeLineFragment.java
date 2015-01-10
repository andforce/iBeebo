
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.IRemoveItem;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
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
import org.zarroboogs.weibo.db.task.CommentToMeTimeLineDBTask;
import org.zarroboogs.weibo.dialogfragment.CommentFloatingMenuDialog;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.loader.CommentsToMeDBLoader;
import org.zarroboogs.weibo.loader.CommentsToMeMsgLoader;
import org.zarroboogs.weibo.service.NotificationServiceHelper;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.ui.actionmenu.CommentSingleChoiceModeListener;
import org.zarroboogs.weibo.widget.TopTipsView;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

public class CommentsToMeTimeLineFragment extends AbsBaseTimeLineFragment<CommentListBean> implements IRemoveItem {

    private AccountBean accountBean;

    private UserBean userBean;

    private String token;

    private RemoveTask removeTask;

    private CommentListBean bean = new CommentListBean();

    private UnreadBean unreadBean;

    private TimeLinePosition timeLinePosition;

    @Override
    public CommentListBean getList() {
        return bean;
    }

    public CommentsToMeTimeLineFragment() {

    }

    public CommentsToMeTimeLineFragment(AccountBean accountBean, UserBean userBean, String token) {
        this.accountBean = accountBean;
        this.userBean = userBean;
        this.token = token;
    }

    public void setCurrentGroupId(int positoin) {

    }

    protected void clearAndReplaceValue(CommentListBean value) {
        getList().getItemList().clear();
        getList().getItemList().addAll(value.getItemList());
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
        setActionBarTabCount(newMsgTipBar.getValues().size());

        newMsgTipBar.setOnChangeListener(new TopTipsView.OnChangeListener() {
            @Override
            public void onChange(int count) {
                ((MainTimeLineActivity) getActivity()).setCommentsToMeCount(count);
                setActionBarTabCount(count);
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
        CommentToMeTimeLineDBTask.asyncUpdatePosition(timeLinePosition, accountBean.getUid());
    }

    private void checkUnreadInfo() {
        Loader loader = getLoaderManager().getLoader(DB_CACHE_LOADER_ID);
        if (loader != null) {
            return;
        }
        Intent intent = getActivity().getIntent();
        AccountBean intentAccount = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        CommentListBean commentsToMe = intent.getParcelableExtra(BundleArgsConstants.COMMENTS_TO_ME_EXTRA);
        UnreadBean unreadBeanFromNotification = intent.getParcelableExtra(BundleArgsConstants.UNREAD_EXTRA);
        if (accountBean.equals(intentAccount) && commentsToMe != null) {
            addUnreadMessage(commentsToMe);
            clearUnreadComment(unreadBeanFromNotification);
            CommentListBean nullObject = null;
            intent.putExtra(BundleArgsConstants.COMMENTS_TO_ME_EXTRA, nullObject);
            getActivity().setIntent(intent);
        }
    }

    private void setActionBarTabCount(int count) {
//        CommentsTimeLineFragment parent = (CommentsTimeLineFragment) getParentFragment();
        // ActionBar.Tab tab = parent.getCommentsToMeTab();
        // if (tab == null) {
        // return;
        // }
        // String tabTag = (String) tab.getTag();
        // if (CommentsToMeTimeLineFragment.class.getName().equals(tabTag)) {
        // View customView = tab.getCustomView();
        // TextView countTV = (TextView) customView.findViewById(R.id.tv_home_count);
        // countTV.setText(String.valueOf(count));
        // if (count > 0) {
        // countTV.setVisibility(View.VISIBLE);
        // } else {
        // countTV.setVisibility(View.GONE);
        // }
        // }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                userBean = (UserBean) savedInstanceState.getParcelable(Constants.USERBEAN);
                accountBean = (AccountBean) savedInstanceState.getParcelable(Constants.ACCOUNT);
                token = savedInstanceState.getString(Constants.TOKEN);
                timeLinePosition = (TimeLinePosition) savedInstanceState.getSerializable("timeLinePosition");
                unreadBean = (UnreadBean) savedInstanceState.getParcelable("unreadBean");

                Loader<CommentTimeLineData> loader = getLoaderManager().getLoader(DB_CACHE_LOADER_ID);
                if (loader != null) {
                    getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
                }

                CommentListBean savedBean = (CommentListBean) savedInstanceState.getSerializable(Constants.BEAN);
                if (savedBean != null && savedBean.getSize() > 0) {
                    clearAndReplaceValue(savedBean);
                    timeLineAdapter.notifyDataSetChanged();
                    refreshLayout(getList());
                    // setListViewPositionFromPositionsCache();
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
//        getListView().setOnItemLongClickListener(onItemLongClickListener);
        newMsgTipBar.setType(TopTipsView.Type.ALWAYS);
    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (position - 1 < getList().getSize() && position - 1 >= 0) {
                if (actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                    getListView().setItemChecked(position, true);
                    timeLineAdapter.notifyDataSetChanged();
                    actionMode = getActivity().startActionMode(
                            new CommentSingleChoiceModeListener(getListView(), timeLineAdapter,
                                    CommentsToMeTimeLineFragment.this, getList().getItemList().get(
                                            position - 1)));
                    return true;
                } else {
                    getListView().setItemChecked(position, true);
                    timeLineAdapter.notifyDataSetChanged();
                    actionMode = getActivity().startActionMode(
                            new CommentSingleChoiceModeListener(getListView(), timeLineAdapter,
                                    CommentsToMeTimeLineFragment.this, getList().getItemList().get(
                                            position - 1)));
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public void removeItem(int position) {
        clearActionMode();
        if (removeTask == null || removeTask.getStatus() == MyAsyncTask.Status.FINISHED) {
            removeTask = new RemoveTask(GlobalContext.getInstance().getSpecialToken(), getList().getItemList().get(position)
                    .getId(), position);
            
            Log.d("commentsToooME: removeItem", "toaken:" + GlobalContext.getInstance().getSpecialToken() + "  ID: "+ getList().getItemList().get(position)
                    .getId() + "   pos:"  +position);
            removeTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void removeCancel() {
        clearActionMode();
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
            setActionBarTabCount(newMsgTipBar.getValues().size());
            ((MainTimeLineActivity) getActivity()).setCommentsToMeCount(newMsgTipBar.getValues().size());
        }
    }

    @Override
    protected void buildListAdapter() {
        CommentListAdapter adapter = new CommentListAdapter(this, getList().getItemList(), getListView(), true, false);
        adapter.setTopTipBar(newMsgTipBar);
        timeLineAdapter = adapter;
        mPullToRefreshListView.setAdapter(timeLineAdapter);
    }

    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {
        if (!clearActionModeIfOpen()) {
            CommentFloatingMenuDialog menu = new CommentFloatingMenuDialog(getList().getItem(position));
            menu.show(getFragmentManager(), "");
        }
    }

    @Override
    protected void newMsgLoaderSuccessCallback(CommentListBean newValue, Bundle loaderArgs) {
        if (newValue != null && newValue.getItemList() != null && newValue.getItemList().size() > 0) {
            addNewDataAndRememberPosition(newValue);
        }
        unreadBean = null;
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationServiceHelper.getCommentsToMeNotificationId(GlobalContext.getInstance()
                .getAccountBean()));
    }

    private void addNewDataAndRememberPosition(final CommentListBean newValue) {

        int initSize = getList().getSize();

        if (getActivity() != null && newValue.getSize() > 0) {

            final boolean jumpToTop = getList().getSize() == 0;

            getList().addNewData(newValue);

            if (!jumpToTop) {
                int index = getListView().getFirstVisiblePosition();
                getAdapter().notifyDataSetChanged();
                int finalSize = getList().getSize();
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
            CommentToMeTimeLineDBTask.asyncReplace(getList(), accountBean.getUid());
            saveTimeLinePositionToDB();
        }
    }

    @Override
    protected void oldMsgLoaderSuccessCallback(CommentListBean newValue) {
        if (newValue != null && newValue.getItemList().size() > 1) {
            getList().addOldData(newValue);
            getAdapter().notifyDataSetChanged();
            CommentToMeTimeLineDBTask.asyncReplace(getList(), accountBean.getUid());
        }
    }

    @Override
    protected void middleMsgLoaderSuccessCallback(int position, CommentListBean newValue, boolean towardsBottom) {

        if (newValue != null) {
            int size = newValue.getSize();

            if (getActivity() != null && newValue.getSize() > 0) {
                getList().addMiddleData(position, newValue, towardsBottom);

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

    private LoaderManager.LoaderCallbacks<CommentTimeLineData> dbCallback = new LoaderManager.LoaderCallbacks<CommentTimeLineData>() {
        @Override
        public Loader<CommentTimeLineData> onCreateLoader(int id, Bundle args) {
            getPullToRefreshListView().setVisibility(View.INVISIBLE);
            return new CommentsToMeDBLoader(getActivity(), GlobalContext.getInstance().getCurrentAccountId());
        }

        @Override
        public void onLoadFinished(Loader<CommentTimeLineData> loader, CommentTimeLineData result) {
            if (result != null) {
                clearAndReplaceValue(result.cmtList);
                timeLinePosition = result.position;
            }

            getPullToRefreshListView().setVisibility(View.VISIBLE);
            getAdapter().notifyDataSetChanged();
            setListViewPositionFromPositionsCache();

            refreshLayout(getList());
            /**
             * when this account first open app,if he don't have any data in database,fetch data
             * from server automally
             */
            if (getList().getSize() == 0) {
                getPullToRefreshListView().setRefreshing();
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
        String token = accountBean.getAccess_token();
        String sinceId = null;
        if (getList().getItemList().size() > 0) {
            sinceId = getList().getItemList().get(0).getId();
        }
        return new CommentsToMeMsgLoader(getActivity(), accountId, token, sinceId, null);
    }

    protected Loader<AsyncTaskLoaderResult<CommentListBean>> onCreateMiddleMsgLoader(int id, Bundle args,
            String middleBeginId, String middleEndId,
            String middleEndTag, int middlePosition) {
        String accountId = accountBean.getUid();
        String token = accountBean.getAccess_token();
        return new CommentsToMeMsgLoader(getActivity(), accountId, token, middleBeginId, middleEndId);
    }

    protected Loader<AsyncTaskLoaderResult<CommentListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String accountId = accountBean.getUid();
        String token = accountBean.getAccess_token();
        String maxId = null;
        if (getList().getItemList().size() > 0) {
            maxId = getList().getItemList().get(getList().getItemList().size() - 1).getId();
        }
        return new CommentsToMeMsgLoader(getActivity(), accountId, token, null, maxId);
    }

    private BroadcastReceiver newBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final AccountBean intentAccount = (AccountBean) intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
            final UnreadBean unreadBean = (UnreadBean) intent.getParcelableExtra(BundleArgsConstants.UNREAD_EXTRA);
            if (intentAccount == null || !accountBean.equals(intentAccount)) {
                return;
            }
            CommentListBean data = (CommentListBean) intent.getParcelableExtra(BundleArgsConstants.COMMENTS_TO_ME_EXTRA);
            addUnreadMessage(data);
            clearUnreadComment(unreadBean);
        }
    };

    private void addUnreadMessage(CommentListBean data) {
        if (data != null && data.getSize() > 0) {
            CommentBean last = data.getItem(data.getSize() - 1);
            boolean dup = getList().getItemList().contains(last);
            if (!dup) {
                addNewDataAndRememberPosition(data);
            }
        }
    }

    private void clearUnreadComment(final UnreadBean data) {
        new MyAsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    new ClearUnreadDao(GlobalContext.getInstance().getAccountBean().getAccess_token()).clearCommentUnread(
                            data, GlobalContext.getInstance()
                                    .getAccountBean().getUid());
                } catch (WeiboException ignored) {

                }
                return null;
            }
        }.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
    }
}
