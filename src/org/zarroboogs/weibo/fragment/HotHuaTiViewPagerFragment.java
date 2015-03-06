
package org.zarroboogs.weibo.fragment;

import java.util.ArrayList;
import java.util.List;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.adapter.HotHuaTiViewPagerAdapter;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.support.lib.LongClickableLinkMovementMethod;
import org.zarroboogs.weibo.support.utils.Utility;

import com.example.android.common.view.SlidingTabLayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class HotHuaTiViewPagerFragment extends BaseStateFragment implements MainTimeLineActivity.ScrollableListFragment {

    private ViewPager viewPager;

    private SparseArray<Fragment> childrenFragments = new SparseArray<Fragment>();

    private SlidingTabLayout mSlidingTabLayout;

    public static HotHuaTiViewPagerFragment newInstance() {
        HotHuaTiViewPagerFragment fragment = new HotHuaTiViewPagerFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mention_timeline_fragment_layout, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.mentionViewpager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.mentionSlidingTab);
        
        childrenFragments.append(0, new HotHuaTiOneHourFragment());
        childrenFragments.append(1, new HotHuaTiFragmentFilm());
        childrenFragments.append(2, new HotHuaTiFragmentDigit());
        childrenFragments.append(3, new HotHuaTiFragmentHumor());
        childrenFragments.append(4, new HotHuaTiFragmentIT());
        childrenFragments.append(5, new HotHuaTiFragmentShot());
        childrenFragments.append(6, new HotHuaTiFragmentOrig());
        childrenFragments.append(7, new HotHuaTiFragmentPet());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(onPageChangeListener);
        
        List<String> titleList = new ArrayList<String>();
        titleList.add("1小时热榜");
        titleList.add("电影热榜");
        titleList.add("消费数码");
        titleList.add("幽默搞笑");
        titleList.add("IT互联网");
        titleList.add("摄影热榜");
        titleList.add("创意征集");
        titleList.add("动物萌宠");
        HotHuaTiViewPagerAdapter adapter = new HotHuaTiViewPagerAdapter(this, viewPager, getChildFragmentManager(), childrenFragments, titleList);
        
        viewPager.setAdapter(adapter);
        mSlidingTabLayout.setViewPager(viewPager);
    }

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            ((LeftMenuFragment) ((MainTimeLineActivity) getActivity()).getLeftMenuFragment()).mentionsTabIndex = position;
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


    
    @Override
    public void scrollToTop() {
        AbsBaseTimeLineFragment fragment = (AbsBaseTimeLineFragment) (childrenFragments.get(viewPager.getCurrentItem()));
        Utility.stopListViewScrollingAndScrollToTop(fragment.getListView());
    }
}
