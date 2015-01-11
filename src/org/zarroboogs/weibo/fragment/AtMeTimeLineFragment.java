
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.adapter.AtMeTimeLinePagerAdapter;
import org.zarroboogs.weibo.bean.UnreadTabIndex;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.support.lib.LongClickableLinkMovementMethod;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;

import com.example.android.common.view.SlidingTabLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class AtMeTimeLineFragment extends BaseStateFragment implements MainTimeLineActivity.ScrollableListFragment {

    private ViewPager viewPager;

    private SparseArray<Fragment> childrenFragments = new SparseArray<Fragment>();

	public static final int AT_ME_WEIBO = 0;
	public static final int AT_ME_COMMENT = 1;

	public static final int COMMENT_TO_ME = 2;
	public static final int COMMENT_BY_ME = 3;
    

    private SlidingTabLayout mSlidingTabLayout;

    public static AtMeTimeLineFragment newInstance() {
        AtMeTimeLineFragment fragment = new AtMeTimeLineFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if ((((MainTimeLineActivity) getActivity()).getLeftMenuFragment()).getCurrentIndex() == LeftMenuFragment.MENTIONS_INDEX) {
            buildActionBarAndViewPagerTitles(((MainTimeLineActivity) getActivity()).getLeftMenuFragment().mentionsTabIndex);
        }
    }

    // private ActionBar.Tab buildMentionsCommentTab(SimpleTwoTabsListener tabListener) {
    // ActionBar.Tab mentionsCommentTab;
    // View customView =
    // getActivity().getLayoutInflater().inflate(R.layout.ab_tab_custom_view_layout, null);
    // ((TextView) customView.findViewById(R.id.title)).setText(R.string.mentions_to_me);
    // mentionsCommentTab =
    // getActivity().getActionBar().newTab().setCustomView(customView).setTag(MentionsCommentTimeLineFragment.class.getName())
    // .setTabListener(tabListener);
    // tabMap.append(AT_ME_COMMENT, mentionsCommentTab);
    // return mentionsCommentTab;
    // }
    //
    // private ActionBar.Tab buildMentionsWeiboTab(SimpleTwoTabsListener tabListener) {
    // ActionBar.Tab mentionsWeiboTab;
    // View customView =
    // getActivity().getLayoutInflater().inflate(R.layout.ab_tab_custom_view_layout, null);
    // ((TextView) customView.findViewById(R.id.title)).setText(R.string.mentions_weibo);
    // mentionsWeiboTab =
    // getActivity().getActionBar().newTab().setCustomView(customView).setTag(MentionsWeiboTimeLineFragment.class.getName())
    // .setTabListener(tabListener);
    // tabMap.append(AT_ME_WEIBO, mentionsWeiboTab);
    // return mentionsWeiboTab;
    // }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mention_timeline_fragment_layout, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.mentionViewpager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.mentionSlidingTab);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(onPageChangeListener);
        AtMeTimeLinePagerAdapter adapter = new AtMeTimeLinePagerAdapter(this, viewPager, getChildFragmentManager(),
                childrenFragments);
        viewPager.setAdapter(adapter);
        mSlidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            int mentionsTabIndex = getArguments().getInt("mentionsTabIndex");
            buildActionBarAndViewPagerTitles(mentionsTabIndex);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return;
        }
        UnreadTabIndex unreadTabIndex = (UnreadTabIndex) intent
                .getSerializableExtra(BundleArgsConstants.OPEN_NAVIGATION_INDEX_EXTRA);
        if (unreadTabIndex == null) {
            return;
        }
        switch (unreadTabIndex) {
            case MENTION_WEIBO:
                ((MainTimeLineActivity) getActivity()).getLeftMenuFragment().switchCategory(LeftMenuFragment.MENTIONS_INDEX);
                viewPager.setCurrentItem(0);
                intent.putExtra(BundleArgsConstants.OPEN_NAVIGATION_INDEX_EXTRA, UnreadTabIndex.NONE);
                break;
            case MENTION_COMMENT:
                ((MainTimeLineActivity) getActivity()).getLeftMenuFragment().switchCategory(LeftMenuFragment.MENTIONS_INDEX);
                viewPager.setCurrentItem(1);
                intent.putExtra(BundleArgsConstants.OPEN_NAVIGATION_INDEX_EXTRA, UnreadTabIndex.NONE);
                break;
			case COMMENT_TO_ME:
                ((MainTimeLineActivity) getActivity()).getLeftMenuFragment().switchCategory(LeftMenuFragment.MENTIONS_INDEX);
                viewPager.setCurrentItem(2);
                intent.putExtra(BundleArgsConstants.OPEN_NAVIGATION_INDEX_EXTRA, UnreadTabIndex.NONE);
				break;
			case NONE:
				break;
			default:
				break;
        }

    }

    public void buildActionBarAndViewPagerTitles(int nav) {
        ((MainTimeLineActivity) getActivity()).setCurrentFragment(this);

        // if (Utility.isDevicePort()) {
        // ((MainTimeLineActivity) getActivity()).setTitle(R.string.mentions);
        // getActivity().getActionBar().setIcon(R.drawable.repost_light);
        // } else {
        // ((MainTimeLineActivity) getActivity()).setTitle("");
        // getActivity().getActionBar().setIcon(R.drawable.beebo_launcher);
        // }
        // ActionBar actionBar = getActivity().getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(Utility.isDevicePort());
        // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // actionBar.removeAllTabs();
        // SimpleTwoTabsListener tabListener = new SimpleTwoTabsListener(viewPager);
        //
        // ActionBar.Tab mentionsWeiboTab = getWeiboTab();
        // if (mentionsWeiboTab == null) {
        // mentionsWeiboTab = buildMentionsWeiboTab(tabListener);
        // }
        // actionBar.addTab(mentionsWeiboTab);
        //
        // ActionBar.Tab mentionsCommentTab = getCommentTab();
        // if (mentionsCommentTab == null) {
        // mentionsCommentTab = buildMentionsCommentTab(tabListener);
        // }
        //
        // actionBar.addTab(mentionsCommentTab);
        //
        // if (actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS && nav > -1) {
        // if (viewPager != null) {
        // viewPager.setCurrentItem(nav, false);
        // }
        //
        // }
    }

    // public ActionBar.Tab getWeiboTab() {
    // return tabMap.get(AT_ME_WEIBO);
    // }
    //
    // public ActionBar.Tab getCommentTab() {
    // return tabMap.get(AT_ME_COMMENT);
    // }

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // ActionBar ab = getActivity().getActionBar();
            // if (getActivity().getActionBar().getNavigationMode() ==
            // ActionBar.NAVIGATION_MODE_TABS && ab.getTabAt(position) == tabMap.get(position)) {
            // ab.setSelectedNavigationItem(position);
            // }
            ((LeftMenuFragment) ((MainTimeLineActivity) getActivity()).getLeftMenuFragment()).mentionsTabIndex = position;
            clearActionMode();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            switch (state) {
                case ViewPager.SCROLL_STATE_SETTLING:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LongClickableLinkMovementMethod.getInstance().setLongClickable(true);

                        }
                    }, ViewConfiguration.getLongPressTimeout());
                    break;
                default:
                    LongClickableLinkMovementMethod.getInstance().setLongClickable(false);
                    break;
            }
        }
    };

    public MentionsCommentTimeLineFragment getMentionsCommentTimeLineFragment() {
        MentionsCommentTimeLineFragment fragment = ((MentionsCommentTimeLineFragment) getChildFragmentManager()
                .findFragmentByTag(
                        MentionsCommentTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = new MentionsCommentTimeLineFragment(GlobalContext.getInstance().getAccountBean(),
                    GlobalContext.getInstance().getAccountBean().getInfo(), GlobalContext.getInstance().getSpecialToken());
        }

        return fragment;
    }

    public MentionsWeiboTimeLineFragment getMentionsWeiboTimeLineFragment() {
        MentionsWeiboTimeLineFragment fragment = ((MentionsWeiboTimeLineFragment) getChildFragmentManager()
                .findFragmentByTag(
                        MentionsWeiboTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = new MentionsWeiboTimeLineFragment(GlobalContext.getInstance().getAccountBean(), GlobalContext
                    .getInstance().getAccountBean().getInfo(),
                    GlobalContext.getInstance().getSpecialToken());
        }

        return fragment;
    }

    public CommentsToMeTimeLineFragment getCommentsToMeTimeLineFragment() {
        CommentsToMeTimeLineFragment fragment = ((CommentsToMeTimeLineFragment) getChildFragmentManager().findFragmentByTag(
                CommentsToMeTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = new CommentsToMeTimeLineFragment(GlobalContext.getInstance().getAccountBean(), GlobalContext
                    .getInstance().getAccountBean().getInfo(),
                    GlobalContext.getInstance().getSpecialToken());
        }

        return fragment;
    }

    public CommentsByMeTimeLineFragment getCommentsByMeTimeLineFragment() {
        CommentsByMeTimeLineFragment fragment = ((CommentsByMeTimeLineFragment) getChildFragmentManager().findFragmentByTag(
                CommentsByMeTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = new CommentsByMeTimeLineFragment(GlobalContext.getInstance().getAccountBean(), GlobalContext
                    .getInstance().getAccountBean().getInfo(),
                    GlobalContext.getInstance().getSpecialToken());
        }
        return fragment;
    }
    
    @Override
    public void scrollToTop() {
        AbsBaseTimeLineFragment fragment = (AbsBaseTimeLineFragment) (childrenFragments.get(viewPager.getCurrentItem()));
        Utility.stopListViewScrollingAndScrollToTop(fragment.getListView());
    }

    public void clearActionMode() {
        getMentionsCommentTimeLineFragment().clearActionMode();
        getMentionsWeiboTimeLineFragment().clearActionMode();
    }
}
