
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.msrl.widget.MaterialSwipeRefreshLayout;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.MyAnimationListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.UserInfoActivity;
import org.zarroboogs.weibo.adapter.UserListAdapter;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.loader.AbstractAsyncNetRequestTaskLoader;
import org.zarroboogs.weibo.loader.DummyLoader;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public abstract class AbstractUserListFragment extends BaseStateFragment {

    protected ListView pullToRefreshListView;

    protected MaterialSwipeRefreshLayout mSwipeRefreshLayout;

    protected TextView empty;

    private UserListAdapter userListAdapter;

    protected UserListBean bean = new UserListBean();

    protected static final int NEW_USER_LOADER_ID = 1;

    protected static final int OLD_USER_LOADER_ID = 2;

    private boolean canLoadOldData = true;

    // private Toolbar mToolbar;

    public AbstractUserListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(false);
    }

    public UserListBean getList() {
        return bean;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BEAN, bean);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.abs_userlist_listview_layout, container, false);
        // mToolbar = (Toolbar) view.findViewById(R.id.baseToolBar);

        empty = (TextView) view.findViewById(R.id.empty);
        mSwipeRefreshLayout = ViewUtility.findViewById(view,R.id.userListSRL);
        mSwipeRefreshLayout.setOnlyPullRefersh();

        mSwipeRefreshLayout.setEnableSount(SettingUtils.getEnableSound());
        pullToRefreshListView = (ListView) view.findViewById(R.id.listView);

//        mSwipeRefreshLayout.setOnRefreshListener(userOnRefreshListener);

        mSwipeRefreshLayout.setOnRefreshLoadMoreListener(userOnRefreshListener);

//        pullToRefreshListView.setOnLastItemVisibleListener(new UserListOnLastItemVisibleListener());
//        pullToRefreshListView.setOnPullEventListener(getPullEventListener());
        pullToRefreshListView.setOnScrollListener(new UserListOnScrollListener());
        pullToRefreshListView.setOnItemClickListener(new UserListOnItemClickListener());
        pullToRefreshListView.setFooterDividersEnabled(false);

        dismissFooterView();

        userListAdapter = new UserListAdapter(AbstractUserListFragment.this, bean.getUsers(), getListView());
        pullToRefreshListView.setAdapter(userListAdapter);

        return view;
    }

    public ListView getListView() {
        return pullToRefreshListView;
    }

    // public Toolbar getBaseToolbar(){
    // return mToolbar;
    // }

    protected UserListAdapter getAdapter() {
        return userListAdapter;
    }

    protected void clearAndReplaceValue(UserListBean value) {

        bean.setNext_cursor(value.getNext_cursor());
        bean.getUsers().clear();
        bean.getUsers().addAll(value.getUsers());
        bean.setTotal_number(value.getTotal_number());
        bean.setPrevious_cursor(value.getPrevious_cursor());

    }

    protected ActionMode actionMode;

    public void setmActionMode(ActionMode mActionMode) {
        this.actionMode = mActionMode;
    }

    @Override
    public void onResume() {
        super.onResume();
        getListView().setFastScrollEnabled(SettingUtils.allowFastScroll());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Loader<UserListBean> loader = getLoaderManager().getLoader(NEW_USER_LOADER_ID);
        if (loader != null) {
            getLoaderManager().initLoader(NEW_USER_LOADER_ID, null, userAsyncTaskLoaderCallback);
        }

        loader = getLoaderManager().getLoader(OLD_USER_LOADER_ID);
        if (loader != null) {
            getLoaderManager().initLoader(OLD_USER_LOADER_ID, null, userAsyncTaskLoaderCallback);
        }

    }

    protected void listViewFooterViewClick(View view) {
        loadOldMsg(view);
    }

    protected void listViewItemClick(AdapterView parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
        intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
        intent.putExtra("user", bean.getUsers().get(position));
        startActivity(intent);
    }

    protected void refreshLayout(UserListBean bean) {
        if (bean.getUsers().size() > 0) {
            empty.setVisibility(View.INVISIBLE);
            // listView.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.INVISIBLE);
            // listView.setVisibility(View.INVISIBLE);
        }
    }

    protected void showFooterView() {
    }

    protected void dismissFooterView() {
    }

    protected void showErrorFooterView() {
    }

    public void loadNewMsg() {
        canLoadOldData = true;

        getLoaderManager().destroyLoader(OLD_USER_LOADER_ID);
        dismissFooterView();
        getLoaderManager().restartLoader(NEW_USER_LOADER_ID, null, userAsyncTaskLoaderCallback);
    }

    protected void loadOldMsg(View view) {

        if (getLoaderManager().getLoader(OLD_USER_LOADER_ID) != null || !canLoadOldData) {
            return;
        }

        getLoaderManager().destroyLoader(NEW_USER_LOADER_ID);
        mSwipeRefreshLayout.setRefreshing(false);
        getLoaderManager().restartLoader(OLD_USER_LOADER_ID, null, userAsyncTaskLoaderCallback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_menu_userlistfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
//		if (itemId == R.id.loading_progressbar) {
//            mSwipeRefreshLayout.setRefreshing(true);
//			loadNewMsg();
//		}
        return super.onOptionsItemSelected(item);
    }

    private void showListView() {
        empty.setVisibility(View.INVISIBLE);
        // listView.setVisibility(View.VISIBLE);
    }


    public void clearActionMode() {
        if (actionMode != null) {

            actionMode.finish();
            actionMode = null;
        }
        if (pullToRefreshListView != null && getListView().getCheckedItemCount() > 0) {
            getListView().clearChoices();
            if (getAdapter() != null) {
                getAdapter().notifyDataSetChanged();
            }
        }
    }

//    private class UserListOnLastItemVisibleListener implements PullToRefreshBase.OnLastItemVisibleListener {
//
//        @Override
//        public void onLastItemVisible() {
//            listViewFooterViewClick(null);
//        }
//    }

//    private SwipeRefreshLayout.OnRefreshListener userOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
//        @Override
//        public void onRefresh() {
//            loadNewMsg();
//        }
//    };

    private MaterialSwipeRefreshLayout.OnRefreshLoadMoreListener userOnRefreshListener = new MaterialSwipeRefreshLayout.OnRefreshLoadMoreListener() {
        @Override
        public void onRefresh() {
            loadNewMsg();
        }

        @Override
        public void onLoadMore() {

        }
    };


    private class UserListOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (actionMode != null) {
                getListView().clearChoices();
                actionMode.finish();
                actionMode = null;
                return;
            }
            getListView().clearChoices();
            if (position< getList().getUsers().size()) {

                listViewItemClick(parent, view, position, id);
            } else {

                listViewFooterViewClick(view);
            }

        }
    }

    private class UserListOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (getListView().getLastVisiblePosition() > 7
                    && getListView().getLastVisiblePosition() > getList().getUsers().size() - 3
                    && getListView().getFirstVisiblePosition() != getListView().getHeaderViewsCount()) {
                loadOldMsg(null);
            }
        }
    }

    protected abstract void oldUserLoaderSuccessCallback(UserListBean newValue);

    protected void newUserLoaderSuccessCallback() {

    }

    private Loader<AsyncTaskLoaderResult<UserListBean>> createNewUserLoader(int id, Bundle args) {
        Loader<AsyncTaskLoaderResult<UserListBean>> loader = onCreateNewUserLoader(id, args);
        if (loader == null) {
            loader = new DummyLoader<UserListBean>(getActivity());
        }
        if (loader instanceof AbstractAsyncNetRequestTaskLoader) {
            ((AbstractAsyncNetRequestTaskLoader) loader).setArgs(args);
        }
        return loader;
    }

    private Loader<AsyncTaskLoaderResult<UserListBean>> createOldUserLoader(int id, Bundle args) {
        Loader<AsyncTaskLoaderResult<UserListBean>> loader = onCreateOldUserLoader(id, args);
        if (loader == null) {
            loader = new DummyLoader<UserListBean>(getActivity());
        }
        return loader;
    }

    protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateNewUserLoader(int id, Bundle args) {
        return null;
    }

    protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateOldUserLoader(int id, Bundle args) {
        return null;
    }

    protected LoaderManager.LoaderCallbacks<AsyncTaskLoaderResult<UserListBean>> userAsyncTaskLoaderCallback = new LoaderManager.LoaderCallbacks<AsyncTaskLoaderResult<UserListBean>>() {

        @Override
        public Loader<AsyncTaskLoaderResult<UserListBean>> onCreateLoader(int id, Bundle args) {
            showListView();
            switch (id) {
                case NEW_USER_LOADER_ID:
                    if (args == null || args.getBoolean(BundleArgsConstants.SCROLL_TO_TOP)) {
                        Utility.stopListViewScrollingAndScrollToTop(getListView());
                    }
                    return createNewUserLoader(id, args);
                case OLD_USER_LOADER_ID:
                    showFooterView();
                    return createOldUserLoader(id, args);
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<AsyncTaskLoaderResult<UserListBean>> loader,
                AsyncTaskLoaderResult<UserListBean> result) {

            UserListBean data = result != null ? result.data : null;
            WeiboException exception = result != null ? result.exception : null;

            switch (loader.getId()) {
                case NEW_USER_LOADER_ID:
                    mSwipeRefreshLayout.setRefreshing(false);
                    refreshLayout(getList());
                    if (Utility.isAllNotNull(exception)) {
                        Toast.makeText(getActivity(), exception.getError(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (data != null && data.getUsers().size() > 0) {
                            clearAndReplaceValue(data);
                            getAdapter().notifyDataSetChanged();
                            getListView().setSelectionAfterHeaderView();
                            newUserLoaderSuccessCallback();
                        }
                    }
                    break;
                case OLD_USER_LOADER_ID:
                    refreshLayout(getList());

                    if (exception != null) {
                        showErrorFooterView();
                    } else if (data != null) {
                        canLoadOldData = data.getUsers().size() > 1;
                        oldUserLoaderSuccessCallback(data);
                        getAdapter().notifyDataSetChanged();
                        dismissFooterView();
                    } else {
                        canLoadOldData = false;
                        dismissFooterView();
                    }
                    break;
            }
            getLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<AsyncTaskLoaderResult<UserListBean>> loader) {

        }
    };

}
