
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.adapter.CommentsTimeLinePagerAdapter;
import org.zarroboogs.weibo.bean.UnreadTabIndex;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.support.lib.LongClickableLinkMovementMethod;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;


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

import com.andforce.common.view.SlidingTabLayout;

public class CommentsTimeLineFragment extends BaseStateFragment implements MainTimeLineActivity.ScrollableListFragment {

    private ViewPager viewPager;

    private SparseArray<Fragment> childrenFragments = new SparseArray<Fragment>();

    public static final int COMMENTS_TO_ME_CHILD_POSITION = 0;

    public static final int COMMENTS_BY_ME_CHILD_POSITION = 1;
    private SlidingTabLayout mSlidingTabLayout;

    public static CommentsTimeLineFragment newInstance() {
        CommentsTimeLineFragment fragment = new CommentsTimeLineFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments_time_line_fragment_layout, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.commentsViewpager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.commentsSlingTab);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(onPageChangeListener);
        CommentsTimeLinePagerAdapter adapter = new CommentsTimeLinePagerAdapter(this, viewPager, getChildFragmentManager(),
                childrenFragments);
        viewPager.setAdapter(adapter);
        mSlidingTabLayout.setViewPager(viewPager);
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

    }


    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
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

    public CommentsToMeTimeLineFragment getCommentsToMeTimeLineFragment() {
        CommentsToMeTimeLineFragment fragment = ((CommentsToMeTimeLineFragment) getChildFragmentManager().findFragmentByTag(
                CommentsToMeTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = new CommentsToMeTimeLineFragment(BeeboApplication.getInstance().getAccountBean(), BeeboApplication
                    .getInstance().getAccountBean().getInfo());
        }

        return fragment;
    }

    public CommentsByMeTimeLineFragment getCommentsByMeTimeLineFragment() {
        CommentsByMeTimeLineFragment fragment = ((CommentsByMeTimeLineFragment) getChildFragmentManager().findFragmentByTag(
                CommentsByMeTimeLineFragment.class.getName()));
        if (fragment == null) {
            fragment = new CommentsByMeTimeLineFragment(BeeboApplication.getInstance().getAccountBean(), BeeboApplication
                    .getInstance().getAccountBean().getInfo());
        }

        return fragment;
    }

    @Override
    public void scrollToTop() {
        AbsBaseTimeLineFragment fragment = (AbsBaseTimeLineFragment) (childrenFragments.get(viewPager.getCurrentItem()));
        Utility.stopListViewScrollingAndScrollToTop(fragment.getListView());
    }

}
