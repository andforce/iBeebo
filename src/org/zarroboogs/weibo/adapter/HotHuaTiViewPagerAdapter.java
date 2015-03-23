
package org.zarroboogs.weibo.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import org.zarroboogs.weibo.support.lib.AppFragmentPagerAdapter;

public class HotHuaTiViewPagerAdapter extends AppFragmentPagerAdapter {

    private SparseArray<Fragment> mFragmentList;
    private List<String> mTitleList;
    private SparseArray<String> tagList = new SparseArray<String>();
    

    public HotHuaTiViewPagerAdapter(Fragment fragment, ViewPager viewPager, FragmentManager fm, SparseArray<Fragment> fragmentList, List<String> titleList) {
        super(fm);
        this.mFragmentList = fragmentList;
        this.mTitleList = titleList;

        for (int i = 0; i < this.mFragmentList.size(); i++) {
			tagList.append(i, this.mFragmentList.get(i).getClass().getName());
		}

        
        FragmentTransaction transaction = fragment.getChildFragmentManager().beginTransaction();
        
        for (int i = 0; i < fragmentList.size(); i++) {
			Fragment ft = this.mFragmentList.get(i);
			if (!ft.isAdded()) {
				transaction.add(viewPager.getId(), mFragmentList.get(i), ft.getClass().getName());
			}
		}
        
        if (!transaction.isEmpty()) {
            transaction.commit();
            fragment.getChildFragmentManager().executePendingTransactions();
        }
    }

    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    protected String getTag(int position) {
        return tagList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
    	return mTitleList.get(position);
    }
}
