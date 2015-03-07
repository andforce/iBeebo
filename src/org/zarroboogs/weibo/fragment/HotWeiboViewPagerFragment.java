
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

public class HotWeiboViewPagerFragment extends BaseStateFragment implements MainTimeLineActivity.ScrollableListFragment {

    private ViewPager viewPager;

    private SparseArray<Fragment> childrenFragments = new SparseArray<Fragment>();

    private SlidingTabLayout mSlidingTabLayout;

    public static HotWeiboViewPagerFragment newInstance() {
        HotWeiboViewPagerFragment fragment = new HotWeiboViewPagerFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mention_timeline_fragment_layout, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.mentionViewpager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.mentionSlidingTab);
        
        childrenFragments.append(0, new HotWeiboFragment());
        childrenFragments.append(1, new HotWeiboFragmentYestoday());
        childrenFragments.append(2, new HotWeiboFragmentQianTian());
        childrenFragments.append(3, new HotWeiboFragmentZuiYou());
        childrenFragments.append(4, new HotWeiboFragmentVideo());
        childrenFragments.append(5, new HotWeiboFragmentBaoLiao());
        childrenFragments.append(6, new HotWeiboFragmentXiaoHua());
        childrenFragments.append(7, new HotWeiboFragmentPet());
        childrenFragments.append(8, new HotWeiboFragmentMeiNv());
        childrenFragments.append(9, new HotWeiboFragmentKeji());
        childrenFragments.append(10, new HotWeiboFragmentTravel());
        childrenFragments.append(11, new HotWeiboFragmentMeiTu());
        
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(onPageChangeListener);
        
        List<String> titleList = new ArrayList<String>();
        titleList.add("当前");
        titleList.add("昨天");
        titleList.add("前天");
        titleList.add("神最右");
        titleList.add("视频");
        titleList.add("爆料");
        titleList.add("笑话");
        titleList.add("萌宠");
        titleList.add("美女");
        titleList.add("科技");
        titleList.add("旅行");
        titleList.add("美图");

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
