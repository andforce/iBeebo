
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.MyAnimationListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.BrowserWeiboMsgActivity;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.activity.WriteWeiboWithAppSrcActivity;
import org.zarroboogs.weibo.activity.WriteWeiboActivity;
import org.zarroboogs.weibo.adapter.AbstractAppListAdapter;
import org.zarroboogs.weibo.adapter.FriendsTimeLineListNavAdapter;
import org.zarroboogs.weibo.adapter.StatusListAdapter;
import org.zarroboogs.weibo.asynctask.GroupInfoTask;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.GroupBean;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.bean.MessageReCmtCountBean;
import org.zarroboogs.weibo.bean.MessageTimeLineData;
import org.zarroboogs.weibo.bean.TimeLinePosition;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.TimeLineReCmtCountDao;
import org.zarroboogs.weibo.db.task.FriendsTimeLineDBTask;
import org.zarroboogs.weibo.fragment.base.AbsTimeLineFragment;
import org.zarroboogs.weibo.loader.FriendsMsgLoader;
import org.zarroboogs.weibo.othercomponent.WifiAutoDownloadPictureRunnable;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.lib.LogOnExceptionScheduledExecutor;
import org.zarroboogs.weibo.support.utils.AppConfig;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.HeaderListView;
import org.zarroboogs.weibo.widget.TopTipsView;
import org.zarroboogs.weibo.widget.VelocityListView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lib.org.zarroboogs.weibo.login.utils.LogTool;

public class FriendsTimeLineFragment extends AbsTimeLineFragment<MessageListBean> implements
        GlobalContext.MyProfileInfoChangeListener,
        MainTimeLineActivity.ScrollableListFragment {

    private AccountBean mAccountBean;

    private UserBean mUserBean;

    private String mToken;

    private DBCacheTask mDBCacheTask;

    private ScheduledExecutorService mAutoRefreshExecutor = null;

    public final static String ALL_GROUP_ID = "0";

    public final static String BILATERAL_GROUP_ID = "1";

    private String currentGroupId = ALL_GROUP_ID;

    private HashMap<String, MessageListBean> groupDataCache = new HashMap<String, MessageListBean>();

    private HashMap<String, TimeLinePosition> positionCache = new HashMap<String, TimeLinePosition>();

    private MessageListBean bean = new MessageListBean();

    private BaseAdapter mBaseAdapter;

    private Thread mBackgroundWifiDownloadPicThread = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Toolbar mToolbar;

    public FriendsTimeLineFragment() {
        super();
    }

    public static FriendsTimeLineFragment newInstance(AccountBean accountBean, UserBean userBean, String token) {
        FriendsTimeLineFragment fragment = new FriendsTimeLineFragment(accountBean, userBean, token);
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public FriendsTimeLineFragment(AccountBean accountBean, UserBean userBean, String token) {
        this.mAccountBean = accountBean;
        this.mUserBean = userBean;
        this.mToken = token;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

    }

    @Override
    public MessageListBean getList() {
        return bean;
    }

    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
    // savedInstanceState) {
    // View view = inflater.inflate(R.layout.friends_time_line_listview_layout, container, false);
    // buildLayout(inflater, view);
    // return view;
    // }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.mainTimeLineToolBar);
        mToolbar.setTitle(R.string.weibo_home_page);
        mToolbar.inflateMenu(R.menu.actionbar_menu_friendstimelinefragment);
        // mToolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
        //
        // @Override
        // public boolean onMenuItemClick(MenuItem item) {
        //
        // switch (item.getItemId()) {
        // case R.id.write_weibo:
        // Class<?> clzz = null;
        // if (SettingUtils.isDebug()) {
        // clzz = WriteWeiboActivity.class;
        // } else {
        // clzz = WeiboMainActivity.class;
        // }
        // Intent intent = new Intent(getActivity(), clzz);
        // intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
        // intent.putExtra(Constants.TOKEN, mToken);
        // intent.putExtra(Constants.ACCOUNT, mAccountBean);
        // startActivity(intent);
        // break;
        // case R.id.refresh:
        // if (allowRefresh()) {
        // getPullToRefreshListView().setRefreshing();
        // loadNewMsg();
        // }
        // break;
        // case R.id.switch_theme:
        // // make sure activity has saved current left menu position
        // ((MainTimeLineActivity) getActivity()).saveNavigationPositionToDB();
        // SettingUtils.switchToAnotherTheme();
        // ((MainTimeLineActivity) getActivity()).reload();
        // break;
        // }
        // return false;
        // }
        // });
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
                        FriendsTimeLineDBTask.asyncUpdateCount(msg.getId(), msg.getComments_count(), msg.getReposts_count());
                        getAdapter().notifyDataSetChanged();
                    }
                    break;
                }
            }

        }
    }

    @Override
    protected void onListViewScrollStop() {
        savePositionToPositionsCache();
        startDownloadingOtherPicturesOnWifiNetworkEnvironment();

    }

    private void startDownloadingOtherPicturesOnWifiNetworkEnvironment() {
        if (mBackgroundWifiDownloadPicThread == null && Utility.isWifi(getActivity()) && SettingUtils.getEnableBigPic()
                && SettingUtils.isWifiAutoDownloadPic()) {
            final int position = getListView().getFirstVisiblePosition();
            int listVewOrientation = ((VelocityListView) getListView()).getTowardsOrientation();
            WifiAutoDownloadPictureRunnable runnable = new WifiAutoDownloadPictureRunnable(getList(), position,
                    listVewOrientation);
            mBackgroundWifiDownloadPicThread = new Thread(runnable);
            mBackgroundWifiDownloadPicThread.start();
            AppLoggerUtils.i("WifiAutoDownloadPictureRunnable startDownloadingOtherPicturesOnWifiNetworkEnvironment");

        }
    }

    @Override
    protected void onListViewScrollStateTouchScroll() {
        super.onListViewScrollStateTouchScroll();
        stopDownloadingOtherPicturesOnWifiNetworkEnvironment();
    }

    @Override
    protected void onListViewScrollStateFling() {
        super.onListViewScrollStateFling();
        stopDownloadingOtherPicturesOnWifiNetworkEnvironment();
    }

    private void stopDownloadingOtherPicturesOnWifiNetworkEnvironment() {
        if (mBackgroundWifiDownloadPicThread != null) {
            mBackgroundWifiDownloadPicThread.interrupt();
            mBackgroundWifiDownloadPicThread = null;
            AppLoggerUtils.i("WifiAutoDownloadPictureRunnable stopDownloadingOtherPicturesOnWifiNetworkEnvironment");

        }
    }

    private void savePositionToPositionsCache() {
        positionCache.put(currentGroupId, Utility.getCurrentPositionFromListView(getListView()));
    }

    private void saveNewMsgCountToPositionsCache() {
        final TimeLinePosition position = positionCache.get(currentGroupId);
        position.newMsgIds = newMsgTipBar.getValues();
    }

    private void setListViewPositionFromPositionsCache() {
        final TimeLinePosition p = positionCache.get(currentGroupId);
        Utility.setListViewSelectionFromTop(getListView(), p != null ? p.position : 0, p != null ? p.top : 0,
                new Runnable() {
                    @Override
                    public void run() {
                        setListViewUnreadTipBar(p);
                    }
                });
    }

    private void setListViewUnreadTipBar(TimeLinePosition p) {
        if (p != null && p.newMsgIds != null) {
            if (SettingUtils.getEnableAutoRefresh()) {
                newMsgTipBar.setType(TopTipsView.Type.ALWAYS);
            }
            newMsgTipBar.setValue(p.newMsgIds);
        }
    }

    // must create new position every time onpause, pulltorefresh wont call
    // onListViewScrollStop
    private void savePositionToDB() {
        savePositionToPositionsCache();
        TimeLinePosition position = positionCache.get(currentGroupId);
        position.newMsgIds = newMsgTipBar.getValues();
        final String groupId = currentGroupId;
        FriendsTimeLineDBTask.asyncUpdatePosition(position, GlobalContext.getInstance().getCurrentAccountId(), groupId);
    }

    private void saveGroupIdToDB() {
        FriendsTimeLineDBTask.asyncUpdateRecentGroupId(GlobalContext.getInstance().getCurrentAccountId(), currentGroupId);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!getActivity().isChangingConfigurations()) {
            savePositionToDB();
            saveGroupIdToDB();
        }
        removeRefresh();
        stopDownloadingOtherPicturesOnWifiNetworkEnvironment();
    }

    @Override
    public void onResume() {
        super.onResume();
        addRefresh();
        GlobalContext.getInstance().registerForAccountChangeListener(this);
        if (SettingUtils.getEnableAutoRefresh()) {
            this.newMsgTipBar.setType(TopTipsView.Type.ALWAYS);
        } else {
            this.newMsgTipBar.setType(TopTipsView.Type.AUTO);
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(switchReceiver,
                new IntentFilter(AppEventAction.SWITCH_WEIBO_GROUP_BROADCAST));
    }

    BroadcastReceiver switchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<GroupBean> list = new ArrayList<GroupBean>();
            if (GlobalContext.getInstance().getGroup() != null) {
                list = GlobalContext.getInstance().getGroup().getLists();
            } else {
                list = new ArrayList<GroupBean>();
            }
            final List<GroupBean> finalList = list;

            int intgroupId = intent.getIntExtra(RightMenuFragment.SWITCH_GROUP_KEY, 0);
            String groupId = getGroupIdFromIndex(intgroupId, finalList);
            Log.d("SwitchGroup", "" + intgroupId + "     " + groupId);
            switchFriendsGroup(groupId);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.cancelTasks(mDBCacheTask);
        GlobalContext.getInstance().unRegisterForAccountChangeListener(this);

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(switchReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.ACCOUNT, mAccountBean);
        outState.putParcelable(Constants.USERBEAN, mUserBean);
        outState.putString(Constants.TOKEN, mToken);

        // outState.putSerializable(Constants.BEAN, getList());
        // outState.putSerializable("groupDataCache", groupDataCache);
        // outState.putString("currentGroupId", currentGroupId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                if (Utility.isTaskStopped(mDBCacheTask) && getList().getSize() == 0) {
                    mDBCacheTask = new DBCacheTask(this, mAccountBean.getUid());
                    mDBCacheTask.executeOnIO();
                    GroupInfoTask groupInfoTask = new GroupInfoTask(GlobalContext.getInstance().getSpecialToken(),
                            GlobalContext.getInstance()
                                    .getCurrentAccountId());
                    groupInfoTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    getAdapter().notifyDataSetChanged();
                    refreshLayout(getList());
                }

                groupDataCache.put(ALL_GROUP_ID, new MessageListBean());
                groupDataCache.put(BILATERAL_GROUP_ID, new MessageListBean());

                if (getList().getSize() > 0) {
                    groupDataCache.put(ALL_GROUP_ID, getList().copy());
                }
                buildActionBarNav();

                break;
            case SCREEN_ROTATE:
                refreshLayout(getList());
                buildActionBarNav();
                setListViewPositionFromPositionsCache();
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                mUserBean = savedInstanceState.getParcelable(Constants.USERBEAN);
                mAccountBean = savedInstanceState.getParcelable(Constants.ACCOUNT);
                mToken = savedInstanceState.getString(Constants.TOKEN);

                if (Utility.isTaskStopped(mDBCacheTask) && getList().getSize() == 0) {
                    mDBCacheTask = new DBCacheTask(this, mAccountBean.getUid());
                    mDBCacheTask.executeOnIO();
                    GroupInfoTask groupInfoTask = new GroupInfoTask(GlobalContext.getInstance().getSpecialToken(),
                            GlobalContext.getInstance()
                                    .getCurrentAccountId());
                    groupInfoTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    getAdapter().notifyDataSetChanged();
                    refreshLayout(getList());
                }

                groupDataCache.put(ALL_GROUP_ID, new MessageListBean());
                groupDataCache.put(BILATERAL_GROUP_ID, new MessageListBean());

                if (getList().getSize() > 0) {
                    groupDataCache.put(ALL_GROUP_ID, getList().copy());
                }
                buildActionBarNav();

                break;
        }

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            buildActionBarNav();
            // ((MainTimeLineActivity) getActivity()).setCurrentFragment(this);
        }
    }

    @Override
    protected void buildListAdapter() {
        StatusListAdapter adapter = new StatusListAdapter(this, getList().getItemList(), getListView(), true, false);
        adapter.setTopTipBar(newMsgTipBar);
        timeLineAdapter = adapter;
        getListView().setAdapter(timeLineAdapter);
    }

    private int getIndexFromGroupId(String id, List<GroupBean> list) {

        if (list == null || list.size() == 0) {
            return 0;
        }

        int index = 0;

        if (id.equals("0")) {
            index = 0;
        } else if (id.equals("1")) {
            index = 1;
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdstr().equals(id)) {
                index = i + 2;
                break;
            }
        }
        return index;
    }

    private String getGroupIdFromIndex(int index, List<GroupBean> list) {
        String selectedItemId;

        if (index == 0) {
            selectedItemId = "0";
        } else if (index == 1) {
            selectedItemId = "1";
        } else {
            selectedItemId = list.get(index - 2).getIdstr();
        }
        return selectedItemId;
    }

    private String[] buildListNavData(List<GroupBean> list) {
        List<String> name = new ArrayList<String>();

        name.add(getString(R.string.all_people));
        name.add(getString(R.string.bilateral));

        for (GroupBean b : list) {
            name.add(b.getName());
        }

        String[] valueArray = name.toArray(new String[0]);
        return valueArray;
    }

    public void buildActionBarNav() {
        if ((((MainTimeLineActivity) getActivity()).getLeftMenuFragment()).getCurrentIndex() != LeftMenuFragment.HOME_INDEX) {
            return;
        }
        ((MainTimeLineActivity) getActivity()).setCurrentFragment(this);

        List<GroupBean> list = new ArrayList<GroupBean>();
        if (GlobalContext.getInstance().getGroup() != null) {
            list = GlobalContext.getInstance().getGroup().getLists();
        } else {
            list = new ArrayList<GroupBean>();
        }
        mBaseAdapter = new FriendsTimeLineListNavAdapter(getActivity(), buildListNavData(list));
        final List<GroupBean> finalList = list;
        // getActivity().getActionBar().setListNavigationCallbacks(mBaseAdapter, new
        // ActionBar.OnNavigationListener() {
        // @Override
        // public boolean onNavigationItemSelected(int which, long itemId) {
        //
        // if (!Utility.isTaskStopped(mDBCacheTask)) {
        // return true;
        // }
        //
        // String groupId = getGroupIdFromIndex(which, finalList);
        //
        // if (!groupId.equals(currentGroupId)) {
        //
        // switchFriendsGroup(groupId);
        // }
        // return true;
        // }
        // });
        currentGroupId = FriendsTimeLineDBTask.getRecentGroupId(GlobalContext.getInstance().getCurrentAccountId());

        LogTool.D("RecentGroupID Friend-asyncUpdateRecentGroupId : " + currentGroupId);
        
        // if (Utility.isDevicePort()) {
        // ((MainTimeLineActivity) getActivity()).setTitle("");
        // getBaseToolbar().setLogo(R.drawable.ic_menu_home);
        // } else {
        // ((MainTimeLineActivity) getActivity()).setTitle("");
        // getBaseToolbar().setLogo(R.drawable.beebo_launcher);
        // }

        // if (getActivity().getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST &&
        // isVisible()) {
        // getActivity().getActionBar().setSelectedNavigationItem(getRecentNavIndex());
        // }

    }

    @Override
    public void onChange(UserBean newUserBean) {
        if (mBaseAdapter != null) {
            mBaseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void scrollToTop() {
        Utility.stopListViewScrollingAndScrollToTop(getListView());
    }

    private void handleDBCacheOnProgressUpdateData(MessageTimeLineData[] result) {
        if (result != null && result.length > 0) {
            MessageTimeLineData recentData = result[0];
            getList().replaceData(recentData.msgList);
            putToGroupDataMemoryCache(recentData.groupId, recentData.msgList);
            positionCache.put(recentData.groupId, recentData.position);
            currentGroupId = recentData.groupId;
        }
        getPullToRefreshListView().setVisibility(View.VISIBLE);
        getAdapter().notifyDataSetChanged();
        setListViewPositionFromPositionsCache();
        // if (getActivity().getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST) {
        // getActivity().getActionBar().setSelectedNavigationItem(getRecentNavIndex());
        // }
        refreshLayout(getList());
        /**
         * when this account first open app,if he don't have any data in database,fetch data from
         * server automally
         */
        if (getList().getSize() == 0) {
            getPullToRefreshListView().setRefreshing();
            loadNewMsg();
        } else {
            new RefreshReCmtCountTask(FriendsTimeLineFragment.this, getList())
                    .executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void handleDBCacheResultData(List<MessageTimeLineData> result) {
        for (MessageTimeLineData single : result) {
            putToGroupDataMemoryCache(single.groupId, single.msgList);
            positionCache.put(single.groupId, single.position);
        }
    }

    @Override
    public void loadNewMsg() {
        super.loadNewMsg();
        new RefreshReCmtCountTask(FriendsTimeLineFragment.this, getList())
                .executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class DBCacheTask extends MyAsyncTask<Void, MessageTimeLineData, List<MessageTimeLineData>> {

        private WeakReference<FriendsTimeLineFragment> fragmentWeakReference;

        private String accountId;

        private DBCacheTask(FriendsTimeLineFragment friendsTimeLineFragment, String accountId) {
            fragmentWeakReference = new WeakReference<FriendsTimeLineFragment>(friendsTimeLineFragment);
            this.accountId = accountId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FriendsTimeLineFragment fragment = fragmentWeakReference.get();
            if (fragment != null) {
                fragment.getPullToRefreshListView().setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected List<MessageTimeLineData> doInBackground(Void... params) {
            MessageTimeLineData recentGroupData = FriendsTimeLineDBTask.getRecentGroupData(accountId);
            publishProgress(recentGroupData);
            return FriendsTimeLineDBTask.getOtherGroupData(accountId, recentGroupData.groupId);
        }

        @Override
        protected void onPostExecute(List<MessageTimeLineData> result) {
            super.onPostExecute(result);
            FriendsTimeLineFragment fragment = fragmentWeakReference.get();

            if (fragment == null) {
                return;
            }

            if (fragment.getActivity() == null) {
                return;
            }

            if (result != null && result.size() > 0) {
                fragment.handleDBCacheResultData(result);
            }
        }

        @Override
        protected void onProgressUpdate(MessageTimeLineData... result) {
            super.onProgressUpdate(result);

            FriendsTimeLineFragment fragment = fragmentWeakReference.get();

            if (fragment == null) {
                return;
            }

            if (fragment.getActivity() == null) {
                return;
            }

            fragment.handleDBCacheOnProgressUpdateData(result);

        }
    }

    private int getRecentNavIndex() {
        List<GroupBean> list = new ArrayList<GroupBean>();
        if (GlobalContext.getInstance().getGroup() != null) {
            list = GlobalContext.getInstance().getGroup().getLists();
        } else {
            list = new ArrayList<GroupBean>();
        }
        return getIndexFromGroupId(currentGroupId, list);
    }

    @Override
    protected void onTimeListViewItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent mIntent = BrowserWeiboMsgActivity.newIntent(GlobalContext.getInstance().getAccountBean(),
                getList().getItem(position), GlobalContext
                        .getInstance().getSpecialToken());
        mIntent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
        startActivityForResult(mIntent, MainTimeLineActivity.REQUEST_CODE_UPDATE_FRIENDS_TIMELINE_COMMENT_REPOST_COUNT);

    }

    public void setSelected(String selectedItemId) {
        currentGroupId = selectedItemId;
    }

    @Override
    protected void newMsgLoaderSuccessCallback(MessageListBean newValue, Bundle loaderArgs) {
        if (Utility.isAllNotNull(getActivity(), newValue) && newValue.getSize() > 0) {
            if (loaderArgs != null && loaderArgs.getBoolean(BundleArgsConstants.AUTO_REFRESH, false)) {
                addNewDataAndRememberPositionAutoRefresh(newValue);
            } else {
                boolean scrollToTop = SettingUtils.isReadStyleEqualWeibo();
                if (scrollToTop) {
                    addNewDataWithoutRememberPosition(newValue);
                } else {
                    addNewDataAndRememberPosition(newValue);
                }
            }
            putToGroupDataMemoryCache(currentGroupId, getList());
            FriendsTimeLineDBTask.asyncReplace(getList(), mAccountBean.getUid(), currentGroupId);
        }
    }

    @SuppressLint("NewApi")
    private void addNewDataAndRememberPositionAutoRefresh(final MessageListBean newValue) {

        int initSize = getList().getSize();

        if (getActivity() != null && newValue.getSize() > 0) {

            if (hasActionMode()) {
                AppLoggerUtils.v("ListView have ActionMode, skip notifyDataSetChanged");
                return;
            }

            final HeaderListView headerListView = (HeaderListView) getListView();
            View firstChildView = getListView().getChildAt(0);
            boolean isFirstViewHeader = headerListView.isThisViewHeader(firstChildView);

            if (isFirstViewHeader && !headerListView.isInTouchByUser()) {

                // //Overlay shi Android 4.3 method
                if (!Utility.isJB2()) {
                    getList().addNewData(newValue);
                    getAdapter().notifyDataSetChanged();
                    Utility.setListViewSelectionFromTop(getListView(), 0, 0);
                    return;
                }

                // animate add item

                final ListView listView = getListView();

                final ArrayList<Pair<Long, Bitmap>> previousViewsBitmap = new ArrayList<Pair<Long, Bitmap>>();
                final HashMap<Long, Integer> previousViewsTop = new HashMap<Long, Integer>();
                final HashMap<Long, View> previousViews = new HashMap<Long, View>();

                int childCount = listView.getChildCount();

                for (int i = 0; i < childCount; i++) {
                    View childView = listView.getChildAt(i);
                    if (headerListView.isThisViewHeader(childView)) {
                        continue;
                    }

                    int firstAdapterItemPosition = listView.getFirstVisiblePosition();
                    int currentAdapterItemPosition = firstAdapterItemPosition + i - listView.getHeaderViewsCount();

                    long currentAdapterItemId = getAdapter().getItemId(currentAdapterItemPosition);
                    int childViewTop = childView.getTop();
                    Bitmap bitmap = Utility.getBitmapFromView(childView);

                    Pair<Long, Bitmap> pair = new Pair<Long, Bitmap>(currentAdapterItemId, bitmap);
                    previousViewsBitmap.add(pair);

                    previousViewsTop.put(currentAdapterItemId, childViewTop);
                    childView.setHasTransientState(true);
                    previousViews.put(currentAdapterItemId, childView);

                }

                getList().addNewData(newValue);

                getAdapter().notifyDataSetChanged();

                getListView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {

                        listView.getViewTreeObserver().removeOnPreDrawListener(this);

                        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

                        final ViewGroupOverlay overlay = listView.getOverlay();

                        Set<Long> previousViewsId = previousViews.keySet();

                        boolean somePreviousViewsAreStillInScreen = false;

                        ArrayList<View> newAddedItemViews = new ArrayList<View>();

                        int deltaY = 0;

                        int childCount = listView.getChildCount();

                        for (int i = 0; i < childCount; i++) {
                            View childView = listView.getChildAt(i);
                            if (headerListView.isThisViewHeader(childView)) {
                                continue;
                            }

                            int firstAdapterItemPosition = listView.getFirstVisiblePosition();
                            int currentAdapterItemPosition = firstAdapterItemPosition + i - listView.getHeaderViewsCount();

                            long currentAdapterItemId = getAdapter().getItemId(currentAdapterItemPosition);

                            if (previousViewsId.contains(currentAdapterItemId)) {
                                somePreviousViewsAreStillInScreen = true;
                                deltaY = childView.getTop() - previousViewsTop.get(currentAdapterItemId);
                            } else {
                                newAddedItemViews.add(childView);
                            }

                        }

                        if (!somePreviousViewsAreStillInScreen) {
                            deltaY = listView.getHeight();
                        }

                        for (View view : newAddedItemViews) {
                            view.setTranslationY(-deltaY);
                            view.animate().translationY(0).setInterpolator(decelerateInterpolator);
                        }

                        for (Pair<Long, Bitmap> pair : previousViewsBitmap) {
                            long id = pair.first;
                            int top = previousViewsTop.get(id);
                            final View view = previousViews.get(id);
                            final Bitmap bitmap = pair.second;
                            final ImageView imageView = new ImageView(getActivity());
                            imageView.setImageBitmap(bitmap);
                            imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                            imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
                            overlay.add(imageView);
                            imageView.setTranslationY(top);
                            view.setAlpha(0);
                            imageView.animate().translationY(top + deltaY).setInterpolator(decelerateInterpolator)
                                    .setListener(new MyAnimationListener(new Runnable() {
                                        @Override
                                        public void run() {
                                            overlay.remove(imageView);
                                            bitmap.recycle();
                                            view.setAlpha(1.0f);
                                            view.setHasTransientState(false);
                                        }
                                    }));
                        }

                        return true;
                    }
                });

                return;
            }

            getList().addNewData(newValue);

            Runnable endAction = new Runnable() {
                @Override
                public void run() {
                    newMsgTipBar.setValue(newValue, false);
                    newMsgTipBar.setType(TopTipsView.Type.ALWAYS);
                }
            };

            // keep current read position when user touch listview
            if (isFirstViewHeader && headerListView.isInTouchByUser()) {
                int index = getListView().getFirstVisiblePosition();
                getAdapter().notifyDataSetChanged();
                int finalSize = getList().getSize();
                final int positionAfterRefresh = index + finalSize - initSize + getListView().getHeaderViewsCount();
                // use 1 px to show newMsgTipBar
                Utility.setListViewSelectionFromTop(getListView(), positionAfterRefresh, 1, endAction);
                return;
            }

            // need to calc the first child view's top distance
            int index = getListView().getFirstVisiblePosition();

            View firstAdapterItemView = Utility.getListViewFirstAdapterItemView(getListView());
            final int top = (firstAdapterItemView == null) ? 0 : firstAdapterItemView.getTop();

            getAdapter().notifyDataSetChanged();
            int finalSize = getList().getSize();
            final int positionAfterRefresh = index + finalSize - initSize;
            Utility.setListViewSelectionFromTop(getListView(), positionAfterRefresh, top, endAction);

        }

    }

    private void addNewDataAndRememberPosition(final MessageListBean newValue) {

        int initSize = getList().getSize();

        if (getActivity() != null && newValue.getSize() > 0) {
            getList().addNewData(newValue);
            int index = getListView().getFirstVisiblePosition();
            getAdapter().notifyDataSetChanged();
            int finalSize = getList().getSize();
            final int positionAfterRefresh = index + finalSize - initSize + getListView().getHeaderViewsCount();
            // use 1 px to show newMsgTipBar
            Utility.setListViewSelectionFromTop(getListView(), positionAfterRefresh, 1, new Runnable() {

                @Override
                public void run() {
                    newMsgTipBar.setValue(newValue, false);
                    newMsgTipBar.setType(TopTipsView.Type.AUTO);
                }
            });

        }

    }

    protected void middleMsgLoaderSuccessCallback(int position, MessageListBean newValue, boolean towardsBottom) {

        if (newValue == null) {
            return;
        }

        if (getActivity() != null && newValue.getSize() > 0) {
            getList().addMiddleData(position, newValue, towardsBottom);

            int finalSize = getList().getSize();

            if (towardsBottom || SettingUtils.isReadStyleEqualWeibo()) {
                getAdapter().notifyDataSetChanged();
            } else {
                int initSize = getList().getSize();
                int index = getListView().getFirstVisiblePosition();
                View v = Utility.getListViewFirstAdapterItemView(getListView());
                final int top = (v == null) ? 0 : v.getTop();
                getAdapter().notifyDataSetChanged();
                final int positionAfterRefresh = index + finalSize - initSize;
                Utility.setListViewSelectionFromTop(getListView(), positionAfterRefresh, top);
            }

        }
    }

    private void addNewDataWithoutRememberPosition(MessageListBean newValue) {
        newMsgTipBar.setValue(newValue, true);

        getList().addNewData(newValue);
        getAdapter().notifyDataSetChanged();
        getListView().setSelectionAfterHeaderView();
    }

    @Override
    protected void oldMsgLoaderSuccessCallback(MessageListBean oldValue) {
        if (Utility.isAllNotNull(getActivity(), oldValue) && oldValue.getSize() > 1) {
            getList().addOldData(oldValue);
            putToGroupDataMemoryCache(currentGroupId, getList());
            FriendsTimeLineDBTask.asyncReplace(getList(), mAccountBean.getUid(), currentGroupId);

        } else if (Utility.isAllNotNull(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.older_message_empty), Toast.LENGTH_SHORT).show();
        }
    }

    public void switchFriendsGroup(String groupId) {
        getLoaderManager().destroyLoader(NEW_MSG_LOADER_ID);
        getLoaderManager().destroyLoader(MIDDLE_MSG_LOADER_ID);
        getLoaderManager().destroyLoader(OLD_MSG_LOADER_ID);
        getPullToRefreshListView().onRefreshComplete();
        dismissFooterView();
        savedCurrentLoadingMsgViewPositon = -1;
        if (timeLineAdapter instanceof AbstractAppListAdapter) {
            ((AbstractAppListAdapter) timeLineAdapter).setSavedMiddleLoadingViewPosition(savedCurrentLoadingMsgViewPositon);
        }

        positionCache.put(currentGroupId, Utility.getCurrentPositionFromListView(getListView()));
        saveNewMsgCountToPositionsCache();
        setSelected(groupId);
        newMsgTipBar.clearAndReset();
        if (groupDataCache.get(currentGroupId) == null || groupDataCache.get(currentGroupId).getSize() == 0) {
            getList().getItemList().clear();
            getAdapter().notifyDataSetChanged();
            getPullToRefreshListView().setRefreshing();
            loadNewMsg();

        } else {
            getList().replaceData(groupDataCache.get(currentGroupId));
            getAdapter().notifyDataSetChanged();
            setListViewPositionFromPositionsCache();
            saveGroupIdToDB();
            new RefreshReCmtCountTask(this, getList()).executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void putToGroupDataMemoryCache(String groupId, MessageListBean value) {
        MessageListBean copy = new MessageListBean();
        copy.addNewData(value);
        groupDataCache.put(groupId, copy);
    }

    private void removeRefresh() {
        if (mAutoRefreshExecutor != null && !mAutoRefreshExecutor.isShutdown()) {
            mAutoRefreshExecutor.shutdownNow();
        }
    }

    protected void addRefresh() {

        mAutoRefreshExecutor = new LogOnExceptionScheduledExecutor(1);
        mAutoRefreshExecutor.scheduleAtFixedRate(new AutoTask(), AppConfig.AUTO_REFRESH_INITIALDELAY,
                AppConfig.AUTO_REFRESH_PERIOD, TimeUnit.SECONDS);

    }

    private class AutoTask implements Runnable {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (getActivity() == null) {
                        return;
                    }

                    if (hasActionMode()) {
                        return;
                    }

                    if (!SettingUtils.getEnableAutoRefresh()) {
                        return;
                    }

                    if (!Utility.isTaskStopped(mDBCacheTask)) {
                        return;
                    }

                    if (!allowRefresh()) {
                        return;
                    }
                    if (!Utility.isWifi(getActivity())) {
                        return;
                    }
                    if (isListViewFling() || !isVisible()/*
                                                          * || ((MainTimeLineActivity)
                                                          * getActivity())
                                                          * .getSlidingMenu().isMenuShowing()
                                                          */) {
                        return;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(BundleArgsConstants.SCROLL_TO_TOP, false);
                    bundle.putBoolean(BundleArgsConstants.AUTO_REFRESH, true);
                    getLoaderManager().restartLoader(NEW_MSG_LOADER_ID, bundle, msgAsyncTaskLoaderCallback);
                }
            });

        }
    }

    /**
     * refresh timline messages' repost and comment count
     */
    private static class RefreshReCmtCountTask extends
            MyAsyncTask<Void, List<MessageReCmtCountBean>, List<MessageReCmtCountBean>> {

        private List<String> msgIds;

        private WeakReference<FriendsTimeLineFragment> fragmentWeakReference;

        private RefreshReCmtCountTask(FriendsTimeLineFragment friendsTimeLineFragment, MessageListBean data) {
            fragmentWeakReference = new WeakReference<FriendsTimeLineFragment>(friendsTimeLineFragment);
            msgIds = new ArrayList<String>();
            List<MessageBean> msgList = data.getItemList();
            for (MessageBean msg : msgList) {
                if (msg != null) {
                    msgIds.add(msg.getId());
                }
            }
        }

        @Override
        protected List<MessageReCmtCountBean> doInBackground(Void... params) {

            if (msgIds.size() == 0) {
                return null;
            }

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
            FriendsTimeLineFragment fragment = fragmentWeakReference.get();
            if (fragment == null || value == null || value.size() == 0) {
                return;
            }
            fragment.updateTimeLineMessageCommentAndRepostData(value);
        }

    }

    private void updateTimeLineMessageCommentAndRepostData(List<MessageReCmtCountBean> value) {

        if (value == null) {
            return;
        }

        HashMap<String, MessageReCmtCountBean> messageReCmtCountBeanHashMap = new HashMap<String, MessageReCmtCountBean>();

        for (MessageReCmtCountBean count : value) {
            messageReCmtCountBeanHashMap.put(count.getId(), count);
        }

        for (int i = 0; i < getList().getSize(); i++) {
            MessageBean msg = getList().getItem(i);

            if (msg == null) {
                continue;
            }

            MessageReCmtCountBean count = messageReCmtCountBeanHashMap.get(msg.getId());
            if (count != null) {
                msg.setReposts_count(count.getReposts());
                msg.setComments_count(count.getComments());
            }
        }
        getAdapter().notifyDataSetChanged();
        FriendsTimeLineDBTask.asyncReplace(getList(), mAccountBean.getUid(), currentGroupId);
    }

    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String accountId = mAccountBean.getUid();
        String token = mAccountBean.getAccess_token();
        String sinceId = null;
        if (getList().getItemList().size() > 0) {
            sinceId = getList().getItemList().get(0).getId();
        }
        return new FriendsMsgLoader(getActivity(), accountId, token, currentGroupId, sinceId, null);
    }

    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateMiddleMsgLoader(int id, Bundle args,
            String middleBeginId, String middleEndId,
            String middleEndTag, int middlePosition) {
        String accountId = mAccountBean.getUid();
        String token = mAccountBean.getAccess_token();
        return new FriendsMsgLoader(getActivity(), accountId, token, currentGroupId, middleBeginId, middleEndId);
    }

    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String accountId = mAccountBean.getUid();
        String token = mAccountBean.getAccess_token();
        String maxId = null;
        if (getList().getItemList().size() > 0) {
            maxId = getList().getItemList().get(getList().getItemList().size() - 1).getId();
        }
        return new FriendsMsgLoader(getActivity(), accountId, token, currentGroupId, null, maxId);
    }
}
