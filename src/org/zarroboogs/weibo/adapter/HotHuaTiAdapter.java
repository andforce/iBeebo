package org.zarroboogs.weibo.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.hot.hean.HotHuaTiCardGroupBean;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HotHuaTiAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private List<HotHuaTiCardGroupBean> list = new ArrayList<HotHuaTiCardGroupBean>();

	public HotHuaTiAdapter(Context context) {
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
			convertView = mInflater.inflate(R.layout.hot_huati_item_layout, null);
			holder = buildHolder(convertView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HotHuaTiCardGroupBean blog = list.get(position);

		holder.title.setText(blog.getTitle_sub());
		holder.descript.setText(blog.getDesc1());
		holder.readnumber.setText(blog.getDesc2());
		mImageLoader.displayImage(blog.getPic(), holder.pic);
		
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

    
    
    public void addNewDataWithOutRemberPos(List<HotHuaTiCardGroupBean> newValue) {

        if (newValue == null || newValue.size() == 0) {
            return;
        }

        this.list.addAll( newValue);
        }
    public void addNewData(List<HotHuaTiCardGroupBean> newValue) {

        if (newValue == null || newValue.size() == 0) {
            return;
        }

        this.list.addAll(0, newValue);
        
        // remove duplicate null flag, [x,y,null,null,z....]
 
        ListIterator<HotHuaTiCardGroupBean> listIterator = this.list.listIterator();

        boolean isLastItemNull = false;
        while (listIterator.hasNext()) {
        	HotHuaTiCardGroupBean msg = listIterator.next();
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
