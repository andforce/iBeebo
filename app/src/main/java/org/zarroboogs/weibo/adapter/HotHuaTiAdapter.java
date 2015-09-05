package org.zarroboogs.weibo.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.zarroboogs.utils.ImageLoader;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.hot.bean.huati.HotHuaTiCardGroup;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HotHuaTiAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private List<HotHuaTiCardGroup> list = new ArrayList<HotHuaTiCardGroup>();

    private Context mContext;

	public HotHuaTiAdapter(Context context) {
		super();
		mInflater = LayoutInflater.from(context);
        mContext = context;
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
			convertView = mInflater.inflate(R.layout.hot_huati_item_layout, null);
			holder = buildHolder(convertView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HotHuaTiCardGroup blog = list.get(position);

		holder.title.setText(blog.getTitle_sub());
		holder.descript.setText(blog.getDesc1());
		holder.readnumber.setText(blog.getDesc2());

		ImageLoader.load(mContext, blog.getPic(), holder.pic);

		return convertView;
	}


    public static class ViewHolder {
        TextView title;
        TextView descript;
        TextView readnumber;
        ImageView pic;
    }

    private ViewHolder buildHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.pic = ViewUtility.findViewById(convertView, R.id.huatiPic);
        
        holder.title = ViewUtility.findViewById(convertView, R.id.title);
        holder.descript = ViewUtility.findViewById(convertView, R.id.descript);
 
        holder.readnumber = ViewUtility.findViewById(convertView, R.id.readNumber);
        
        return holder;
    }

    
    
    public void addNewData(List<HotHuaTiCardGroup> newValue) {

        if (newValue == null || newValue.size() == 0) {
            return;
        }

        this.list.addAll(0, newValue);
        
        // remove duplicate null flag, [x,y,null,null,z....]
 
        ListIterator<HotHuaTiCardGroup> listIterator = this.list.listIterator();

        boolean isLastItemNull = false;
        while (listIterator.hasNext()) {
        	HotHuaTiCardGroup msg = listIterator.next();
            if (msg == null) {
                if (isLastItemNull) {
                    listIterator.remove();
                }
                isLastItemNull = true;
            } else {
                isLastItemNull = false;
            }
        }
    }
}
