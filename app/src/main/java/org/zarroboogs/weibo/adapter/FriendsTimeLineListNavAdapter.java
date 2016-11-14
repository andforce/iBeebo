package org.zarroboogs.weibo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.GroupBean;
import org.zarroboogs.weibo.db.task.FriendsTimeLineDBTask;

public class FriendsTimeLineListNavAdapter extends BaseAdapter {
    private Activity activity;
    private List<GroupBean> mGroupBeans;

    private int mSelectId = 0;

    public void setSelectId(int id) {
        this.mSelectId = id;
        notifyDataSetChanged();
    }

    public void refresh(List<GroupBean> group) {
        this.mGroupBeans = group;
        notifyDataSetChanged();
    }

    public FriendsTimeLineListNavAdapter(Activity activity, List<GroupBean> groupBeanList) {
        this.activity = activity;
        mGroupBeans = groupBeanList;
        mSelectId = getRecentNavIndex(FriendsTimeLineDBTask.getRecentGroupId(BeeboApplication.getInstance().getCurrentAccountId()));
    }

    private int getRecentNavIndex(String currentGroupId) {
        List<GroupBean> list;
        if (BeeboApplication.getInstance().getGroup() != null) {
            list = BeeboApplication.getInstance().getGroup().getLists();
        } else {
            list = new ArrayList<>();
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
        return mGroupBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupBeans.get(position);
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
            holder.mGroupName = (TextView) convertView.findViewById(R.id.weiboGroupName);
            holder.mGroupCount = (TextView) convertView.findViewById(R.id.weiboGroupCount);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GroupBean groupBean = mGroupBeans.get(position);

        holder.mGroupName.setText(groupBean.getName());
        holder.mGroupCount.setText(groupBean.getMember_count() + "");

        if (groupBean.getId().equals("0") || groupBean.getId().equals("1")) {
            holder.mGroupCount.setText("");
        }

        if (position == mSelectId) {
            holder.mGroupName.setTextColor(convertView.getResources().getColor(R.color.md_actionbar_bg_color));
            holder.mGroupCount.setTextColor(convertView.getResources().getColor(R.color.md_actionbar_bg_color));
        } else {
            holder.mGroupName.setTextColor(convertView.getResources().getColor(R.color.draw_text_color));
            holder.mGroupCount.setTextColor(convertView.getResources().getColor(R.color.draw_text_color));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView mGroupName;
        TextView mGroupCount;
    }
}
