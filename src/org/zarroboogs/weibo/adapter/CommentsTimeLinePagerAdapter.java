package org.zarroboogs.weibo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.CommentsByMeTimeLineFragment;
import org.zarroboogs.weibo.fragment.CommentsTimeLineFragment;
import org.zarroboogs.weibo.fragment.CommentsToMeTimeLineFragment;
import org.zarroboogs.weibo.support.lib.AppFragmentPagerAdapter;

public class CommentsTimeLinePagerAdapter extends AppFragmentPagerAdapter {

	private SparseArray<Fragment> fragmentList;

	public CommentsTimeLinePagerAdapter(CommentsTimeLineFragment fragment, ViewPager viewPager, FragmentManager fm, SparseArray<Fragment> fragmentList) {
		super(fm);
		this.fragmentList = fragmentList;
		fragmentList.append(CommentsTimeLineFragment.COMMENTS_TO_ME_CHILD_POSITION, fragment.getCommentsToMeTimeLineFragment());
		fragmentList.append(CommentsTimeLineFragment.COMMENTS_BY_ME_CHILD_POSITION, fragment.getCommentsByMeTimeLineFragment());
		FragmentTransaction transaction = fragment.getChildFragmentManager().beginTransaction();
		if (!fragmentList.get(CommentsTimeLineFragment.COMMENTS_TO_ME_CHILD_POSITION).isAdded())
			transaction.add(viewPager.getId(), fragmentList.get(CommentsTimeLineFragment.COMMENTS_TO_ME_CHILD_POSITION), CommentsToMeTimeLineFragment.class.getName());
		if (!fragmentList.get(CommentsTimeLineFragment.COMMENTS_BY_ME_CHILD_POSITION).isAdded())
			transaction.add(viewPager.getId(), fragmentList.get(CommentsTimeLineFragment.COMMENTS_BY_ME_CHILD_POSITION), CommentsByMeTimeLineFragment.class.getName());
		if (!transaction.isEmpty()) {
			transaction.commit();
			fragment.getChildFragmentManager().executePendingTransactions();
		}
	}

	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	protected String getTag(int position) {
		SparseArray<String> tagList = new SparseArray<String>();
		tagList.append(CommentsTimeLineFragment.COMMENTS_TO_ME_CHILD_POSITION, CommentsToMeTimeLineFragment.class.getName());
		tagList.append(CommentsTimeLineFragment.COMMENTS_BY_ME_CHILD_POSITION, CommentsByMeTimeLineFragment.class.getName());

		return tagList.get(position);
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
	    if (position == 0) {
	        return fragmentList.get(position).getActivity().getResources().getString(R.string.all_people_send_to_me);
        }
	    return fragmentList.get(position).getActivity().getResources().getString(R.string.my_comment);
	}
}
