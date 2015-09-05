package org.zarroboogs.weibo.widget.viewpagerfragment;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import org.zarroboogs.weibo.support.lib.AppFragmentPagerAdapter;

public class ViewPagerFragmentAdapter extends AppFragmentPagerAdapter {

	private ArrayList<ChildPage> mFragmentList;

	public ViewPagerFragmentAdapter(Fragment fragment, ViewPager viewPager, FragmentManager fm, ArrayList<ChildPage> fragmentList) {
		super(fm);
		this.mFragmentList = fragmentList;

		FragmentTransaction transaction = fragment.getChildFragmentManager().beginTransaction();

		for (int i = 0; i < fragmentList.size(); i++) {
			Fragment ft = this.mFragmentList.get(i).getmFragment();
			if (!ft.isAdded()) {
				transaction.add(viewPager.getId(), ft, ft.getClass().getName() + i);
			}
		}

		if (!transaction.isEmpty()) {
			transaction.commit();
			fragment.getChildFragmentManager().executePendingTransactions();
		}
	}

	public Fragment getItem(int position) {
		return mFragmentList.get(position).getmFragment();
	}

	@Override
	protected String getTag(int position) {
		return mFragmentList.get(position).getmFragment().getTag();
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentList.get(position).getmFragmentTitle();
	}
}
