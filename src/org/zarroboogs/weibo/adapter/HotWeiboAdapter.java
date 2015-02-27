package org.zarroboogs.weibo.adapter;

import java.util.ArrayList;
import java.util.List;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.adapter.AbstractAppListAdapter.ViewHolder;
import org.zarroboogs.weibo.bean.HotMblogBean;
import org.zarroboogs.weibo.support.asyncdrawable.IWeiciyuanDrawable;
import org.zarroboogs.weibo.support.utils.ViewUtility;
import org.zarroboogs.weibo.widget.TimeLineAvatarImageView;
import org.zarroboogs.weibo.widget.TimeLineImageView;
import org.zarroboogs.weibo.widget.TimeTextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HotWeiboAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private List<HotMblogBean> list = new ArrayList<HotMblogBean>();

	public HotWeiboAdapter(Context context) {
		super();
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.hotweibo_item_layout, null);
			holder = buildHolder(convertView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// mImageLoader.displayImage("file://" +
		// SendImgData.getInstance().getSendImgs().get(position),
		// holder.mImageView , options);
		holder.username.setText(list.get(position).getUser().getScreen_name());

		return convertView;
	}

	public static class ViewHolder {
		TextView username;
	}

	private ViewHolder buildHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.username = ViewUtility.findViewById(convertView, R.id.username);

		return holder;
	}

	public void addNewData(List<HotMblogBean> list) {
		this.list.addAll(list);
		notifyDataSetChanged();
	}
}
