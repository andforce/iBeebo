package org.zarroboogs.weibo.adapter;

import java.util.ArrayList;
import java.util.List;

import org.zarroboogs.utils.ImageLoader;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.hot.bean.model.detail.Pics;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class HotModelDetailAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Pics> mPics = new ArrayList<>();


    public ArrayList<String> getPicStrings() {
        ArrayList<String> pics = new ArrayList<String>();
        for (Pics p : mPics) {
            pics.add(p.getPic_big());
        }
        return pics;

    }

    public HotModelDetailAdapter(Context context) {
        super();
        // TODO Auto-generated constructor stub
        mInflater = LayoutInflater.from(context);
        mContext = context;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mPics.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mPics.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.hot_model_detail_item_layout, null);
            holder = buildHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String pic = mPics.get(position).getPic_small();

        ImageLoader.load(mContext, pic, holder.modelDetail);
        return convertView;
    }


    public static class ViewHolder {
        ImageView modelDetail;
    }

    private ViewHolder buildHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.modelDetail = ViewUtility.findViewById(convertView, R.id.modelDetail);
        return holder;
    }


    public void addNewData(List<Pics> newValue) {

        if (newValue == null || newValue.size() == 0) {
            return;
        }

        this.mPics.addAll(newValue);
        notifyDataSetChanged();

    }
}
