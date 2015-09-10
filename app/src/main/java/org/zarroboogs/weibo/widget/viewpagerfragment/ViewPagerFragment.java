
package org.zarroboogs.weibo.widget.viewpagerfragment;

import java.util.ArrayList;

import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.support.utils.Utility;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andforce.common.view.SlidingTabLayout;

public abstract class ViewPagerFragment extends BaseStateFragment implements MainTimeLineActivity.ScrollableListFragment {

    private ViewPager viewPager;

    private ArrayList<ChildPage> childrenFragments = new ArrayList<ChildPage>();

    private SlidingTabLayout mSlidingTabLayout;

    public abstract ArrayList<ChildPage> buildChildPage();
    
    public abstract void onViewPageSelected(int id);
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mention_timeline_fragment_layout, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.mentionViewpager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.mentionSlidingTab);
        
        return view;
    }

    public Fragment getCurrentFargment(){
    	return childrenFragments.get(viewPager.getCurrentItem()).getmFragment();
    }
    
    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    	
    	childrenFragments = buildChildPage();
    	if (childrenFragments == null || childrenFragments.size() == 0) {
			throw new RuntimeException("Child Page is Null");
		}
    	
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setOffscreenPageLimit(2);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(this, viewPager, getChildFragmentManager(), childrenFragments);
        
        viewPager.setAdapter(adapter);
        mSlidingTabLayout.setViewPager(viewPager);
        
        mSlidingTabLayout.setOnPageChangeListener(onPageChangeListener);
    }

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
        	super.onPageSelected(position);
        	onViewPageSelected(position);
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        	super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        	DevLog.printLog("SimpleOnPageChangeListener onPageScrolled", "" + position + "  " + positionOffset);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            DevLog.printLog("SimpleOnPageChangeListener onPageScrollStateChanged", "" + state);
            switch (state) {
                case ViewPager.SCROLL_STATE_SETTLING:
                    break;
                default:
                    break;
            }
        }
    };


    
    @Override
    public void scrollToTop() {
        AbsBaseTimeLineFragment<?> fragment = (AbsBaseTimeLineFragment<?>) (childrenFragments.get(viewPager.getCurrentItem()).getmFragment());
        Utility.stopListViewScrollingAndScrollToTop(fragment.getListView());
    }
}
