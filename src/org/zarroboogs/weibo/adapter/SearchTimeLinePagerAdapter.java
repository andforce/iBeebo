
package org.zarroboogs.weibo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.fragment.MentionsCommentTimeLineFragment;
import org.zarroboogs.weibo.fragment.MentionsWeiboTimeLineFragment;
import org.zarroboogs.weibo.fragment.SearchMainParentFragment;
import org.zarroboogs.weibo.fragment.SearchStatusFragment;
import org.zarroboogs.weibo.fragment.SearchUserFragment;
import org.zarroboogs.weibo.support.lib.AppFragmentPagerAdapter;

public class SearchTimeLinePagerAdapter extends AppFragmentPagerAdapter {

    private SparseArray<Fragment> fragmentList;

    public SearchTimeLinePagerAdapter(SearchMainParentFragment fragment, ViewPager viewPager, FragmentManager fm,
            MainTimeLineActivity activity,
            SparseArray<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
        fragmentList.append(0, fragment.getSearchWeiboFragment());
        fragmentList.append(1, fragment.getSearchUserFragment());
        FragmentTransaction transaction = fragment.getChildFragmentManager().beginTransaction();
        if (!fragmentList.get(0).isAdded())
            transaction.add(viewPager.getId(), fragmentList.get(0), SearchStatusFragment.class.getName());
        if (!fragmentList.get(1).isAdded())
            transaction.add(viewPager.getId(), fragmentList.get(1), SearchUserFragment.class.getName());
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
        tagList.append(0, MentionsWeiboTimeLineFragment.class.getName());
        tagList.append(0, MentionsCommentTimeLineFragment.class.getName());

        return tagList.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return fragmentList.get(position).getActivity().getResources().getString(R.string.weibo_weibo);
        } else {
            return fragmentList.get(position).getActivity().getResources().getString(R.string.user);
        }
    }

}
