package org.zarroboogs.weibo.fragment;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.TreeSet;

public class LeftMenuFragment extends BaseStateFragment {

    private LeftDrawerViewHolder layout;

    private int currentIndex = -1;

    private boolean firstStart = true;

    private SparseArray<Fragment> rightFragments = new SparseArray<Fragment>();

    public static final int HOME_INDEX = 0;

    public static final int FAV_INDEX = 1;

    public static final int HOT_WEIBO = 2;

    public static final int HOT_HUA_TI = 3;

    private Toolbar mToolbar;
    private ImageView mCoverBlureImage;

    public static LeftMenuFragment newInstance() {
        LeftMenuFragment fragment = new LeftMenuFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentIndex", currentIndex);
        outState.putBoolean("firstStart", firstStart);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt("currentIndex");
            firstStart = savedInstanceState.getBoolean("firstStart");
        } else {
            readUnreadCountFromDB();
        }
        if (currentIndex == -1) {
            currentIndex = BeeboApplication.getInstance().getAccountBean().getNavigationPosition() / 10;
        }

        rightFragments.append(HOME_INDEX, ((MainTimeLineActivity) getActivity()).getMainTimeLineFragment());
        rightFragments.append(FAV_INDEX, ((MainTimeLineActivity) getActivity()).getFavFragment());

        rightFragments.append(HOT_WEIBO, ((MainTimeLineActivity) getActivity()).getHotWeiboViewPagerFragment());
        rightFragments.append(HOT_HUA_TI, ((MainTimeLineActivity) getActivity()).getHotHuaTiViewPagerFragment());

        switchCategory(currentIndex);

        layout.nickname.setText(BeeboApplication.getInstance().getCurrentAccountName());

        TimeLineBitmapDownloader.getInstance().display(layout.avatar, -1, -1, BeeboApplication.getInstance().getAccountBean().getInfo().getAvatar_large(),
                FileLocationMethod.avatar_large);
        layout.avatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccountBean().getAccess_token());

                UserBean userBean = new UserBean();
                userBean.setId(BeeboApplication.getInstance().getCurrentAccountId());
                intent.putExtra("user", userBean);
                intent.putExtra(Constants.ACCOUNT, BeeboApplication.getInstance().getAccountBean());
                startActivity(intent);
            }
        });
    }

    public void switchCategory(int position) {

        switch (position) {
            case HOME_INDEX:
                showHomePage(true);
                break;
            case FAV_INDEX:
                showFavPage(true);
                break;
            case HOT_WEIBO: {
                showHotWeibo(true);
                break;
            }
            case HOT_HUA_TI: {
                showHotHuaTi(true);
                break;
            }

        }
        drawButtonsBackground(position);

        firstStart = false;
    }

    private boolean showHotHuaTi(boolean reset) {
        if (currentIndex == HOT_HUA_TI && !reset) {
            return true;
        }
        currentIndex = HOT_HUA_TI;
        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == HOT_HUA_TI) {
                        mToolbar.setTitle(R.string.left_drawer_hothuati);
                        shotHotHuaTiImp();
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                    new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            shotHotHuaTiImp();

        }
        return false;
    }

    protected void shotHotHuaTiImp() {
        FragmentTransaction ft = hideFragmentExp(HOT_HUA_TI);

        HotHuaTiViewPagerFragment fragment = (HotHuaTiViewPagerFragment) rightFragments.get(HOT_HUA_TI);

        ((MainTimeLineActivity) getActivity()).setCurrentFragment(fragment);
        fragment.onViewPageSelected(0);
        ft.show(fragment);
        ft.commit();
        ((MainTimeLineActivity) getActivity()).closeRight();
        // fragment.showMenuOnToolBar(R.menu.main_time_line_menu);
        //
        // fragment.buildActionBarNav();
    }

    private boolean showHotWeibo(boolean reset) {
        if (currentIndex == HOT_WEIBO && !reset) {
            return true;
        }
        currentIndex = HOT_WEIBO;
        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == HOT_WEIBO) {
                        mToolbar.setTitle(R.string.left_drawer_hotweibo);
                        showHotWeiboImp();
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showHotWeiboImp();

        }
        return false;
    }

    protected void showHotWeiboImp() {
        FragmentTransaction ft = hideFragmentExp(HOT_WEIBO);

        HotWeiboViewPagerFragment fragment = (HotWeiboViewPagerFragment) rightFragments.get(HOT_WEIBO);

        ((MainTimeLineActivity) getActivity()).setCurrentFragment(fragment);
        fragment.onViewPageSelected(0);
        ft.show(fragment);
        ft.commit();
        ((MainTimeLineActivity) getActivity()).closeRight();
        // fragment.showMenuOnToolBar(R.menu.main_time_line_menu);
        //
        // fragment.buildActionBarNav();
    }

    private void showSettingPage() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
    }

    private boolean showHomePage(boolean reset) {
        if (currentIndex == HOME_INDEX && !reset) {
            return true;
        }
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
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showHomePageImp();

        }
        return false;
    }

    private void showHomePageImp() {
        FragmentTransaction ft = hideFragmentExp(HOME_INDEX);
        MainTimeLineFragment fragment = (MainTimeLineFragment) rightFragments.get(HOME_INDEX);
        ft.show(fragment);
        ft.commit();
        ((MainTimeLineActivity) getActivity()).openRight();
//
//		fragment.showMenuOnToolBar(R.menu.main_time_line_menu);
//
//		fragment.buildActionBarNav();
    }

    private FragmentTransaction hideFragmentExp(int key) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (key) {
            case HOME_INDEX:
                ft.hide(rightFragments.get(FAV_INDEX));
                ft.hide(rightFragments.get(HOT_WEIBO));
                ft.hide(rightFragments.get(HOT_HUA_TI));

                break;
            case FAV_INDEX:
                ft.hide(rightFragments.get(HOME_INDEX));
                ft.hide(rightFragments.get(HOT_WEIBO));
                ft.hide(rightFragments.get(HOT_HUA_TI));
                break;
            case HOT_WEIBO:
                ft.hide(rightFragments.get(HOME_INDEX));
                ft.hide(rightFragments.get(FAV_INDEX));
                ft.hide(rightFragments.get(HOT_HUA_TI));
                break;
            case HOT_HUA_TI:
                ft.hide(rightFragments.get(HOME_INDEX));
                ft.hide(rightFragments.get(FAV_INDEX));
                ft.hide(rightFragments.get(HOT_WEIBO));
                break;
            default:
                break;
        }
        return ft;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    private boolean showFavPage(boolean reset) {
        if (currentIndex == FAV_INDEX && !reset) {
            return true;
        }
        currentIndex = FAV_INDEX;
        if (Utility.isDevicePort() && !reset) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
                    if (currentIndex == FAV_INDEX) {
                        mToolbar.setTitle(R.string.favourite_weibo);
                        showFavPageImp();
                    }

                }
            };
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(AppEventAction.SLIDING_MENU_CLOSED_BROADCAST));
        } else {
            showFavPageImp();

        }
        return false;
    }

    private void showFavPageImp() {
        FragmentTransaction ft = hideFragmentExp(FAV_INDEX);

        MyFavListFragment fragment = (MyFavListFragment) rightFragments.get(FAV_INDEX);

        ft.show(fragment);
        ft.commit();

        ((MainTimeLineActivity) getActivity()).closeRight();

//		fragment.showMenuOnToolBar(R.menu.main_time_line_menu);
//
//		((MyFavListFragment) fragment).buildActionBarAndViewPagerTitles();
    }

    private void readUnreadCountFromDB() {
        TimeLinePosition position = MentionWeiboTimeLineDBTask.getPosition(BeeboApplication.getInstance().getCurrentAccountId());
        TreeSet<Long> hashSet = position.newMsgIds;

        position = MentionCommentsTimeLineDBTask.getPosition(BeeboApplication.getInstance().getCurrentAccountId());
        hashSet = position.newMsgIds;
        position = CommentToMeTimeLineDBTask.getPosition(BeeboApplication.getInstance().getCurrentAccountId());
        hashSet = position.newMsgIds;
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
        // layot.location = (Button) view.findViewById(R.id.btn_location);
        layout.fav = (Button) view.findViewById(R.id.btn_favourite);
        layout.homeCount = (TextView) view.findViewById(R.id.tv_home_count);

        layout.leftDrawerSettingBtn = (Button) view.findViewById(R.id.leftDrawerSettingBtn);

        layout.homeButton = (Button) view.findViewById(R.id.homeButton);

        layout.mHotWeibo = ViewUtility.findViewById(view, R.id.btnHotWeibo);

        layout.mHotHuaTi = ViewUtility.findViewById(view, R.id.btnHotHuaTi);

        layout.mHotModel = ViewUtility.findViewById(view, R.id.btnHotModel);

        return view;
    }

    public void displayCover() {
        // final String picPath =
        // "file:///android_asset/coverImage.jpg";//GlobalContext.getInstance().getAccountBean().getInfo().getCover_image();
        // LogTool.D("mCoverBlureImage: path: " + picPath);
        // Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
        // +
        // getActivity().getResources().getResourcePackageName(R.drawable.cover_image)
        // + "/"
        // +
        // getActivity().getResources().getResourceTypeName(R.drawable.cover_image)
        // + "/"
        // +
        // getActivity().getResources().getResourceEntryName(R.drawable.cover_image));
        mCoverBlureImage.setImageResource(R.drawable.cover_image);// .setOriImageUrl(picPath);
//		mCoverBlureImage.setAlpha(0.5f);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout.home.setOnClickListener(onClickListener);
        // layout.location.setOnClickListener(onClickListener);
        layout.fav.setOnClickListener(onClickListener);

        layout.leftDrawerSettingBtn.setOnClickListener(onClickListener);

        mToolbar = (Toolbar) getActivity().findViewById(R.id.mainTimeLineToolBar);

        layout.mHotWeibo.setOnClickListener(onClickListener);
        layout.mHotHuaTi.setOnClickListener(onClickListener);
        layout.mHotModel.setOnClickListener(onClickListener);

    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_home) {
                showHomePage(false);
                drawButtonsBackground(HOME_INDEX);
            } else if (id == R.id.btn_location) {
                startActivity(new Intent(getActivity(), NearbyTimeLineActivity.class));
            } else if (id == R.id.btn_favourite) {
                showFavPage(false);
                drawButtonsBackground(FAV_INDEX);
            } else if (id == R.id.leftDrawerSettingBtn) {
                showSettingPage();
            } else if (id == R.id.btnHotWeibo) {
                showHotWeibo(false);
                drawButtonsBackground(HOT_WEIBO);

            } else if (id == R.id.btnHotHuaTi) {
                showHotHuaTi(false);
                drawButtonsBackground(HOT_HUA_TI);
//				Intent intent = new Intent(getActivity(), HotHuaTiActivity.class);
//				startActivity(intent);
            } else if (id == R.id.btnHotModel) {
//				Intent intent = new Intent(getActivity(), HotModelActivity.class);
//				startActivity(intent);
            }
            ((MainTimeLineActivity) getActivity()).closeLeftDrawer();
        }
    };

    private void drawButtonsBackground(int position) {

        int hightLight = getResources().getColor(R.color.md_actionbar_bg_color);

        int normalColor = getResources().getColor(R.color.draw_text_color);
        layout.homeButton.setTextColor(normalColor);
        layout.fav.setTextColor(normalColor);
        layout.mHotWeibo.setTextColor(normalColor);
        layout.mHotHuaTi.setTextColor(normalColor);

        switch (position) {
            case HOME_INDEX:
                layout.homeButton.setTextColor(hightLight);
                break;
            case FAV_INDEX:
                layout.fav.setTextColor(hightLight);
                break;
            case HOT_WEIBO: {
                layout.mHotWeibo.setTextColor(hightLight);
                break;
            }
            case HOT_HUA_TI: {
                layout.mHotHuaTi.setTextColor(hightLight);
                break;
            }
        }
    }

    public void setHomeUnreadCount(int count) {
        if (count > 0) {
            layout.homeCount.setVisibility(View.VISIBLE);
            layout.homeCount.setText(String.valueOf(count));
        } else {
            layout.homeCount.setVisibility(View.GONE);
        }
    }

    private class LeftDrawerViewHolder {

        ImageView avatar;

        TextView nickname;

        LinearLayout home;

        Button homeButton;

        TextView homeCount;

        // Button location;
        Button fav;

        Button leftDrawerSettingBtn;

        Button mHotWeibo;

        Button mHotHuaTi;

        Button mHotModel;
    }

}
