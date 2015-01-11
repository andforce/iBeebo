
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.AccountActivity;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.activity.MyInfoActivity;
import org.zarroboogs.weibo.activity.NearbyTimeLineActivity;
import org.zarroboogs.weibo.bean.TimeLinePosition;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.db.task.CommentToMeTimeLineDBTask;
import org.zarroboogs.weibo.db.task.MentionCommentsTimeLineDBTask;
import org.zarroboogs.weibo.db.task.MentionWeiboTimeLineDBTask;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.setting.activity.SettingActivity;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.support.utils.AnimationUtility;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.ViewUtility;
import org.zarroboogs.weibo.widget.BlurImageView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.TreeSet;

public class LeftMenuFragment extends BaseStateFragment {

    private LeftDrawerViewHolder layout;

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

    private Toolbar mToolbar;
    private BlurImageView mCoverBlureImage;

    public static LeftMenuFragment newInstance() {
        LeftMenuFragment fragment = new LeftMenuFragment();
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

        layout.nickname.setText(GlobalContext.getInstance().getCurrentAccountName());
        
        TimeLineBitmapDownloader.getInstance().display(layout.avatar, -1, -1,
                GlobalContext.getInstance().getAccountBean().getInfo().getAvatar_large(),
                FileLocationMethod.avatar_large);
        layout.avatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	            Intent intent = new Intent(getActivity(), MyInfoActivity.class);
	            intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getAccountBean().getAccess_token());

	            UserBean userBean = new UserBean();
	            userBean.setId(GlobalContext.getInstance().getCurrentAccountId());
	            intent.putExtra("user", userBean);
	            intent.putExtra(Constants.ACCOUNT, GlobalContext.getInstance().getAccountBean());
	            startActivity(intent);
			}
		});
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
        drawButtonsBackground(position);

        buildUnreadCount();

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

    private void buildUnreadCount() {
        setMentionWeiboUnreadCount(mentionsWeiboUnreadCount);
        setMentionCommentUnreadCount(mentionsCommentUnreadCount);
        setCommentUnreadCount(commentsToMeUnreadCount);
    }

    private void showAccountSwitchPage() {
        Intent intent = AccountActivity.newIntent();
        startActivity(intent);
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
                        mToolbar.setTitle(R.string.weibo_home_page);
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
        // setTitle("");
        ViewUtility.findViewById(getActivity(), R.id.scrollToTopBtn).setVisibility(View.VISIBLE);
        mToolbar.getMenu().clear();

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
                        mToolbar.setTitle(R.string.mentions);
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

        ViewUtility.findViewById(getActivity(), R.id.scrollToTopBtn).setVisibility(View.VISIBLE);
        mToolbar.getMenu().clear();

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
            Log.d("Left_menu_fr", "onReceiver_change_if");
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("Left_menu_fr", "onReceiver_");
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == COMMENTS_INDEX) {
                        mToolbar.setTitle(R.string.comments);
                        showCommentPageImp();
                        Log.d("Left_menu_fr", "onReceiver_change");
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            Log.d("Left_menu_fr", "onReceiver_change_else");
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

        ViewUtility.findViewById(getActivity(), R.id.scrollToTopBtn).setVisibility(View.VISIBLE);
        mToolbar.getMenu().clear();

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
                        mToolbar.setTitle(R.string.search);
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

        ((SearchMainParentFragment) fragment).showSearchMenu();

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
                        mToolbar.setTitle(R.string.dm);
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

        ((DMUserListFragment) fragment).showWriteDmMenu();
        ViewUtility.findViewById(getActivity(), R.id.scrollToTopBtn).setVisibility(View.VISIBLE);

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
                        mToolbar.setTitle(R.string.favourite);
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
        ViewUtility.findViewById(getActivity(), R.id.scrollToTopBtn).setVisibility(View.VISIBLE);
        mToolbar.getMenu().clear();

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

        ViewUtility.findViewById(getActivity(), R.id.scrollToTopBtn).setVisibility(View.VISIBLE);
        mToolbar.getMenu().clear();

        AnimationUtility.translateFragmentY(fragment, -400, 0, fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ScrollView view = (ScrollView) inflater.inflate(R.layout.main_timeline_left_drawer_layout, container, false);

        mCoverBlureImage = ViewUtility.findViewById(view, R.id.coverBlureImage);
        displayCover();
        
        layout = new LeftDrawerViewHolder();

        layout.avatar = (ImageView) view.findViewById(R.id.avatar);
        layout.nickname = (TextView) view.findViewById(R.id.nickname);

        layout.home = (LinearLayout) view.findViewById(R.id.btn_home);
        layout.mention = (LinearLayout) view.findViewById(R.id.btn_mention);
        layout.comment = (LinearLayout) view.findViewById(R.id.btn_comment);
        layout.search = (Button) view.findViewById(R.id.btn_search);
        // layout.location = (Button) view.findViewById(R.id.btn_location);
        layout.dm = (Button) view.findViewById(R.id.btn_dm);
        layout.fav = (Button) view.findViewById(R.id.btn_favourite);
        layout.homeCount = (TextView) view.findViewById(R.id.tv_home_count);
        layout.mentionCount = (TextView) view.findViewById(R.id.tv_mention_count);
        layout.commentCount = (TextView) view.findViewById(R.id.tv_comment_count);
        
        layout.leftDrawerSettingBtn = (ImageButton) view.findViewById(R.id.leftDrawerSettingBtn);
        
        layout.homeButton = (Button) view.findViewById(R.id.homeButton);
        layout.mentionButton = (Button) view.findViewById(R.id.mentionButton);
        layout.commentButton = (Button) view.findViewById(R.id.commentButton);

        boolean blackMagic = GlobalContext.getInstance().getAccountBean().isBlack_magic();
        if (!blackMagic) {
            layout.dm.setVisibility(View.GONE);
            layout.search.setVisibility(View.GONE);
        }
        return view;
    }

	public void displayCover() {
//		final String picPath = "file:///android_asset/coverImage.jpg";//GlobalContext.getInstance().getAccountBean().getInfo().getCover_image();
//        LogTool.D("mCoverBlureImage: path: " + picPath);
//        Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//        	    + getActivity().getResources().getResourcePackageName(R.drawable.cover_image) + "/"
//        	    + getActivity().getResources().getResourceTypeName(R.drawable.cover_image) + "/"
//        	    + getActivity().getResources().getResourceEntryName(R.drawable.cover_image));
        mCoverBlureImage.setImageResource(R.drawable.cover_image);//.setOriImageUrl(picPath);
        mCoverBlureImage.setAlpha(0.5f);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout.home.setOnClickListener(onClickListener);
        layout.mention.setOnClickListener(onClickListener);
        layout.comment.setOnClickListener(onClickListener);
        layout.search.setOnClickListener(onClickListener);
        // layout.location.setOnClickListener(onClickListener);
        layout.dm.setOnClickListener(onClickListener);
        layout.fav.setOnClickListener(onClickListener);

        layout.leftDrawerSettingBtn.setOnClickListener(onClickListener);
        
        mToolbar = (Toolbar) getActivity().findViewById(R.id.mainTimeLineToolBar);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_home:
                    showHomePage(false);
                    drawButtonsBackground(HOME_INDEX);
                    break;
                case R.id.btn_mention:
                    showMentionPage(false);
                    drawButtonsBackground(MENTIONS_INDEX);
                    break;
                case R.id.btn_comment:
                    showCommentPage(false);
                    drawButtonsBackground(COMMENTS_INDEX);
                    break;
                case R.id.btn_search:
                    showSearchPage(false);
                    drawButtonsBackground(SEARCH_INDEX);
                    break;
                case R.id.btn_location:
                    startActivity(new Intent(getActivity(), NearbyTimeLineActivity.class));
                    // drawButtonsBackground(5);
                    break;
                case R.id.btn_favourite:
                    showFavPage(false);
                    drawButtonsBackground(FAV_INDEX);
                    break;
                case R.id.btn_dm:
                    showDMPage(false);
                    drawButtonsBackground(DM_INDEX);
                    break;
                case R.id.leftDrawerSettingBtn:{
                	showSettingPage();
                	break;
                }
            }
            ((MainTimeLineActivity) getActivity()).closeLeftDrawer();
        }
    };

    private void drawButtonsBackground(int position) {
    	layout.homeButton.setTextColor(getResources().getColor(R.color.draw_text_color));
    	layout.mentionButton.setTextColor(getResources().getColor(R.color.draw_text_color));
    	
    	layout.dm.setTextColor(getResources().getColor(R.color.draw_text_color));
		layout.fav.setTextColor(getResources().getColor(R.color.draw_text_color));
		layout.search.setTextColor(getResources().getColor(R.color.draw_text_color));
		
//		layout.home.setBackgroundResource(R.drawable.btn_drawer_menu);
//		layout.mention.setBackgroundResource(R.drawable.btn_drawer_menu);
//		layout.comment.setBackgroundResource(R.drawable.btn_drawer_menu);
//		layout.search.setBackgroundResource(R.drawable.btn_drawer_menu);
//		layout.location.setBackgroundResource(R.color.transparent);
//		layout.setting.setBackgroundResource(R.color.transparent);
//		layout.dm.setBackgroundResource(R.drawable.btn_drawer_menu);
//		layout.fav.setBackgroundResource(R.drawable.btn_drawer_menu);
//		layout.logout.setBackgroundResource(R.color.transparent);
        switch (position) {
            case HOME_INDEX:
//                layout.home.setBackgroundResource(R.color.ics_blue_semi);
            	layout.homeButton.setTextColor(getResources().getColor(R.color.md_actionbar_bg_color));
                break;
            case MENTIONS_INDEX:
//                layout.mention.setBackgroundResource(R.color.ics_blue_semi);
            	layout.mentionButton.setTextColor(getResources().getColor(R.color.md_actionbar_bg_color));
                break;
            case COMMENTS_INDEX:
//                layout.comment.setBackgroundResource(R.color.ics_blue_semi);
            	layout.commentButton.setTextColor(getResources().getColor(R.color.md_actionbar_bg_color));
                break;
            case SEARCH_INDEX:
//                layout.search.setBackgroundResource(R.color.ics_blue_semi);
            	layout.search.setTextColor(getResources().getColor(R.color.md_actionbar_bg_color));
                break;
            case DM_INDEX:
//                layout.dm.setBackgroundResource(R.color.ics_blue_semi);
            	layout.dm.setTextColor(getResources().getColor(R.color.md_actionbar_bg_color));
                break;
            case FAV_INDEX:
//                layout.fav.setBackgroundResource(R.color.ics_blue_semi);
            	layout.fav.setTextColor(getResources().getColor(R.color.md_actionbar_bg_color));
                break;
        // case 5:
        // layout.location.setBackgroundResource(R.color.ics_blue_semi);
        // break;
        }
    }

    // private SlidingMenu getSlidingMenu() {
    // return ((MainTimeLineActivity) getActivity()).getSlidingMenu();
    // }

    private void setTitle(int res) {
        ((MainTimeLineActivity) getActivity()).setTitle(res);
    }

    private void setTitle(String title) {
        ((MainTimeLineActivity) getActivity()).setTitle(title);
    }

    public void setHomeUnreadCount(int count) {
        if (count > 0) {
            layout.homeCount.setVisibility(View.VISIBLE);
            layout.homeCount.setText(String.valueOf(count));
        } else {
            layout.homeCount.setVisibility(View.GONE);
        }
    }

    public void setMentionWeiboUnreadCount(int count) {
        this.mentionsWeiboUnreadCount = count;
        int totalCount = this.mentionsWeiboUnreadCount + this.mentionsCommentUnreadCount;
        if (totalCount > 0) {
            layout.mentionCount.setVisibility(View.VISIBLE);
            layout.mentionCount.setText(String.valueOf(totalCount));
        } else {
            layout.mentionCount.setVisibility(View.GONE);
        }
    }

    public void setMentionCommentUnreadCount(int count) {
        this.mentionsCommentUnreadCount = count;
        int totalCount = this.mentionsWeiboUnreadCount + this.mentionsCommentUnreadCount;
        if (totalCount > 0) {
            layout.mentionCount.setVisibility(View.VISIBLE);
            layout.mentionCount.setText(String.valueOf(totalCount));
        } else {
            layout.mentionCount.setVisibility(View.GONE);
        }
    }

    public void setCommentUnreadCount(int count) {
        this.commentsToMeUnreadCount = count;
        if (this.commentsToMeUnreadCount > 0) {
            layout.commentCount.setVisibility(View.VISIBLE);
            layout.commentCount.setText(String.valueOf(this.commentsToMeUnreadCount));
        } else {
            layout.commentCount.setVisibility(View.GONE);
        }
    }


    private class LeftDrawerViewHolder {

        ImageView avatar;

        TextView nickname;

        LinearLayout home;
        
        Button homeButton;

        LinearLayout mention;

        Button mentionButton;
        
        LinearLayout comment;

        Button commentButton;
        
        TextView homeCount;

        TextView mentionCount;

        TextView commentCount;

        Button search;

        // Button location;
        Button dm;

        Button fav;
        
        ImageButton leftDrawerSettingBtn;
    }

}
