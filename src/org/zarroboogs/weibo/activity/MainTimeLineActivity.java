
package org.zarroboogs.weibo.activity;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ViewTarget;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.CommentListBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.bean.UnreadBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.db.DatabaseManager;
import org.zarroboogs.weibo.db.task.AccountDBTask;
import org.zarroboogs.weibo.fragment.CommentsTimeLineFragment;
import org.zarroboogs.weibo.fragment.DMUserListFragment;
import org.zarroboogs.weibo.fragment.FriendsTimeLineFragment;
import org.zarroboogs.weibo.fragment.LeftMenuFragment;
import org.zarroboogs.weibo.fragment.AtMeTimeLineFragment;
import org.zarroboogs.weibo.fragment.MyFavListFragment;
import org.zarroboogs.weibo.fragment.RightMenuFragment;
import org.zarroboogs.weibo.fragment.SearchMainParentFragment;
import org.zarroboogs.weibo.fragment.UserInfoFragment;
import org.zarroboogs.weibo.othercomponent.AppNewMsgAlarm;
import org.zarroboogs.weibo.othercomponent.ConnectionChangeReceiver;
import org.zarroboogs.weibo.othercomponent.MusicReceiver;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.lib.RecordOperationAppBroadcastReceiver;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainTimeLineActivity extends AbstractAppActivity {

    public static final int REQUEST_CODE_UPDATE_FRIENDS_TIMELINE_COMMENT_REPOST_COUNT = 0;

    public static final int REQUEST_CODE_UPDATE_MENTIONS_WEIBO_TIMELINE_COMMENT_REPOST_COUNT = 1;

    public static final int REQUEST_CODE_UPDATE_MY_FAV_TIMELINE_COMMENT_REPOST_COUNT = 2;

    private NewMsgInterruptBroadcastReceiver newMsgInterruptBroadcastReceiver;

    private MusicReceiver musicReceiver;

    private ScrollableListFragment currentFragment;

    private TextView titleText;

    private View clickToTop;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Button mScrollTopBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UmengUpdateAgent.update(this);

        if (savedInstanceState != null) {
            mAccountBean = savedInstanceState.getParcelable(Constants.ACCOUNT);
        } else {
            Intent intent = getIntent();
            mAccountBean = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        }

        if (mAccountBean == null) {
            mAccountBean = GlobalContext.getInstance().getAccountBean();
        }

        GlobalContext.getInstance().setGroup(null);
        GlobalContext.getInstance().setAccountBean(mAccountBean);
        SettingUtils.setDefaultAccountId(mAccountBean.getUid());

        buildInterface(savedInstanceState);

        mScrollTopBtn = (Button) findViewById(R.id.scrollToTopBtn);
        mScrollTopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "TOP", Toast.LENGTH_LONG).show();
                scrollCurrentListViewToTop();
            }
        });

        if (AppNewMsgAlarm.DEBUG) {
			AppNewMsgAlarm.startAlarm(AppNewMsgAlarm.DEBUG, getApplicationContext(), true);
		}
        
    }

    public void closeLeftDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    public void closeRightDrawer() {
        mDrawerLayout.closeDrawer(Gravity.END);
    }

    private void buildInterface(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_time_line_activity);
        mToolbar = (Toolbar) findViewById(R.id.mainTimeLineToolBar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.writeWeiboDrawerL);
        mDrawerToggle = new MyDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerToggle.setToolbarNavigationClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("onOptionsItemSelected", " setToolbarNavigationClickListener");
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        boolean isPhoneDevice = findViewById(R.id.menu_frame) == null;
        Log.d("MainTimeLine-buildInterface", "isPhoneDevice: " + isPhoneDevice);

        buildCustomActionBarTitle(savedInstanceState);

        if (savedInstanceState == null) {
            initFragments();
            FragmentTransaction secondFragmentTransaction = getSupportFragmentManager().beginTransaction();
            secondFragmentTransaction.replace(R.id.menu_frame, getLeftMenuFragment(), LeftMenuFragment.class.getName());
            // getSlidingMenu().showContent();
            secondFragmentTransaction.commit();

            FragmentTransaction mainTransaction = getSupportFragmentManager().beginTransaction();
            mainTransaction.replace(R.id.menu_frame_right, getRightMenuFragment(), RightMenuFragment.class.getName());
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
            if (mDrawerLayout.isDrawerOpen(findViewById(R.id.menu_frame_right))) {
                mDrawerLayout.closeDrawer(Gravity.END);
            }
            getLeftMenuFragment().displayCover();
        }
    }

    private void initFragments() {
        Fragment friend = getFriendsTimeLineFragment();
        Fragment mentions = getAtMeTimeLineFragment();
        Fragment comments = getCommentsTimeLineFragment();

        Fragment fav = getFavFragment();
        Fragment myself = getMyProfileFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!friend.isAdded()) {
            fragmentTransaction.add(R.id.menu_right_fl, friend, FriendsTimeLineFragment.class.getName());
            fragmentTransaction.hide(friend);
        }
        if (!mentions.isAdded()) {
            fragmentTransaction.add(R.id.menu_right_fl, mentions, AtMeTimeLineFragment.class.getName());
            fragmentTransaction.hide(mentions);

        }
        if (!comments.isAdded()) {
            fragmentTransaction.add(R.id.menu_right_fl, comments, CommentsTimeLineFragment.class.getName());
            fragmentTransaction.hide(comments);

        }

        if (!fav.isAdded()) {
            fragmentTransaction.add(R.id.menu_right_fl, fav, MyFavListFragment.class.getName());
            fragmentTransaction.hide(fav);
        }

        if (!myself.isAdded()) {
            fragmentTransaction.add(R.id.menu_right_fl, myself, UserInfoFragment.class.getName());
            fragmentTransaction.hide(myself);
        }

        if (GlobalContext.getInstance().getAccountBean().isBlack_magic()) {
            Fragment search = getSearchFragment();
            Fragment dm = getDMFragment();

            if (!search.isAdded()) {
                fragmentTransaction.add(R.id.menu_right_fl, search, SearchMainParentFragment.class.getName());
                fragmentTransaction.hide(search);

            }

            if (!dm.isAdded()) {
                fragmentTransaction.add(R.id.menu_right_fl, dm, DMUserListFragment.class.getName());
                fragmentTransaction.hide(dm);

            }
        }

        if (!fragmentTransaction.isEmpty()) {
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    private void buildCustomActionBarTitle(Bundle savedInstanceState) {
        View title = getLayoutInflater().inflate(R.layout.maintimelineactivity_title_layout, null);
        titleText = (TextView) title.findViewById(R.id.tv_title);
        clickToTop = title.findViewById(R.id.tv_click_to_top);
        clickToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollCurrentListViewToTop();
            }
        });
        View write = title.findViewById(R.id.btn_write);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SettingUtils.isDebug()) {
                    Intent intent = WriteWeiboActivity.newIntent(GlobalContext.getInstance().getAccountBean());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainTimeLineActivity.this, WriteWeiboWithAppSrcActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void scrollCurrentListViewToTop() {
        if (this.currentFragment != null) {
            this.currentFragment.scrollToTop();
        }
    }

    public View getClickToTopView() {
        return mScrollTopBtn;
    }

    public void setCurrentFragment(ScrollableListFragment fragment) {
        this.currentFragment = fragment;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (SettingUtils.isClickToTopTipFirstShow()) {
            ViewTarget target = new ViewTarget(getClickToTopView());
            ShowcaseView.insertShowcaseView(target, this, R.string.tip, R.string.click_to_top_tip);
        }
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
            GlobalContext.getInstance().setAccountBean(mAccountBean);
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
        GlobalContext.getInstance().getBitmapCache().evictAll();
        finish();
    }

    public UserBean getUser() {
        return mAccountBean.getInfo();

    }

    private void readClipboard() {
        ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cmContent = cm.getPrimaryClip();
        if (cmContent == null) {
            return;
        }
        ClipData.Item item = cmContent.getItemAt(0);
        if (item != null) {
            String url = item.coerceToText(this).toString();
            boolean a = !TextUtils.isEmpty(url) && !url.equals(SettingUtils.getLastFoundWeiboAccountLink());
            boolean b = Utility.isWeiboAccountIdLink(url) || Utility.isWeiboAccountDomainLink(url);
            if (a && b) {
                OpenWeiboAccountLinkDialog dialog = new OpenWeiboAccountLinkDialog(url);
                dialog.show(getSupportFragmentManager(), "");
                SettingUtils.setLastFoundWeiboAccountLink(url);
            }
        }
    }

    public static class OpenWeiboAccountLinkDialog extends DialogFragment {

        private String url;

        public OpenWeiboAccountLinkDialog() {

        }

        public OpenWeiboAccountLinkDialog(String url) {
            this.url = url;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("url", url);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                this.url = savedInstanceState.getString("url");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.find_weibo_account_link).setMessage(url)
                    .setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Utility.isWeiboAccountIdLink(url)) {
                                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                                intent.putExtra("id", Utility.getIdFromWeiboAccountLink(url));
                                startActivity(intent);
                            } else if (Utility.isWeiboAccountDomainLink(url)) {
                                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                                intent.putExtra("domain", Utility.getDomainFromWeiboAccountLink(url));
                                startActivity(intent);
                            }
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);

        IntentFilter filter = new IntentFilter(AppEventAction.NEW_MSG_PRIORITY_BROADCAST);
        filter.setPriority(1);
        newMsgInterruptBroadcastReceiver = new NewMsgInterruptBroadcastReceiver();
        Utility.registerReceiverIgnoredReceiverHasRegisteredHereException(this, newMsgInterruptBroadcastReceiver, filter);
        musicReceiver = new MusicReceiver();
        Utility.registerReceiverIgnoredReceiverHasRegisteredHereException(this, musicReceiver,
                AppEventAction.getSystemMusicBroadcastFilterAction());
        readClipboard();
        // ensure timeline picture type is correct
        ConnectionChangeReceiver.judgeNetworkStatus(this, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);

        Utility.unregisterReceiverIgnoredReceiverNotRegisteredException(this, newMsgInterruptBroadcastReceiver);
        Utility.unregisterReceiverIgnoredReceiverNotRegisteredException(this, musicReceiver);

        if (isFinishing()) {
            saveNavigationPositionToDB();
        }
    }

    public void saveNavigationPositionToDB() {
        int navPosition = getLeftMenuFragment().getCurrentIndex() * 10;
        // ActionBar actionBar = getActionBar();
        int second = 0;
        // if (actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_STANDARD) {
        // second = actionBar.getSelectedNavigationIndex();
        // }
        int result = navPosition + second;
        GlobalContext.getInstance().getAccountBean().setNavigationPosition(result);
        AccountDBTask.updateNavigationPosition(GlobalContext.getInstance().getAccountBean(), result);
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

    public FriendsTimeLineFragment getFriendsTimeLineFragment() {
        FriendsTimeLineFragment fragment = ((FriendsTimeLineFragment) getSupportFragmentManager().findFragmentByTag(
                FriendsTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = FriendsTimeLineFragment.newInstance(getAccount(), getUser(), getToken());
        }
        return fragment;
    }

    public AtMeTimeLineFragment getAtMeTimeLineFragment() {
        AtMeTimeLineFragment fragment = ((AtMeTimeLineFragment) getSupportFragmentManager().findFragmentByTag(
                AtMeTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = AtMeTimeLineFragment.newInstance();
        }
        return fragment;
    }

    public CommentsTimeLineFragment getCommentsTimeLineFragment() {
        CommentsTimeLineFragment fragment = ((CommentsTimeLineFragment) getSupportFragmentManager().findFragmentByTag(
                CommentsTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = CommentsTimeLineFragment.newInstance();
        }
        return fragment;
    }

    public SearchMainParentFragment getSearchFragment() {
        SearchMainParentFragment fragment = ((SearchMainParentFragment) getSupportFragmentManager().findFragmentByTag(
                SearchMainParentFragment.class.getName()));
        if (fragment == null) {
            fragment = SearchMainParentFragment.newInstance();
        }
        return fragment;
    }

    public DMUserListFragment getDMFragment() {
        DMUserListFragment fragment = ((DMUserListFragment) getSupportFragmentManager().findFragmentByTag(
                DMUserListFragment.class.getName()));
        if (fragment == null) {
            fragment = DMUserListFragment.newInstance();
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

    public UserInfoFragment getMyProfileFragment() {
        UserInfoFragment fragment = ((UserInfoFragment) getSupportFragmentManager().findFragmentByTag(
                UserInfoFragment.class.getName()));
        if (fragment == null) {
            fragment = UserInfoFragment.newInstance(mToolbar, GlobalContext.getInstance().getAccountBean().getInfo(), GlobalContext
                    .getInstance().getSpecialToken());
        }
        return fragment;
    }

    // todo
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
                String tip = String.format(context.getString(R.string.you_have_new_unread_count),
                        String.valueOf(unreadCount));
                Toast.makeText(MainTimeLineActivity.this, tip, Toast.LENGTH_LONG).show();
                abortBroadcast();
            }
        }
    }

    public void setMentionsWeiboCount(int count) {
        LeftMenuFragment fragment = getLeftMenuFragment();
        fragment.setMentionWeiboUnreadCount(count);
    }

    public void setMentionsCommentCount(int count) {
        LeftMenuFragment fragment = getLeftMenuFragment();
        fragment.setMentionCommentUnreadCount(count);
    }

    public void setCommentsToMeCount(int count) {

        LeftMenuFragment fragment = getLeftMenuFragment();
        fragment.setCommentUnreadCount(count);
    }

    public static interface ScrollableListFragment {

        public void scrollToTop();
    }

    public static Intent newIntent() {
        return new Intent(GlobalContext.getInstance(), MainTimeLineActivity.class);
    }

    public static Intent newIntent(AccountBean accountBean) {
        Intent intent = newIntent();
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, accountBean);
        return intent;
    }

    /*
     * notification bar
     */
    public static Intent newIntent(AccountBean accountBean, MessageListBean mentionsWeiboData,
            CommentListBean mentionsCommentData,
            CommentListBean commentsToMeData, UnreadBean unreadBean) {
        Intent intent = newIntent();
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, accountBean);
        intent.putExtra(BundleArgsConstants.MENTIONS_WEIBO_EXTRA, mentionsWeiboData);
        intent.putExtra(BundleArgsConstants.MENTIONS_COMMENT_EXTRA, mentionsCommentData);
        intent.putExtra(BundleArgsConstants.COMMENTS_TO_ME_EXTRA, commentsToMeData);
        intent.putExtra(BundleArgsConstants.UNREAD_EXTRA, unreadBean);
        return intent;
    }

    public String getToken() {
        return mAccountBean.getAccess_token();
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            titleText.setVisibility(View.GONE);
        } else {
            titleText.setText(title);
            titleText.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(int res) {
        setTitle(getString(res));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.ACCOUNT, mAccountBean);
    }
}
