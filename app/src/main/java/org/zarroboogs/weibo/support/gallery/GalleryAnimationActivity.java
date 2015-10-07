
package org.zarroboogs.weibo.support.gallery;

import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.hot.hean.HotMblogBean;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.support.utils.AnimationUtility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class GalleryAnimationActivity extends FragmentActivity {

	public static final String TAG = "GalleryAnimationActivity_";

    private ArrayList<AnimationRect> rectList;

    private ArrayList<String> urls = new ArrayList<>();

    private ViewPager mViewPager;

    private TextView position;

    private int initPosition;

    private View background;

    private ColorDrawable backgroundColor;

    public static Intent newIntent(MessageBean msg, ArrayList<AnimationRect> rectList, int initPosition, boolean... isFromHotWeibo) {
        Intent intent = new Intent(BeeboApplication.getInstance(), GalleryAnimationActivity.class);
        intent.putExtra("msg", msg);
        intent.putExtra("rect", rectList);
        intent.putExtra("position", initPosition);
        if (isFromHotWeibo != null && isFromHotWeibo.length > 0) {
        	intent.putExtra("isFromHotWeibo", isFromHotWeibo[0]);
		}else {
	        intent.putExtra("isFromHotWeibo", false);
		}

        return intent;
    }
    
    public static Intent newIntent(HotMblogBean msg, ArrayList<AnimationRect> rectList, int initPosition) {
        Intent intent = new Intent(BeeboApplication.getInstance(), GalleryAnimationActivity.class);
        intent.putExtra("msg", msg);
        intent.putExtra("rect", rectList);
        intent.putExtra("position", initPosition);
        return intent;
    }
    
    
    public static Intent newIntent(ArrayList<String> lPics, ArrayList<AnimationRect> rectList, int initPosition) {
        Intent intent = new Intent(BeeboApplication.getInstance(), GalleryAnimationActivity.class);
        intent.putStringArrayListExtra("pics", lPics);
        intent.putExtra("position", initPosition);
        intent.putExtra("rect", rectList);
        intent.putExtra("hot_model", true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏


        setContentView(R.layout.galleryactivity_animation_layout);

        boolean isModel = getIntent().getBooleanExtra("hot_model", false);
        
        if (isModel) {

            ArrayList<String> tmp = getIntent().getStringArrayListExtra("pics");
            urls.addAll(tmp);
		}else {
	        MessageBean msg = getIntent().getParcelableExtra("msg");
	        ArrayList<String> tmp = msg.getHotThumbnailPicUrls();
	        if (tmp.isEmpty()) {
				tmp = msg.getThumbnailPicUrls();
			}
	        for (int i = 0; i < tmp.size(); i++) {
	            urls.add(tmp.get(i).replace("thumbnail", "large").replace("webp180", "large"));
	        }
		}
        rectList = getIntent().getParcelableArrayListExtra("rect");

        boolean disableHardwareLayerType = false;

        for (String url : urls) {
            if (url.contains(".gif")) {
                disableHardwareLayerType = true;
                break;
            }
        }

        position = (TextView) findViewById(R.id.position);
        initPosition = getIntent().getIntExtra("position", 0);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mViewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));

        final boolean finalDisableHardwareLayerType = disableHardwareLayerType;

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                GalleryAnimationActivity.this.position.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int scrollState) {
                if (scrollState != ViewPager.SCROLL_STATE_IDLE && finalDisableHardwareLayerType) {
                    final int childCount = mViewPager.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = mViewPager.getChildAt(i);
                        if (child.getLayerType() != View.LAYER_TYPE_NONE) {
                            child.setLayerType(View.LAYER_TYPE_NONE, null);
                        }
                    }
                }
            }
        });
        mViewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        TextView sum = (TextView) findViewById(R.id.sum);
        sum.setText(String.valueOf(urls.size()));

        background = AnimationUtility.getAppContentView(this);

        if (savedInstanceState != null) {
            showBackgroundImmediately();
        }

        toggleHideyBar();
    }

    public void toggleHideyBar() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int newUiOptions = getWindow().getDecorView().getSystemUiVisibility();
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    private HashMap<Integer, BigPicContainerFragment> fragmentMap = new HashMap<>();

    private boolean alreadyAnimateIn = false;

    private class ImagePagerAdapter extends FragmentPagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            BigPicContainerFragment fragment = fragmentMap.get(position);
            if (fragment == null) {

                boolean animateIn = (initPosition == position) && !alreadyAnimateIn;
                fragment = BigPicContainerFragment.newInstance(urls.get(position), rectList.get(position), animateIn,
                        initPosition == position);
                alreadyAnimateIn = true;
                fragmentMap.put(position, fragment);
            }

            return fragment;
        }

        // when activity is recycled, ViewPager will reuse fragment by theirs
        // name, so
        // getItem wont be called, but we need fragmentMap to animate close
        // operation
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                fragmentMap.put(position, (BigPicContainerFragment) object);
            }
        }

        @Override
        public int getCount() {
            return urls.size();
        }
    }

    public void showBackgroundImmediately() {
        if (background.getBackground() == null) {
            backgroundColor = new ColorDrawable(Color.BLACK);
            background.setBackgroundDrawable(backgroundColor);
        }
    }

    public ObjectAnimator showBackgroundAnimate() {
        backgroundColor = new ColorDrawable(Color.BLACK);
        background.setBackgroundDrawable(backgroundColor);
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(backgroundColor, "alpha", 0, 255);
        bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                background.setBackgroundDrawable(backgroundColor);
            }
        });
        return bgAnim;
    }

    @Override
    public void onBackPressed() {

        BigPicContainerFragment fragment = fragmentMap.get(mViewPager.getCurrentItem());
        if (fragment != null && fragment.canAnimateCloseActivity()) {
            backgroundColor = new ColorDrawable(Color.BLACK);
            ObjectAnimator bgAnim = ObjectAnimator.ofInt(backgroundColor, "alpha", 0);
            bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    background.setBackgroundDrawable(backgroundColor);
                }
            });
            bgAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    GalleryAnimationActivity.super.finish();
                    overridePendingTransition(-1, -1);
                }
            });
            fragment.animationExit(bgAnim);
        } else {
            super.onBackPressed();
        }
    }

}
