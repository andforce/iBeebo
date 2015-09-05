
package org.zarroboogs.weibo.fragment;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.widget.viewpagerfragment.ChildPage;
import org.zarroboogs.weibo.widget.viewpagerfragment.ViewPagerFragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseArray;

public class HotWeiboViewPagerFragment extends ViewPagerFragment {


    public static HotWeiboViewPagerFragment newInstance() {
        HotWeiboViewPagerFragment fragment = new HotWeiboViewPagerFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
	public ArrayList<ChildPage> buildChildPage() {
		// TODO Auto-generated method stub
    	SparseArray<ChildPage> sparseArray = new SparseArray<ChildPage>();
		
		Resources re = getActivity().getResources();
		sparseArray.append(0, new ChildPage("当前", new HotWeiboViewPagerChildFragment("-1")) );
		sparseArray.append(1, new ChildPage("昨天", new HotWeiboViewPagerChildFragment("8899")) );
		sparseArray.append(2, new ChildPage("前天", new HotWeiboViewPagerChildFragment("8799")) );
		sparseArray.append(3, new ChildPage("神最右", new HotWeiboViewPagerChildFragment("6399")) );
		sparseArray.append(4, new ChildPage("视频", new HotWeiboViewPagerChildFragment("1199")) );
		sparseArray.append(5, new ChildPage("爆料",new HotWeiboViewPagerChildFragment("1799")) );
		sparseArray.append(6, new ChildPage("笑话", new HotWeiboViewPagerChildFragment("1899")) );
		sparseArray.append(7, new ChildPage("萌宠", new HotWeiboViewPagerChildFragment("2799")) );
		sparseArray.append(8, new ChildPage("美女", new HotWeiboViewPagerChildFragment("2299")) );
		sparseArray.append(9, new ChildPage("科技", new HotWeiboViewPagerChildFragment("2099")) );
		sparseArray.append(10, new ChildPage("旅行", new HotWeiboViewPagerChildFragment("2599")) );
		sparseArray.append(11, new ChildPage("美图", new HotWeiboViewPagerChildFragment("2899")) );
		
		String[] select = SettingUtils.getHotWeiboSelected();
		
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
    	BaseHotWeiboFragment hotWeiboFragment = (BaseHotWeiboFragment) getCurrentFargment();
    	hotWeiboFragment.onPageSelected();
    	DevLog.printLog("onViewPageSelected", "" + id);
    	
	}
}
