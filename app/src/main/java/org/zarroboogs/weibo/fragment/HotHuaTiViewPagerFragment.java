
package org.zarroboogs.weibo.fragment;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.viewpagerfragment.ChildPage;
import org.zarroboogs.weibo.widget.viewpagerfragment.ViewPagerFragment;

import android.content.res.Resources;
import android.os.Bundle;

public class HotHuaTiViewPagerFragment extends ViewPagerFragment {

    public static HotHuaTiViewPagerFragment newInstance() {
        HotHuaTiViewPagerFragment fragment = new HotHuaTiViewPagerFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }
    

    @Override
	public ArrayList<ChildPage> buildChildPage() {
		// TODO Auto-generated method stub
    	ArrayList<ChildPage> sparseArray = new ArrayList<ChildPage>();
		
		Resources re = getActivity().getResources();
		sparseArray.add(0, new ChildPage("1小时热榜", new HotHuaTiFragment("-1")) );
		sparseArray.add(1, new ChildPage("幽默搞笑", new HotHuaTiFragment("140")) );
		sparseArray.add(2, new ChildPage("电影热榜", new HotHuaTiFragment("100")) );
		sparseArray.add(3, new ChildPage("消费数码", new HotHuaTiFragment("131")) );

		sparseArray.add(4, new ChildPage("IT互联网", new HotHuaTiFragment("138")) );
		sparseArray.add(5, new ChildPage("摄影热榜",new HotHuaTiFragment("123")) );
		sparseArray.add(6, new ChildPage("创意征集", new HotHuaTiFragment("9")) );
		sparseArray.add(7, new ChildPage("动物萌宠", new HotHuaTiFragment("128")) );
		
		String[] select = SettingUtils.getHotHuaTioSelected();
		Arrays.sort(select);
		
		List<String> selected = new ArrayList<String>();
		for (String str : select) {
			selected.add(str);
		}
		ArrayList<ChildPage> result = new ArrayList<ChildPage>();
		if (!selected.contains("0")) {
			result.add(sparseArray.get(0));
		}
		if (!selected.contains("1")) {
			result.add(sparseArray.get(1));
		}
		for (String string : select) {
			int key = Integer.valueOf(string);
			result.add(sparseArray.get(key));
		}
		
		return result;
	}

	@Override
	public void onViewPageSelected(int id) {
		// TODO Auto-generated method stub
		BaseHotHuaTiFragment bf = (BaseHotHuaTiFragment) getCurrentFargment();
		bf.onPageSelected();
	}
	
	@Override
	public void scrollToTop() {
		// TODO Auto-generated method stub
		BaseHotHuaTiFragment bf = (BaseHotHuaTiFragment) getCurrentFargment();
        Utility.stopListViewScrollingAndScrollToTop(bf.getListView());
	}
}
