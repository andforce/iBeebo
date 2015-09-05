package org.zarroboogs.weibo.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.zarroboogs.utils.ImageLoader;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.hot.bean.model.HotModelCardGroup;
import org.zarroboogs.weibo.hot.bean.model.HotModelCards;
import org.zarroboogs.weibo.hot.bean.model.Pics;
import org.zarroboogs.weibo.support.gallery.GalleryAnimationActivity;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class HotModelAdapter extends BaseAdapter{

	private LayoutInflater mInflater;

	private Context mContext;
	private List<HotModelCards> list = new ArrayList<>();


	public interface OnModelDetailonClickListener{
		void onModelDetailClick(HotModelCards cards);
	}

	private OnModelDetailonClickListener listener;
	
	public void setOnModelDetailonClickListener(OnModelDetailonClickListener listener){
		this.listener = listener;
	}
	
	public HotModelAdapter(Context context) {
		super();
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);

		this.mContext = context;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
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
		final List<Pics> mPics = group.getPics();

		ImageLoader.load(mContext, mPics.get(0).getPic_small(), holder.modelPic000);
		ImageLoader.load(mContext, mPics.get(1).getPic_small(), holder.modelPic001);
		ImageLoader.load(mContext, mPics.get(2).getPic_small(), holder.modelPic002);
		ImageLoader.load(mContext, mPics.get(3).getPic_small(), holder.modelPic003);
		ImageLoader.load(mContext, mPics.get(4).getPic_small(), holder.modelPic004);
		ImageLoader.load(mContext, mPics.get(5).getPic_small(), holder.modelPic005);

		holder.showModelDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listener != null) {
					listener.onModelDetailClick(list.get(position));
				}
			}
		});
		
		final ViewHolder tmpholder = holder;
		holder.modelPic000.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = 0;
				showPic(tmpholder, mPics, pos);
			}
		});
		holder.modelPic001.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = 1;
				showPic(tmpholder,mPics, pos);
			}
		});
		holder.modelPic002.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = 2;
				showPic(tmpholder,mPics, pos);
			}
		});
		holder.modelPic003.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = 3;
				showPic(tmpholder,mPics, pos);
			}
		});
		holder.modelPic004.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = 4;
				showPic(tmpholder,mPics, pos);
			}
		});
		holder.modelPic005.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = 5;
				showPic(tmpholder,mPics, pos);
			}
		});
		return convertView;
	}

	private void showPic(ViewHolder holder ,final List<Pics> mPics, int pos) {
		ArrayList<String> pics  = new ArrayList<String>();
		for (Pics p : mPics) {
			pics.add(p.getPic_big());
		}
		
		ArrayList<AnimationRect> animationRectArrayList = new ArrayList<AnimationRect>();
		AnimationRect rect00 = AnimationRect.buildFromImageView(holder.modelPic000);
        animationRectArrayList.add(rect00);
        
		AnimationRect rect01 = AnimationRect.buildFromImageView(holder.modelPic001);
        animationRectArrayList.add(rect01);
        
		AnimationRect rect02 = AnimationRect.buildFromImageView(holder.modelPic002);
        animationRectArrayList.add(rect02);
        
		AnimationRect rect03 = AnimationRect.buildFromImageView(holder.modelPic003);
        animationRectArrayList.add(rect03);
        
		AnimationRect rect04 = AnimationRect.buildFromImageView(holder.modelPic004);
        animationRectArrayList.add(rect04);
        
		AnimationRect rect05 = AnimationRect.buildFromImageView(holder.modelPic005);
        animationRectArrayList.add(rect05);
        
        
		Intent intent = GalleryAnimationActivity.newIntent(pics, animationRectArrayList,pos);
		mContext.startActivity(intent);
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
