package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.selectphoto.SendImgData;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class NinePicGriViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    
	public NinePicGriViewAdapter(Context context) {
		super();
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return SendImgData.getInstance().getSendImgs().size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return SendImgData.getInstance().getSendImgs().get(position);
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
			convertView = mInflater.inflate(R.layout.nine_pic_gridview_item,null);
			holder = new ViewHolder();
			holder.mImageView = (ImageView) convertView.findViewById(R.id.oneNineImageView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		mImageLoader.displayImage("file://" + SendImgData.getInstance().getSendImgs().get(position), holder.mImageView	, options);
		return convertView;
	}

	class ViewHolder {
		ImageView mImageView;
	}
	
	
}
