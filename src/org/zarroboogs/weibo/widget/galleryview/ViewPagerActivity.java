package org.zarroboogs.weibo.widget.galleryview;



import java.io.File;
import java.util.List;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.selectphoto.SendImgData;
import org.zarroboogs.weibo.widget.galleryview.PhotoViewAttacher.OnViewTapListener;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.MenuItem;
import android.view.View;

public class ViewPagerActivity extends AbstractViewPagerActivity implements OnViewTapListener {

    private ViewPager mViewPager;
    public static final int VIEW_TAP = 0x10000;
    public static final int DIS_MISS_DIALOG = 0x10001;
    public static final int DOWNLOAD_ERROR = 0x10002;
    public static final int DISABLE_DOWNLOAD_BTN = 0x11000;
    public static final int ENABLE_DOWNLOAD_BTN = 0x11001;

    public static final int NINE_GALLERY_REQUEST_CODE = 0x0100;

    public static final String IMAGE_NAME = "image_name";
    public static final String IMAGE_URL = "image_url";
    public static final String DATA_ITEM = "data_item";
    public static final String SELECTED_ID = "selected_id";

    public static final String ALL_IMAGE_URLS = "all_image_urls";// 多图微博所有图片的url
    public static int screenWidth;
    public static int screenHeight;

    private WeiboGifView mGifView;

    private WeiboGalleryPhotoViewAdapter mPhotoViewAdapter;

    private int mPicId = 0; // 选中哪张图了

    private List<String> mNinePics;

    
    public static final String IMG_LIST = "img_list";
    public static final String IMG_ID = "img_id";

    private Toolbar mToolbar;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mViewPager = new HackyViewPager(this);

        mNinePics = getIntent().getStringArrayListExtra(IMG_LIST);
        mPicId = getIntent().getIntExtra(IMG_ID, 0);

        setContentView(R.layout.weibo_single_gallery_activity);

        mToolbar = (Toolbar) findViewById(R.id.selectImgBar);
        
        mToolbar.setTitle(getResources().getString(R.string.pref_pic_category_title) + "-" + (mPicId + 1));
        
        
        mViewPager = (ViewPager) findViewById(R.id.weibo_nine_pic_gallery);

        mPhotoViewAdapter = new WeiboGalleryPhotoViewAdapter(getApplicationContext(), mViewPager);

        mGifView = (WeiboGifView) findViewById(R.id.weibo_wdy_gif);

//        mGifView.setOnClickListener(this);

        // 多图微博
        if (true) {
            mPhotoViewAdapter.setNinePics(mNinePics);
            addGalleryViewActionBar(mPhotoViewAdapter);
        }
        
        mToolbar.inflateMenu(R.menu.select_image_action_menu);
        
        mToolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				// TODO Auto-generated method stub
				if (arg0.getItemId() == R.id.change_image) {
					
				}else if(arg0.getItemId() == R.id.delete_image){
					if (mNinePics != null && !mNinePics.isEmpty()) {
						mNinePics.remove(mPicId);
						mPhotoViewAdapter.setNinePics(mNinePics);
						mPhotoViewAdapter.notifyDataSetChanged();
						
						SendImgData.getInstance().removeSendImg(mNinePics.get(mPicId));
					}
					if (mNinePics != null && mNinePics.isEmpty()) {
						mToolbar.setTitle(getResources().getString(R.string.pref_pic_category_title) + "-0");
					}
				}
				return false;
			}
		});
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void addGalleryViewActionBar(WeiboGalleryPhotoViewAdapter mSamplePagerAdapter) {
        // RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.top_relativelayout);

        mViewPager.setAdapter(mSamplePagerAdapter);
        // mLayout.addView(mViewPager, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        // mViewPager.bringChildToFront(mViewPager.getChildAt(selected_id));
        mViewPager.setCurrentItem(mPicId);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                // mImageCounter.setText("" + (arg0 + 1) + "/100");
                mPicId = arg0;
                mToolbar.setTitle(getResources().getString(R.string.pref_pic_category_title) + "-" + (arg0 + 1));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void onViewTap(View view, float x, float y) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhotoViewAdapter.cancleAllTasks();
        mPhotoViewAdapter.recycleAllBitmaps();
    }

    @Override
    public File getLargeFile(String filePath) {
        // TODO Auto-generated method stub
        File mFile = new File(filePath);
        return mFile;
    }

    @Override
    public File getMiddleFile(String filePath) {
        // TODO Auto-generated method stub
        File mFile = new File(filePath);
        return mFile;
    }

    @Override
    public File getSmallFile(String filePath) {
        // TODO Auto-generated method stub
        File mFile = new File(filePath);
        return mFile;
    }

    @Override
    public String getLargeFilePath() {
        return null;
    }

    @Override
    public String getMiddleFilePath() {
        return null;
    }

    @Override
    public String getSmallFilePath() {
        return null;
    }

}
