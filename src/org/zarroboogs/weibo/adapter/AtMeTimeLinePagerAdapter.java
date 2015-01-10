
package org.zarroboogs.weibo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.CommentsByMeTimeLineFragment;
import org.zarroboogs.weibo.fragment.CommentsToMeTimeLineFragment;
import org.zarroboogs.weibo.fragment.MentionsCommentTimeLineFragment;
import org.zarroboogs.weibo.fragment.AtMeTimeLineFragment;
import org.zarroboogs.weibo.fragment.MentionsWeiboTimeLineFragment;
import org.zarroboogs.weibo.support.lib.AppFragmentPagerAdapter;

public class AtMeTimeLinePagerAdapter extends AppFragmentPagerAdapter {

    private SparseArray<Fragment> fragmentList;

    public AtMeTimeLinePagerAdapter(AtMeTimeLineFragment fragment, ViewPager viewPager, FragmentManager fm,
            SparseArray<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
        fragmentList.append(AtMeTimeLineFragment.AT_ME_WEIBO,fragment.getMentionsWeiboTimeLineFragment());
        fragmentList.append(AtMeTimeLineFragment.AT_ME_COMMENT,fragment.getMentionsCommentTimeLineFragment());
        
        fragmentList.append(AtMeTimeLineFragment.COMMENT_TO_ME,fragment.getCommentsToMeTimeLineFragment());
        fragmentList.append(AtMeTimeLineFragment.COMMENT_BY_ME,fragment.getCommentsByMeTimeLineFragment());
        
        
        
        FragmentTransaction transaction = fragment.getChildFragmentManager().beginTransaction();
        if (!fragmentList.get(AtMeTimeLineFragment.AT_ME_WEIBO).isAdded()){
            transaction.add(viewPager.getId(), fragmentList.get(AtMeTimeLineFragment.AT_ME_WEIBO),
                    MentionsWeiboTimeLineFragment.class.getName());
        }
        
        if (!fragmentList.get(AtMeTimeLineFragment.AT_ME_COMMENT).isAdded()){
            transaction.add(viewPager.getId(), fragmentList.get(AtMeTimeLineFragment.AT_ME_COMMENT),
                    MentionsCommentTimeLineFragment.class.getName());
        }
        
        
        if (!fragmentList.get(AtMeTimeLineFragment.COMMENT_TO_ME).isAdded()){
            transaction.add(viewPager.getId(), fragmentList.get(AtMeTimeLineFragment.COMMENT_TO_ME),
            		CommentsToMeTimeLineFragment.class.getName());
        }
        
        if (!fragmentList.get(AtMeTimeLineFragment.COMMENT_BY_ME).isAdded()){
            transaction.add(viewPager.getId(), fragmentList.get(AtMeTimeLineFragment.COMMENT_BY_ME),
            		CommentsByMeTimeLineFragment.class.getName());
        }
        
        
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
        tagList.append(AtMeTimeLineFragment.AT_ME_WEIBO, MentionsWeiboTimeLineFragment.class.getName());
        tagList.append(AtMeTimeLineFragment.AT_ME_COMMENT, MentionsCommentTimeLineFragment.class.getName());
        
        tagList.append(AtMeTimeLineFragment.COMMENT_TO_ME, CommentsToMeTimeLineFragment.class.getName());
        tagList.append(AtMeTimeLineFragment.COMMENT_BY_ME, CommentsByMeTimeLineFragment.class.getName());

        return tagList.get(position);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
    	switch (position) {
		case AtMeTimeLineFragment.AT_ME_WEIBO:
			return fragmentList.get(position).getActivity().getResources().getString(R.string.mentions_weibo);

		case AtMeTimeLineFragment.AT_ME_COMMENT:
			
			return fragmentList.get(position).getActivity().getResources().getString(R.string.mentions_to_me);
			
		case AtMeTimeLineFragment.COMMENT_TO_ME:
			return fragmentList.get(position).getActivity().getResources().getString(R.string.all_people_send_to_me);
			
		case AtMeTimeLineFragment.COMMENT_BY_ME:
			return fragmentList.get(position).getActivity().getResources().getString(R.string.my_comment);
		default:
			return fragmentList.get(position).getActivity().getResources().getString(R.string.mentions_weibo);
		}
        
    }
}
