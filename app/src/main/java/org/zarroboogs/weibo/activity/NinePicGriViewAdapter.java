package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.ImageLoader;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.selectphoto.SendImgData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class NinePicGriViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    public NinePicGriViewAdapter(Context context) {
        super();
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
            convertView = mInflater.inflate(R.layout.nine_pic_gridview_item, null);
            holder = new ViewHolder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.oneNineImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.load(mContext, "file://" + SendImgData.getInstance().getSendImgs().get(position), holder.mImageView);
        return convertView;
    }

    class ViewHolder {
        ImageView mImageView;
    }


}
