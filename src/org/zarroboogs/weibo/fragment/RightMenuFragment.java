
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.AccountActivity;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.adapter.FriendsTimeLineListNavAdapter;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.GroupBean;
import org.zarroboogs.weibo.bean.TimeLinePosition;
import org.zarroboogs.weibo.db.task.AccountDBTask;
import org.zarroboogs.weibo.db.task.CommentToMeTimeLineDBTask;
import org.zarroboogs.weibo.db.task.MentionCommentsTimeLineDBTask;
import org.zarroboogs.weibo.db.task.MentionWeiboTimeLineDBTask;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.setting.activity.SettingActivity;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.support.utils.AnimationUtility;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshListView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class RightMenuFragment extends BaseStateFragment {

    private int currentIndex = -1;

    private int mentionsWeiboUnreadCount = 0;

    private int mentionsCommentUnreadCount = 0;

    private int commentsToMeUnreadCount = 0;

    public int commentsTabIndex = -1;

    public int mentionsTabIndex = -1;

    public int searchTabIndex = -1;

    private boolean firstStart = true;

    private SparseArray<Fragment> rightFragments = new SparseArray<Fragment>();

    public static final int HOME_INDEX = 0;

    public static final int MENTIONS_INDEX = 1;

    public static final int COMMENTS_INDEX = 2;

    public static final int DM_INDEX = 3;

    public static final int FAV_INDEX = 4;

    public static final int SEARCH_INDEX = 5;

    public static final int PROFILE_INDEX = 6;

    public static final int LOGOUT_INDEX = 7;

    public static final int SETTING_INDEX = 8;

    public static final String SWITCH_GROUP_KEY = "switch_group";
    private PullToRefreshListView mPullToRefreshListView;
    private FriendsTimeLineListNavAdapter mBaseAdapter;

    public static RightMenuFragment newInstance() {
        RightMenuFragment fragment = new RightMenuFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentIndex", currentIndex);
        outState.putInt("mentionsWeiboUnreadCount", mentionsWeiboUnreadCount);
        outState.putInt("mentionsCommentUnreadCount", mentionsCommentUnreadCount);
        outState.putInt("commentsToMeUnreadCount", commentsToMeUnreadCount);
        outState.putInt("commentsTabIndex", commentsTabIndex);
        outState.putInt("mentionsTabIndex", mentionsTabIndex);
        outState.putInt("searchTabIndex", searchTabIndex);
        outState.putBoolean("firstStart", firstStart);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt("currentIndex");
            mentionsWeiboUnreadCount = savedInstanceState.getInt("mentionsWeiboUnreadCount");
            mentionsCommentUnreadCount = savedInstanceState.getInt("mentionsCommentUnreadCount");
            commentsToMeUnreadCount = savedInstanceState.getInt("commentsToMeUnreadCount");
            commentsTabIndex = savedInstanceState.getInt("commentsTabIndex");
            mentionsTabIndex = savedInstanceState.getInt("mentionsTabIndex");
            searchTabIndex = savedInstanceState.getInt("searchTabIndex");
            firstStart = savedInstanceState.getBoolean("firstStart");
        } else {
            readUnreadCountFromDB();
        }
        if (currentIndex == -1) {
            currentIndex = GlobalContext.getInstance().getAccountBean().getNavigationPosition() / 10;
        }

        rightFragments.append(HOME_INDEX, ((MainTimeLineActivity) getActivity()).getFriendsTimeLineFragment());
        rightFragments.append(MENTIONS_INDEX, ((MainTimeLineActivity) getActivity()).getAtMeTimeLineFragment());
        rightFragments.append(COMMENTS_INDEX, ((MainTimeLineActivity) getActivity()).getCommentsTimeLineFragment());
        rightFragments.append(SEARCH_INDEX, ((MainTimeLineActivity) getActivity()).getSearchFragment());
        rightFragments.append(DM_INDEX, ((MainTimeLineActivity) getActivity()).getDMFragment());
        rightFragments.append(FAV_INDEX, ((MainTimeLineActivity) getActivity()).getFavFragment());
        rightFragments.append(PROFILE_INDEX, ((MainTimeLineActivity) getActivity()).getMyProfileFragment());

        switchCategory(currentIndex);

    }

    public void switchCategory(int position) {

        switch (position) {
            case HOME_INDEX:
                showHomePage(true);
                break;
            case MENTIONS_INDEX:
                showMentionPage(true);
                break;
            case COMMENTS_INDEX:
                showCommentPage(true);
                break;
            case SEARCH_INDEX:
                showSearchPage(true);
                break;
            case DM_INDEX:
                showDMPage(true);
                break;
            case FAV_INDEX:
                showFavPage(true);
                break;
            case PROFILE_INDEX:
                showProfilePage(true);
                break;
        }
        firstStart = false;
    }

    private void readUnreadCountFromDB() {
        TimeLinePosition position = MentionWeiboTimeLineDBTask
                .getPosition(GlobalContext.getInstance().getCurrentAccountId());
        TreeSet<Long> hashSet = position.newMsgIds;
        if (hashSet != null) {
            mentionsWeiboUnreadCount = hashSet.size();
        }

        position = MentionCommentsTimeLineDBTask.getPosition(GlobalContext.getInstance().getCurrentAccountId());
        hashSet = position.newMsgIds;
        if (hashSet != null) {
            mentionsCommentUnreadCount = hashSet.size();
        }
        position = CommentToMeTimeLineDBTask.getPosition(GlobalContext.getInstance().getCurrentAccountId());
        hashSet = position.newMsgIds;
        if (hashSet != null) {
            commentsToMeUnreadCount = hashSet.size();
        }
    }

    private void showAccountSwitchPage() {
        Intent intent = AccountActivity.newIntent();
        startActivity(intent);
        getActivity().finish();
    }

    private void showSettingPage() {
        startActivity(new Intent(getActivity(), SettingActivity.class));
    }

    private boolean showHomePage(boolean reset) {
        if (currentIndex == HOME_INDEX && !reset) {
            // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();
            return true;
        }

        // getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        currentIndex = HOME_INDEX;

        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == HOME_INDEX) {
                        showHomePageImp();
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showHomePageImp();

        }

        // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();

        return false;
    }

    private void showHomePageImp() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.hide(rightFragments.get(MENTIONS_INDEX));
        ft.hide(rightFragments.get(COMMENTS_INDEX));
        ft.hide(rightFragments.get(SEARCH_INDEX));
        ft.hide(rightFragments.get(DM_INDEX));
        ft.hide(rightFragments.get(FAV_INDEX));
        ft.hide(rightFragments.get(PROFILE_INDEX));

        FriendsTimeLineFragment fragment = (FriendsTimeLineFragment) rightFragments.get(HOME_INDEX);
        ft.show(fragment);
        ft.commit();
        setTitle("");
        fragment.buildActionBarNav();
    }

    private boolean showMentionPage(boolean reset) {
        if (currentIndex == MENTIONS_INDEX && !reset) {
            // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();
            return true;
        }

        // getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        currentIndex = MENTIONS_INDEX;

        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == MENTIONS_INDEX) {
                        showMentionPageImp();
                    }
                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showMentionPageImp();
        }
        // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();

        return false;
    }

    private void showMentionPageImp() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(rightFragments.get(HOME_INDEX));
        ft.hide(rightFragments.get(COMMENTS_INDEX));
        ft.hide(rightFragments.get(SEARCH_INDEX));
        ft.hide(rightFragments.get(DM_INDEX));
        ft.hide(rightFragments.get(FAV_INDEX));
        ft.hide(rightFragments.get(PROFILE_INDEX));

        Fragment m = rightFragments.get(MENTIONS_INDEX);

        if (firstStart) {
            int navPosition = GlobalContext.getInstance().getAccountBean().getNavigationPosition() / 10;
            if (navPosition == MENTIONS_INDEX) {
                mentionsTabIndex = GlobalContext.getInstance().getAccountBean().getNavigationPosition() % 10;
            }
        }
        m.getArguments().putInt("mentionsTabIndex", mentionsTabIndex);

        ft.show(m);
        ft.commit();

        ((AtMeTimeLineFragment) m).buildActionBarAndViewPagerTitles(mentionsTabIndex);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    private boolean showCommentPage(boolean reset) {
        // getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        if (currentIndex == COMMENTS_INDEX && !reset) {
            // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();
            return true;
        }
        // getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        currentIndex = COMMENTS_INDEX;
        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == COMMENTS_INDEX) {
                        showCommentPageImp();
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showCommentPageImp();

        }

        // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();

        return false;
    }

    private void showCommentPageImp() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.hide(rightFragments.get(HOME_INDEX));
        ft.hide(rightFragments.get(MENTIONS_INDEX));
        ft.hide(rightFragments.get(SEARCH_INDEX));
        ft.hide(rightFragments.get(DM_INDEX));
        ft.hide(rightFragments.get(FAV_INDEX));
        ft.hide(rightFragments.get(PROFILE_INDEX));

        Fragment fragment = rightFragments.get(COMMENTS_INDEX);
        if (firstStart) {
            int navPosition = GlobalContext.getInstance().getAccountBean().getNavigationPosition() / 10;
            if (navPosition == COMMENTS_INDEX) {
                commentsTabIndex = GlobalContext.getInstance().getAccountBean().getNavigationPosition() % 10;
            }
        }
        fragment.getArguments().putInt("commentsTabIndex", commentsTabIndex);

        ft.show(fragment);
        ft.commit();

        ((CommentsTimeLineFragment) fragment).buildActionBarAndViewPagerTitles(commentsTabIndex);
    }

    private boolean showSearchPage(boolean reset) {
        // getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        if (currentIndex == SEARCH_INDEX && !reset) {
            // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();
            return true;
        }
        // getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        currentIndex = SEARCH_INDEX;
        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == SEARCH_INDEX) {
                        showSearchPageImp();
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showSearchPageImp();

        }

        // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();

        return false;
    }

    private void showSearchPageImp() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.hide(rightFragments.get(HOME_INDEX));
        ft.hide(rightFragments.get(MENTIONS_INDEX));
        ft.hide(rightFragments.get(COMMENTS_INDEX));
        ft.hide(rightFragments.get(DM_INDEX));
        ft.hide(rightFragments.get(FAV_INDEX));
        ft.hide(rightFragments.get(PROFILE_INDEX));

        Fragment fragment = rightFragments.get(SEARCH_INDEX);

        if (firstStart) {
            int navPosition = GlobalContext.getInstance().getAccountBean().getNavigationPosition() / 10;
            if (navPosition == SEARCH_INDEX) {
                searchTabIndex = GlobalContext.getInstance().getAccountBean().getNavigationPosition() % 10;
            }
        }
        fragment.getArguments().putInt("searchTabIndex", searchTabIndex);

        ft.show(fragment);
        ft.commit();

    }

    private boolean showDMPage(boolean reset) {
        // getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        if (currentIndex == DM_INDEX && !reset) {
            // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();
            return true;
        }
        // getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        currentIndex = DM_INDEX;
        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == DM_INDEX) {
                        showDMPageImp();
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showDMPageImp();

        }

        // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();

        return false;
    }

    private void showDMPageImp() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.hide(rightFragments.get(HOME_INDEX));
        ft.hide(rightFragments.get(MENTIONS_INDEX));
        ft.hide(rightFragments.get(COMMENTS_INDEX));
        ft.hide(rightFragments.get(SEARCH_INDEX));
        ft.hide(rightFragments.get(FAV_INDEX));
        ft.hide(rightFragments.get(PROFILE_INDEX));

        Fragment fragment = rightFragments.get(DM_INDEX);

        ft.show(fragment);
        ft.commit();

        ((DMUserListFragment) fragment).buildActionBarAndViewPagerTitles();
    }

    private boolean showFavPage(boolean reset) {
        // getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        if (currentIndex == FAV_INDEX && !reset) {
            // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();
            return true;
        }
        // getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        currentIndex = FAV_INDEX;
        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == FAV_INDEX) {
                        showFavPageImp();
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showFavPageImp();

        }

        // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();

        return false;
    }

    private void showFavPageImp() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.hide(rightFragments.get(HOME_INDEX));
        ft.hide(rightFragments.get(MENTIONS_INDEX));
        ft.hide(rightFragments.get(COMMENTS_INDEX));
        ft.hide(rightFragments.get(SEARCH_INDEX));
        ft.hide(rightFragments.get(DM_INDEX));
        ft.hide(rightFragments.get(PROFILE_INDEX));

        Fragment fragment = rightFragments.get(FAV_INDEX);

        ft.show(fragment);
        ft.commit();
        ((MyFavListFragment) fragment).buildActionBarAndViewPagerTitles();
    }

    private boolean showProfilePage(boolean reset) {
        // getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        if (currentIndex == PROFILE_INDEX && !reset) {
            // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();
            return true;
        }
        // getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        currentIndex = PROFILE_INDEX;
        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == PROFILE_INDEX) {
                        showProfilePageImp();
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showProfilePageImp();

        }

        // ((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();

        return false;
    }

    private void showProfilePageImp() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.hide(rightFragments.get(HOME_INDEX));
        ft.hide(rightFragments.get(MENTIONS_INDEX));
        ft.hide(rightFragments.get(COMMENTS_INDEX));
        ft.hide(rightFragments.get(SEARCH_INDEX));
        ft.hide(rightFragments.get(DM_INDEX));
        ft.hide(rightFragments.get(FAV_INDEX));

        UserInfoFragment fragment = (UserInfoFragment) rightFragments.get(PROFILE_INDEX);

        ft.show(fragment);
        ft.commit();
        ((UserInfoFragment) fragment).buildActionBarAndViewPagerTitles();

        AnimationUtility.translateFragmentY(fragment, -400, 0, fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        List<GroupBean> list = new ArrayList<GroupBean>();
        if (GlobalContext.getInstance().getGroup() != null) {
            list = GlobalContext.getInstance().getGroup().getLists();
        } else {
            list = new ArrayList<GroupBean>();
        }

        mBaseAdapter = new FriendsTimeLineListNavAdapter(getActivity(), buildListNavData(list));
    }

    private String[] buildListNavData(List<GroupBean> list) {
        List<String> name = new ArrayList<String>();

        name.add(getString(R.string.all_people));
        name.add(getString(R.string.bilateral));

        for (GroupBean b : list) {
            name.add(b.getName());
        }

        String[] valueArray = name.toArray(new String[name.size()]);
        return valueArray;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.right_slidingdrawer_contents, container,
                false);

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.rightGroupListView);
        mPullToRefreshListView.setAdapter(mBaseAdapter);
        mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainTimeLineActivity) getActivity()).closeRightDrawer();

                mBaseAdapter.setSelectId(position - 1);
                Intent mIntent = new Intent(AppEventAction.SWITCH_WEIBO_GROUP_BROADCAST);
                mIntent.putExtra(SWITCH_GROUP_KEY, position - 1);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(mIntent);
            }

        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void setTitle(int res) {
        ((MainTimeLineActivity) getActivity()).setTitle(res);
    }

    private void setTitle(String title) {
        ((MainTimeLineActivity) getActivity()).setTitle(title);
    }

    private class AvatarAdapter extends BaseAdapter {

        ArrayList<AccountBean> data = new ArrayList<AccountBean>();

        int count = 0;

        public AvatarAdapter(Spinner spinner) {
            data.addAll(AccountDBTask.getAccountList());
            if (data.size() == 1) {
                count = 1;
            } else {
                count = data.size() - 1;
            }
            Iterator<AccountBean> iterator = data.iterator();
            while (iterator.hasNext()) {
                AccountBean accountBean = iterator.next();
                if (accountBean.getUid().equals(GlobalContext.getInstance().getAccountBean().getUid())) {
                    iterator.remove();
                    break;
                }
            }

        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater(null).inflate(R.layout.slidingdrawer_avatar, parent, false);
            ImageView iv = (ImageView) view.findViewById(R.id.avatar);
            TimeLineBitmapDownloader.getInstance().display(iv, -1, -1,
                    GlobalContext.getInstance().getAccountBean().getInfo().getAvatar_large(),
                    FileLocationMethod.avatar_large);

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater(null).inflate(R.layout.slidingdrawer_avatar_dropdown, parent, false);
            TextView nickname = (TextView) view.findViewById(R.id.nickname);
            ImageView avatar = (ImageView) view.findViewById(R.id.avatar);

            if (data.size() > 0) {
                final AccountBean accountBean = data.get(position);
                TimeLineBitmapDownloader.getInstance().display(avatar, -1, -1, accountBean.getInfo().getAvatar_large(),
                        FileLocationMethod.avatar_large);

                nickname.setText(accountBean.getUsernick());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent start = MainTimeLineActivity.newIntent(accountBean);
                        getActivity().startActivity(start);
                        getActivity().finish();

                    }
                });
            } else {
                avatar.setVisibility(View.GONE);
                nickname.setTextColor(getResources().getColor(R.color.gray));
                nickname.setText(getString(R.string.dont_have_other_account));
            }
            return view;
        }
    }
}
