package org.zarroboogs.weibo.widget.galleryview;



import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zarroboogs.weibo.widget.galleryview.BitmapWorkerTask.OnCreateBitmapListener;
import org.zarroboogs.weibo.widget.galleryview.PhotoViewAttacher.OnViewTapListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class WeiboGalleryPhotoViewAdapter extends PagerAdapter {
    private static final boolean DEBUG = true;
    private static final String TAG = "WeiboGalleryPhotoViewAdapter";

    private Context mContext;
    private ViewPager mViewPager;
    private List<String> lis;
    private LruCache<String, Bitmap> mLruCaches;

    private Set<BitmapWorkerTask> mTasks;
    private static final int MAX_W = 1080;
    private static final int MAX_H = 1920 * 2;

    public WeiboGalleryPhotoViewAdapter(Context context, ViewPager viewPager) {
        super();
        this.mContext = context;
        this.mViewPager = viewPager;
        // get Cache SIze
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 6;
        // create LruCache
        mLruCaches = new LruCache<String, Bitmap>(cacheSize) {
            // must override sizeof()
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }

        };
        mTasks = new HashSet<BitmapWorkerTask>();
    }

    public void setNinePics(List<String> list) {
        lis = list;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        mViewPager.removeView((View) arg2);
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public int getCount() {
        if (lis == null || lis.size() < 1) {
            return 0;
        }
        return lis.size();
    }

    @Override
    public Object instantiateItem(View container, final int position) {
        // LayoutInflater layoutInflater = (LayoutInflater)
        // mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // popupView = layoutInflater.inflate(R.layout.sinaweibo_account_manager_popup, null);

        WeiboGalleryPhotoView photoView = new WeiboGalleryPhotoView(mContext);

        // photoView.setImageURI(Uri.parse(lis.get(arg1)));
        Bitmap mBitmap = createOrGetFromMenory(lis.get(position));
        if (mBitmap == null) {
            BitmapWorkerTask task = new BitmapWorkerTask(photoView, mContext);
            task.setBitmapScalWH(MAX_W, MAX_H);
            task.setOnCreateBitmapListener(new OnCreateBitmapListener() {
                @Override
                public void onCreateBitmap(Bitmap newBitmap) {
                    mLruCaches.put(lis.get(position), newBitmap);
                }
            });
            // add to set
            mTasks.add(task);
            task.execute(Uri.parse(lis.get(position)));
        } else {
            photoView.setImageBitmap(mBitmap);
        }

        // Now just add PhotoView to ViewPager and return it
        mViewPager.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        photoView.setOnViewTapListener(new OnViewTapListener() {

            @Override
            public void onViewTap(View view, float x, float y) {
                if (DEBUG) {
                }
            }
        });
        return photoView;
    }

    public Bitmap createOrGetFromMenory(String keyUri) {
        return mLruCaches.get(keyUri);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void cancleAllTasks() {
        for (BitmapWorkerTask bitmapWorkerTask : mTasks) {
            bitmapWorkerTask.cancel(false);
        }
    }

    public void recycleAllBitmaps() {
        for (String string : lis) {
            Bitmap tmp = mLruCaches.get(string);
            if (tmp != null && !tmp.isRecycled()) {
                tmp.recycle();
            }
            mLruCaches.remove(string);
        }
    }
}
