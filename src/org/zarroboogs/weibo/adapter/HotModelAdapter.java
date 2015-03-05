package org.zarroboogs.weibo.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.hot.bean.model.HotModel;
import org.zarroboogs.weibo.hot.bean.model.HotModelCardGroup;
import org.zarroboogs.weibo.hot.bean.model.HotModelCards;
import org.zarroboogs.weibo.hot.bean.model.Pics;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HotModelAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private List<HotModelCards> list = new ArrayList<HotModelCards>();

	public HotModelAdapter(Context context) {
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
			convertView = mInflater.inflate(R.layout.hot_model_item_layout, null);
			holder = buildHolder(convertView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HotModelCards blog = list.get(position);
		HotModelCardGroup group = blog.getCard_group().get(0);
		List<Pics> mPics = group.getPics();
		mImageLoader.displayImage(mPics.get(0).getPic_small(), holder.modelPic000);
		mImageLoader.displayImage(mPics.get(1).getPic_small(), holder.modelPic001);
		mImageLoader.displayImage(mPics.get(2).getPic_small(), holder.modelPic002);
		mImageLoader.displayImage(mPics.get(3).getPic_small(), holder.modelPic003);
		mImageLoader.displayImage(mPics.get(4).getPic_small(), holder.modelPic004);
		mImageLoader.displayImage(mPics.get(5).getPic_small(), holder.modelPic005);
		
		holder.showModelDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		return convertView;
	}


    public static class ViewHolder {
        ImageView modelPic000;
        ImageView modelPic001;
        ImageView modelPic002;
        ImageView modelPic003;
        ImageView modelPic004;
        ImageView modelPic005;
        
        Button showModelDetail;
        
    }

    private ViewHolder buildHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.modelPic000 = ViewUtility.findViewById(convertView, R.id.modle000);
        
        holder.modelPic001 = ViewUtility.findViewById(convertView, R.id.modle001);
        holder.modelPic002 = ViewUtility.findViewById(convertView, R.id.modle002);
        holder.modelPic003 = ViewUtility.findViewById(convertView, R.id.modle003);
        holder.modelPic004 = ViewUtility.findViewById(convertView, R.id.modle004);
        holder.modelPic005 = ViewUtility.findViewById(convertView, R.id.modle005);
        
        holder.showModelDetail = ViewUtility.findViewById(convertView, R.id.showModelDetail);
        return holder;
    }

    
    
    public void addNewDataWithOutRemberPos(List<HotModelCards> newValue) {

        if (newValue == null || newValue.size() == 0) {
            return;
        }

        this.list.addAll( newValue);
        }
    public void addNewData(List<HotModelCards> newValue) {

        if (newValue == null || newValue.size() == 0) {
            return;
        }

        this.list.addAll(0, newValue);
        
        // remove duplicate null flag, [x,y,null,null,z....]
 
        ListIterator<HotModelCards> listIterator = this.list.listIterator();

        boolean isLastItemNull = false;
        while (listIterator.hasNext()) {
        	HotModelCards msg = listIterator.next();
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
