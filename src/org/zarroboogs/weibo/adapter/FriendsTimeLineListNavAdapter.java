
package org.zarroboogs.weibo.adapter;

import java.util.ArrayList;
import java.util.List;

import lib.org.zarroboogs.weibo.login.utils.LogTool;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.GroupBean;
import org.zarroboogs.weibo.db.task.FriendsTimeLineDBTask;

public class FriendsTimeLineListNavAdapter extends BaseAdapter {
    private Activity activity;
    private String[] valueArray;
    
    private int mSelectId = 0;
    
    public void setSelectId(int id){
    	this.mSelectId = id;
    	notifyDataSetChanged();
    }

	public FriendsTimeLineListNavAdapter(Activity activity, String[] valueArray) {
		this.activity = activity;
		this.valueArray = valueArray;
		
		mSelectId = getRecentNavIndex(FriendsTimeLineDBTask
				.getRecentGroupId(GlobalContext.getInstance()
						.getCurrentAccountId()));
		LogTool.D("RecentGroupID FriendsTimeLineListNavAdapter : " + mSelectId);
		
//		try {
//			mSelectId = Integer.valueOf(FriendsTimeLineDBTask
//					.getRecentGroupId(GlobalContext.getInstance()
//							.getCurrentAccountId()));
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			mSelectId = 0;
//		}
	}

    private int getRecentNavIndex(String currentGroupId) {
        List<GroupBean> list = new ArrayList<GroupBean>();
        if (GlobalContext.getInstance().getGroup() != null) {
            list = GlobalContext.getInstance().getGroup().getLists();
        } else {
            list = new ArrayList<GroupBean>();
        }
        return getIndexFromGroupId(currentGroupId, list);
    }
    
    private int getIndexFromGroupId(String id, List<GroupBean> list) {

        if (list == null || list.size() == 0) {
            return 0;
        }

        int index = 0;

        if (id.equals("0")) {
            index = 0;
        } else if (id.equals("1")) {
            index = 1;
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdstr().equals(id)) {
                index = i + 2;
                break;
            }
        }
        return index;
    }
    
    
    @Override
    public int getCount() {
        return valueArray.length;
    }

    @Override
    public Object getItem(int position) {
        return valueArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.spinner_selector_text_view, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.weiboGroupName);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.textView.setText(valueArray[position]);
        if (position == mSelectId) {
			holder.textView.setTextColor(convertView.getResources().getColor(R.color.md_actionbar_bg_color));
		}else {
			holder.textView.setTextColor(convertView.getResources().getColor(R.color.draw_text_color));
		}
        
//
//        if (position != 0) {
//            holder.textView.setText(valueArray[position]);
//        } else {
//            AccountBean accountBean = GlobalContext.getInstance().getAccountBean();
//            holder.textView.setText(accountBean.getUsernick());
//        }

        return convertView;
    }

    // @Override
    // public View getDropDownView(int position, View convertView, ViewGroup parent) {
    // ViewHolder holder;
    //
    // if (convertView == null || convertView.getTag() == null) {
    // LayoutInflater inflater = activity.getLayoutInflater();
    // convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
    // holder = new ViewHolder();
    // holder.textView = (TextView) convertView;
    // } else {
    // holder = (ViewHolder) convertView.getTag();
    // }
    //
    // holder.textView.setText(valueArray[position]);
    // return convertView;
    //
    // }

    private static class ViewHolder {
        TextView textView;
    }
};
