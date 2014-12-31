package org.zarroboogs.weibo;

import java.util.ArrayList;
import java.util.List;

import org.zarroboogs.weibo.bean.WeiboWeiba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChangeWeibaAdapter extends BaseAdapter {

	Context context;
	List<WeiboWeiba> listdata = new ArrayList<WeiboWeiba>();

	public ChangeWeibaAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return listdata.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listdata.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setWeibas(List<WeiboWeiba> datas) {
		listdata.clear();
		listdata.addAll(datas);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(R.layout.change_weiba_item, null);
			holder.weibaNameTextView = (TextView) convertView.findViewById(R.id.weibaName);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.weibaNameTextView.setText(listdata.get(position).getText());
		return convertView;
	}

	class Holder {
		public TextView weibaNameTextView;
	}

}
