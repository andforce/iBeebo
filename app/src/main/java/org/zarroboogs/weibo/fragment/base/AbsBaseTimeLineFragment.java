
package org.zarroboogs.weibo.fragment.base;

import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.msrl.widget.MaterialSwipeRefreshLayout;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.activity.WriteWeiboWithAppSrcActivity;
import org.zarroboogs.weibo.adapter.AbstractAppListAdapter;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.data.DataItem;
import org.zarroboogs.weibo.bean.data.DataListItem;
import org.zarroboogs.weibo.loader.AbstractAsyncNetRequestTaskLoader;
import org.zarroboogs.weibo.loader.DummyLoader;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.support.lib.LongClickableLinkMovementMethod;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.ViewUtility;
import org.zarroboogs.weibo.widget.AutoScrollListView;
import org.zarroboogs.weibo.widget.TopTipsView;

import com.melnykov.fab.FloatingActionButton;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class AbsBaseTimeLineFragment<T extends DataListItem<?, ?>> extends BaseStateFragment {

    protected MaterialSwipeRefreshLayout mTimeLineSwipeRefreshLayout;
    protected AutoScrollListView mPullToRefreshListView;

    protected TextView empty;

    protected TopTipsView newMsgTipBar;

    protected BaseAdapter timeLineAdapter;

    public FloatingActionButton mFab;

    protected static final int DB_CACHE_LOADER_ID = 0;

    protected static final int NEW_MSG_LOADER_ID = 1;

    protected static final int OLD_MSG_LOADER_ID = 3;

    protected int savedCurrentLoadingMsgViewPositon = NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION;

    public static final int NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION = -1;

    private boolean mCanLoadOldData = true;


    protected void buildLayout(View view) {
        mTimeLineSwipeRefreshLayout = ViewUtility.findViewById(view, R.id.refresh_layout);
        mTimeLineSwipeRefreshLayout.setEnableSount(SettingUtils.getEnableSound());
        mTimeLineSwipeRefreshLayout.setFooterView(R.layout.listview_footer);

        empty = ViewUtility.findViewById(view, R.id.empty);
        mPullToRefreshListView = ViewUtility.findViewById(view, R.id.listView);
        newMsgTipBar = ViewUtility.findViewById(view, R.id.tv_unread_new_message_count_tip_bar);

        getListView().setHeaderDividersEnabled(false);
        getListView().setScrollingCacheEnabled(false);

        dismissFooterView();
        mFab = ViewUtility.findViewById(view, R.id.absTimeLineFab);

        mFab.attachToListView((AbsListView) ViewUtility.findViewById(view, R.id.listView));

        mFab.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), WriteWeiboWithAppSrcActivity.class);
                intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, BeeboApplication.getInstance().getAccountBean());
                intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
                intent.putExtra(Constants.ACCOUNT, BeeboApplication.getInstance().getAccountBean());
                startActivity(intent);
            }
        });

        mFab.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Utility.stopListViewScrollingAndScrollToTop(getListView());
                mTimeLineSwipeRefreshLayout.setRefreshing(true);
                loadNewMsg();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_layout, container, false);
        buildLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mTimeLineSwipeRefreshLayout != null) {
            mTimeLineSwipeRefreshLayout.setOnRefreshLoadMoreListener(onRefreshListener);
            mPullToRefreshListView.setOnScrollListener(listViewOnScrollListener);
            mPullToRefreshListView.setOnItemClickListener(listViewOnItemClickListener);
        }

        buildListAdapter();
        if (savedInstanceState != null) {
            savedCurrentLoadingMsgViewPositon = savedInstanceState.getInt("savedCurrentLoadingMsgViewPositon",
                    NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void showMenuOnToolBar(final Toolbar toolbar, final int menuRes) {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(menuRes);
            }
        }, 200);
    }

    public MaterialSwipeRefreshLayout getSwipeRefreshLayout() {
        return this.mTimeLineSwipeRefreshLayout;
    }

    public AutoScrollListView getListView() {
        return mPullToRefreshListView;
    }

    public BaseAdapter getAdapter() {
        return timeLineAdapter;
    }

    public TopTipsView getNewMsgTipBar() {
        return newMsgTipBar;
    }

    public abstract T getDataList();

    protected abstract void onTimeListViewItemClick(AdapterView<?> parent, View view, int position, long id);

    public void loadNewMsg() {
        mCanLoadOldData = true;
        getLoaderManager().destroyLoader(OLD_MSG_LOADER_ID);
        dismissFooterView();
        getLoaderManager().restartLoader(NEW_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
    }

    protected void loadOldMsg(View view) {

        if (getLoaderManager().getLoader(OLD_MSG_LOADER_ID) != null || !mCanLoadOldData) {
            return;
        }

        getLoaderManager().destroyLoader(NEW_MSG_LOADER_ID);
        getLoaderManager().restartLoader(OLD_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("savedCurrentLoadingMsgViewPositon", savedCurrentLoadingMsgViewPositon);
    }


    private MaterialSwipeRefreshLayout.OnRefreshLoadMoreListener onRefreshListener = new MaterialSwipeRefreshLayout.OnRefreshLoadMoreListener() {
        @Override
        public void onRefresh() {
            if (getActivity() == null) {
                return;
            }

            if (getLoaderManager().getLoader(NEW_MSG_LOADER_ID) != null) {
                return;
            }

            loadNewMsg();
        }

        @Override
        public void onLoadMore() {

            DevLog.printLog("TimeLineLoadMore ", "loadMore");
            if (getActivity() == null) {
                return;
            }

            if (getLoaderManager().getLoader(OLD_MSG_LOADER_ID) != null) {
                return;
            }

            loadOldMsg(null);
        }
    };


    private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            getListView().clearChoices();
            int headerViewsCount = getListView().getHeaderViewsCount();
            if (isPositionBetweenHeaderViewAndFooterView(position)) {
                int indexInDataSource = position - headerViewsCount;
                DataItem msg = getDataList().getItem(indexInDataSource);
                if (!isNullFlag(msg)) {
                    onTimeListViewItemClick(parent, view, indexInDataSource, id);
                }

            } else if (isLastItem(position)) {
                loadOldMsg(view);
            }
        }

        boolean isPositionBetweenHeaderViewAndFooterView(int position) {
            return position - getListView().getHeaderViewsCount() < getDataList().getSize()
                    && position - getListView().getHeaderViewsCount() >= 0;
        }


        boolean isNullFlag(DataItem msg) {
            return msg == null;
        }

        boolean isLastItem(int position) {
            return position - 1 >= getDataList().getSize();
        }
    };

    private AbsListView.OnScrollListener listViewOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    if (!enableRefreshTime) {
                        enableRefreshTime = true;
                        getAdapter().notifyDataSetChanged();
                    }
                    onListViewScrollStop();
                    LongClickableLinkMovementMethod.getInstance().setLongClickable(true);
                    TimeLineBitmapDownloader.getInstance().setPauseDownloadWork(false);
                    TimeLineBitmapDownloader.getInstance().setPauseReadWork(false);

                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    enableRefreshTime = false;
                    LongClickableLinkMovementMethod.getInstance().setLongClickable(false);
                    TimeLineBitmapDownloader.getInstance().setPauseDownloadWork(true);
                    TimeLineBitmapDownloader.getInstance().setPauseReadWork(true);
                    onListViewScrollStateFling();
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    enableRefreshTime = true;
                    LongClickableLinkMovementMethod.getInstance().setLongClickable(false);
                    TimeLineBitmapDownloader.getInstance().setPauseDownloadWork(true);
                    onListViewScrollStateTouchScroll();
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            onListViewScroll();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        TimeLineBitmapDownloader.getInstance().setPauseDownloadWork(false);
        TimeLineBitmapDownloader.getInstance().setPauseReadWork(false);

    }

    protected void onListViewScrollStop() {

    }

    protected void onListViewScrollStateTouchScroll() {

    }

    protected void onListViewScrollStateFling() {

    }

    protected void onListViewScroll() {

        if (allowLoadOldMsgBeforeReachListBottom() && getListView().getLastVisiblePosition() > 7
                && getListView().getLastVisiblePosition() > getDataList().getSize() - 3
                && getListView().getFirstVisiblePosition() != getListView().getHeaderViewsCount()) {
            loadOldMsg(null);
        }
    }

    protected boolean allowLoadOldMsgBeforeReachListBottom() {
        return true;
    }

    protected void showFooterView() {

    }

    protected void dismissFooterView() {

    }

    protected void showErrorFooterView() {

    }

    protected abstract void buildListAdapter();

    protected boolean allowRefresh() {
        boolean isNewMsgLoaderLoading = getLoaderManager().getLoader(NEW_MSG_LOADER_ID) != null;
        return getListView().getVisibility() == View.VISIBLE && !isNewMsgLoaderLoading;
    }

    @Override
    public void onResume() {
        super.onResume();
        getListView().setFastScrollEnabled(SettingUtils.allowFastScroll());
        getAdapter().notifyDataSetChanged();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Loader<T> loader = getLoaderManager().getLoader(NEW_MSG_LOADER_ID);
        if (loader != null) {
            getLoaderManager().initLoader(NEW_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
        }

        loader = getLoaderManager().getLoader(OLD_MSG_LOADER_ID);
        if (loader != null) {
            getLoaderManager().initLoader(OLD_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
        }

        if (getActivity() instanceof MainTimeLineActivity) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            if (mFab != null) {
                mFab.setVisibility(View.GONE);
            }
        }
    }

    protected void showListView() {
    }

    private volatile boolean enableRefreshTime = true;

    public boolean isListViewFling() {
        return !enableRefreshTime;
    }


    protected abstract void onNewMsgLoaderSuccessCallback(T newValue, Bundle loaderArgs);

    protected abstract void onOldMsgLoaderSuccessCallback(T newValue);

    protected void onOldMsgLoaderFailedCallback(WeiboException exception) {

    }

    protected void onNewMsgLoaderFailedCallback(WeiboException exception) {

    }


    private Loader<AsyncTaskLoaderResult<T>> createNewMsgLoader(int id, Bundle args) {
        Loader<AsyncTaskLoaderResult<T>> loader = onCreateNewMsgLoader(id, args);
        if (loader == null) {
            loader = new DummyLoader<T>(getActivity());
        }
        if (loader instanceof AbstractAsyncNetRequestTaskLoader) {
            ((AbstractAsyncNetRequestTaskLoader<T>) loader).setArgs(args);
        }
        return loader;
    }

    private Loader<AsyncTaskLoaderResult<T>> createOldMsgLoader(int id, Bundle args) {
        Loader<AsyncTaskLoaderResult<T>> loader = onCreateOldMsgLoader(id, args);
        if (loader == null) {
            loader = new DummyLoader<T>(getActivity());
        }
        return loader;
    }

    protected Loader<AsyncTaskLoaderResult<T>> onCreateNewMsgLoader(int id, Bundle args) {
        return null;
    }

    protected Loader<AsyncTaskLoaderResult<T>> onCreateOldMsgLoader(int id, Bundle args) {
        return null;
    }

    protected LoaderManager.LoaderCallbacks<AsyncTaskLoaderResult<T>> msgAsyncTaskLoaderCallback = new LoaderManager.LoaderCallbacks<AsyncTaskLoaderResult<T>>() {

        @Override
        public Loader<AsyncTaskLoaderResult<T>> onCreateLoader(int id, Bundle args) {
            showListView();
            switch (id) {
                case NEW_MSG_LOADER_ID:
                    if (args == null || args.getBoolean(BundleArgsConstants.SCROLL_TO_TOP)) {
                        Utility.stopListViewScrollingAndScrollToTop(getListView());
                    }
                    return createNewMsgLoader(id, args);
                case OLD_MSG_LOADER_ID:
                    showFooterView();
                    return createOldMsgLoader(id, args);
            }

            return null;
        }

        public static final boolean isDebug = false;

        @Override
        public void onLoadFinished(Loader<AsyncTaskLoaderResult<T>> loader, AsyncTaskLoaderResult<T> result) {

            T data = result != null ? result.data : null;
            WeiboException exception = result != null ? result.exception : null;
            Bundle args = result != null ? result.args : null;

            switch (loader.getId()) {
                case NEW_MSG_LOADER_ID:
                    mTimeLineSwipeRefreshLayout.setRefreshing(false);
                    if (Utility.isAllNotNull(exception)) {
                        if (isDebug || !exception.getError().trim().equals("用户请求超过上限")) {
                            newMsgTipBar.setError(exception.getError());
                        }

                        onNewMsgLoaderFailedCallback(exception);
                    } else {
                        onNewMsgLoaderSuccessCallback(data, args);
                    }
                    break;
                case OLD_MSG_LOADER_ID:

                    if (exception != null) {
                        showErrorFooterView();
                        onOldMsgLoaderFailedCallback(exception);
                    } else if (data != null) {
                        mCanLoadOldData = data.getSize() > 1;
                        onOldMsgLoaderSuccessCallback(data);
                        getAdapter().notifyDataSetChanged();
                        dismissFooterView();
                    } else {
                        mCanLoadOldData = false;
                        dismissFooterView();
                    }
                    break;
            }
            getLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<AsyncTaskLoaderResult<T>> loader) {

        }
    };

}
