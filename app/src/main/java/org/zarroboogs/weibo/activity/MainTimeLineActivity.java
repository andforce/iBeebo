
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.CommentListBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.bean.UnreadBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.db.DatabaseManager;
import org.zarroboogs.weibo.db.task.AccountDao;
import org.zarroboogs.weibo.fragment.MainTimeLineFragment;
import org.zarroboogs.weibo.fragment.HotHuaTiViewPagerFragment;
import org.zarroboogs.weibo.fragment.HotWeiboViewPagerFragment;
import org.zarroboogs.weibo.fragment.LeftMenuFragment;
import org.zarroboogs.weibo.fragment.MyFavListFragment;
import org.zarroboogs.weibo.fragment.RightMenuFragment;
import org.zarroboogs.weibo.othercomponent.BeeboAlermManager;
import org.zarroboogs.weibo.othercomponent.ConnectionChangeReceiver;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.lib.RecordOperationAppBroadcastReceiver;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

public class MainTimeLineActivity extends AbstractAppActivity {

    public static final int REQUEST_CODE_UPDATE_FRIENDS_TIMELINE_COMMENT_REPOST_COUNT = 0;

    public static final int REQUEST_CODE_UPDATE_MENTIONS_WEIBO_TIMELINE_COMMENT_REPOST_COUNT = 1;

    public static final int REQUEST_CODE_UPDATE_MY_FAV_TIMELINE_COMMENT_REPOST_COUNT = 2;

    private NewMsgInterruptBroadcastReceiver newMsgInterruptBroadcastReceiver;

    private ScrollableListFragment currentFragment;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    private static MenuItem sMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        super.onCreate(savedInstanceState);
        WeiBoURLs.buildUA();

        if (savedInstanceState != null) {
            mAccountBean = savedInstanceState.getParcelable(Constants.ACCOUNT);
        } else {
            Intent intent = getIntent();
            mAccountBean = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        }

        if (mAccountBean == null) {
            mAccountBean = BeeboApplication.getInstance().getAccountBean();
        }

        if (mAccountBean != null) {
            BeeboApplication.getInstance().setGroup(null);
            BeeboApplication.getInstance().setAccountBean(mAccountBean);
            SettingUtils.setDefaultAccountId(mAccountBean.getUid());

            setContentView(R.layout.main_time_line_activity);

            buildInterface(savedInstanceState);

            if (BeeboAlermManager.DEBUG) {
                BeeboAlermManager.startAlarm(BeeboAlermManager.DEBUG, getApplicationContext(), true);
            }
            BeeboAlermManager.keepCookie(getApplicationContext(), mAccountBean.getCookie());
        } else {
            Intent start = new Intent(this, AccountActivity.class);
            startActivity(start);
            finish();
        }
    }

    public void closeLeftDrawer() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void closeRight() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
    }

    public void openRight() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
    }

    public void closeRightDrawer() {
        mDrawerLayout.closeDrawer(Gravity.RIGHT);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void showMenuOnToolBar(final Toolbar toolbar, final int menuRes) {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(menuRes);
                sMenuItem = toolbar.getMenu().findItem(R.id.notify_menu);
            }
        }, 200);
    }

    public void showMenuOnToolBar(int menu) {
        showMenuOnToolBar(mToolbar, menu);

        mToolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                int id = arg0.getItemId();
                switch (id) {
                    case R.id.search_menu: {
                        Intent intent = new Intent(MainTimeLineActivity.this, SearchMainActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.notify_menu: {
                        notifyHandler.sendEmptyMessage(NOTIFY_STOP);
                        Intent intent = new Intent(MainTimeLineActivity.this, NotifyActivity.class);
                        startActivity(intent);

                        break;
                    }

                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void buildInterface(Bundle savedInstanceState) {

        mToolbar = (Toolbar) findViewById(R.id.mainTimeLineToolBar);
        mToolbar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                scrollCurrentListViewToTop();
            }
        });

        showMenuOnToolBar(R.menu.main_time_line_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.writeWeiboDrawerL);
        ActionBarDrawerToggle drawerToggle = new MyDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerToggle.setToolbarNavigationClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("onOptionsItemSelected", " setToolbarNavigationClickListener");
            }
        });
        mDrawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            initFragments();
            FragmentTransaction secondFragmentTransaction = getSupportFragmentManager().beginTransaction();
            secondFragmentTransaction.replace(R.id.left_drawer_layout, getLeftMenuFragment(), LeftMenuFragment.class.getName());
            secondFragmentTransaction.commit();

            FragmentTransaction mainTransaction = getSupportFragmentManager().beginTransaction();
            mainTransaction.replace(R.id.right_drawer_layout, getRightMenuFragment(), RightMenuFragment.class.getName());
            mainTransaction.commit();
        }
    }

    class MyDrawerToggle extends ActionBarDrawerToggle {

        public MyDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes,
                              int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            LocalBroadcastManager.getInstance(MainTimeLineActivity.this).sendBroadcast(
                    new Intent(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
            Log.d("onOptionsItemSelected", " onDrawerClosed ");
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            Log.d("onOptionsItemSelected", " onDrawerOpened");
            if (mDrawerLayout.isDrawerOpen(findViewById(R.id.left_drawer_layout))) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }
            getLeftMenuFragment().displayCover();
        }
    }

    private void initFragments() {
        Fragment timeLineFragment = getMainTimeLineFragment();
        Fragment favFragment = getFavFragment();

        Fragment hotWeiboFragment = getHotWeiboViewPagerFragment();

        Fragment hotHuatiFragment = getHotHuaTiViewPagerFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!timeLineFragment.isAdded()) {
            fragmentTransaction.add(R.id.center_frame_layout, timeLineFragment, MainTimeLineFragment.class.getName());
            fragmentTransaction.hide(timeLineFragment);
        }

        if (!favFragment.isAdded()) {
            fragmentTransaction.add(R.id.center_frame_layout, favFragment, MyFavListFragment.class.getName());
            fragmentTransaction.hide(favFragment);
        }

        if (!hotWeiboFragment.isAdded()) {
            fragmentTransaction.add(R.id.center_frame_layout, hotWeiboFragment, HotWeiboViewPagerFragment.class.getName());
            fragmentTransaction.hide(hotWeiboFragment);
        }

        if (!hotHuatiFragment.isAdded()) {
            fragmentTransaction.add(R.id.center_frame_layout, hotHuatiFragment, HotHuaTiViewPagerFragment.class.getName());
            fragmentTransaction.hide(hotHuatiFragment);
        }

        if (!fragmentTransaction.isEmpty()) {
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    private void scrollCurrentListViewToTop() {
        if (this.currentFragment != null) {
            this.currentFragment.scrollToTop();
        }
    }

    public void setCurrentFragment(ScrollableListFragment fragment) {
        this.currentFragment = fragment;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseManager.close();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AccountBean intentAccountBean = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        if (intentAccountBean == null) {
            return;
        }

        if (mAccountBean.equals(intentAccountBean)) {
            mAccountBean = intentAccountBean;
            BeeboApplication.getInstance().setAccountBean(mAccountBean);
            setIntent(intent);
        } else {
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BeeboApplication.getInstance().getBitmapCache().evictAll();
        finish();
    }

    public UserBean getUser() {
        return mAccountBean.getInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(AppEventAction.UnRead_Message_Action);
        filter.setPriority(1);


        newMsgInterruptBroadcastReceiver = new NewMsgInterruptBroadcastReceiver();
        Utility.registerReceiverIgnoredReceiverHasRegisteredHereException(this, newMsgInterruptBroadcastReceiver, filter);

        // ensure timeline picture type is correct
        ConnectionChangeReceiver.judgeNetworkStatus(this, false);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Utility.unregisterReceiverIgnoredReceiverNotRegisteredException(this, newMsgInterruptBroadcastReceiver);

        if (isFinishing()) {
            saveNavigationPositionToDB();
        }
    }

    public void saveNavigationPositionToDB() {
        int navPosition = getLeftMenuFragment().getCurrentIndex() * 10;
        int second = 0;
        int result = navPosition + second;
        BeeboApplication.getInstance().getAccountBean().setNavigationPosition(result);
        AccountDao.updateNavigationPosition(BeeboApplication.getInstance().getAccountBean(), result);
    }

    public LeftMenuFragment getLeftMenuFragment() {
        LeftMenuFragment fragment = ((LeftMenuFragment) getSupportFragmentManager().findFragmentByTag(
                LeftMenuFragment.class.getName()));
        if (fragment == null) {
            fragment = LeftMenuFragment.newInstance();
        }
        return fragment;
    }

    public RightMenuFragment getRightMenuFragment() {
        RightMenuFragment fragment = ((RightMenuFragment) getSupportFragmentManager().findFragmentByTag(
                RightMenuFragment.class.getName()));
        if (fragment == null) {
            fragment = RightMenuFragment.newInstance();
        }
        return fragment;
    }

    public MainTimeLineFragment getMainTimeLineFragment() {
        MainTimeLineFragment fragment = ((MainTimeLineFragment) getSupportFragmentManager().findFragmentByTag(
                MainTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = MainTimeLineFragment.newInstance(getAccount(), getUser(), getToken());
        }
        return fragment;
    }

    public HotWeiboViewPagerFragment getHotWeiboViewPagerFragment() {
        HotWeiboViewPagerFragment fragment = ((HotWeiboViewPagerFragment) getSupportFragmentManager().findFragmentByTag(
                HotWeiboViewPagerFragment.class.getName()));
        if (fragment == null) {
            fragment = HotWeiboViewPagerFragment.newInstance();
        }
        return fragment;
    }

    public HotHuaTiViewPagerFragment getHotHuaTiViewPagerFragment() {
        HotHuaTiViewPagerFragment fragment = ((HotHuaTiViewPagerFragment) getSupportFragmentManager().findFragmentByTag(
                HotHuaTiViewPagerFragment.class.getName()));
        if (fragment == null) {
            fragment = HotHuaTiViewPagerFragment.newInstance();
        }
        return fragment;
    }


    public MyFavListFragment getFavFragment() {
        MyFavListFragment fragment = ((MyFavListFragment) getSupportFragmentManager().findFragmentByTag(
                MyFavListFragment.class.getName()));
        if (fragment == null) {
            fragment = MyFavListFragment.newInstance();
        }
        return fragment;
    }

    private class NewMsgInterruptBroadcastReceiver extends RecordOperationAppBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AccountBean intentAccount = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
            if (mAccountBean.equals(intentAccount)) {
                MessageListBean mentionsWeibo = intent.getParcelableExtra(BundleArgsConstants.MENTIONS_WEIBO_EXTRA);
                CommentListBean mentionsComment = intent.getParcelableExtra(BundleArgsConstants.MENTIONS_COMMENT_EXTRA);
                CommentListBean commentsToMe = intent.getParcelableExtra(BundleArgsConstants.COMMENTS_TO_ME_EXTRA);
                int unreadCount = (mentionsWeibo != null ? mentionsWeibo.getSize() : 0)
                        + (mentionsComment != null ? mentionsComment.getSize() : 0)
                        + (commentsToMe != null ? commentsToMe.getSize() : 0);
                if (!inNotify) {
                    notifyHandler.sendEmptyMessage(NOTIFY_ON);
                }
                abortBroadcast();
            }
        }
    }

    private static final int NOTIFY_ON = 0x1000;
    private static final int NOTIFY_OFF = 0x1001;
    private static final int NOTIFY_STOP = 0x2000;
    private static boolean inNotify = false;

    private NotifyHandler notifyHandler = new NotifyHandler(this);

    private static class NotifyHandler extends Handler {
        private WeakReference<MainTimeLineActivity> mActivity;

        public NotifyHandler(MainTimeLineActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NOTIFY_ON: {
                    inNotify = true;
                    sMenuItem.setIcon(R.drawable.ic_notifications_on_white_24dp);
                    this.sendEmptyMessageDelayed(NOTIFY_OFF, 1000);
                    break;
                }
                case NOTIFY_OFF: {
                    inNotify = true;
                    sMenuItem.setIcon(R.drawable.ic_notifications_none_white_24dp);
                    this.sendEmptyMessageDelayed(NOTIFY_ON, 1000);
                    break;
                }
                case NOTIFY_STOP: {
                    inNotify = false;
                    this.removeMessages(NOTIFY_ON);
                    this.removeMessages(NOTIFY_OFF);
                    sMenuItem.setIcon(R.drawable.ic_notifications_none_white_24dp);
                    break;
                }
            }
        }

    }

    public interface ScrollableListFragment {

        void scrollToTop();
    }

    public static Intent newIntent() {
        return new Intent(BeeboApplication.getInstance(), MainTimeLineActivity.class);
    }

    public static Intent newIntent(AccountBean accountBean) {
        Intent intent = newIntent();
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, accountBean);
        return intent;
    }

    /**
     * 未读信息的Intent
     *
     * @param accountBean
     * @param mentionsWeiboData
     * @param mentionsCommentData
     * @param commentsToMeData
     * @param unreadBean
     * @return
     */
    public static Intent unReadIntent(AccountBean accountBean, MessageListBean mentionsWeiboData,
                                      CommentListBean mentionsCommentData,
                                      CommentListBean commentsToMeData, UnreadBean unreadBean) {
        Intent intent = new Intent(BeeboApplication.getInstance(), NotifyActivity.class);
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, accountBean);
        intent.putExtra(BundleArgsConstants.MENTIONS_WEIBO_EXTRA, mentionsWeiboData);
        intent.putExtra(BundleArgsConstants.MENTIONS_COMMENT_EXTRA, mentionsCommentData);
        intent.putExtra(BundleArgsConstants.COMMENTS_TO_ME_EXTRA, commentsToMeData);
        intent.putExtra(BundleArgsConstants.UNREAD_EXTRA, unreadBean);

        intent.putExtra(BundleArgsConstants.FromUnReadIntent, true);
        return intent;
    }

    public String getToken() {
        return mAccountBean.getAccess_token();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.ACCOUNT, mAccountBean);
    }
}
