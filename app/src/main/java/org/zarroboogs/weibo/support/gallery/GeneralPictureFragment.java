
package org.zarroboogs.weibo.support.gallery;

import org.zarroboogs.weibo.MyAnimationListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.support.utils.AnimationUtility;
import org.zarroboogs.weibo.support.utils.Utility;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class GeneralPictureFragment extends Fragment {

    private static final int IMAGEVIEW_SOFT_LAYER_MAX_WIDTH = 2000;

    private static final int IMAGEVIEW_SOFT_LAYER_MAX_HEIGHT = 3000;

    private PhotoView mPhotoView;

    public static final int ANIMATION_DURATION = 300;

    public static GeneralPictureFragment newInstance(String path, AnimationRect rect, boolean animationIn) {
        GeneralPictureFragment fragment = new GeneralPictureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putParcelable("rect", rect);
        bundle.putBoolean("animationIn", animationIn);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_general_layout, container, false);

        mPhotoView = (PhotoView) view.findViewById(R.id.animation);

        if (SettingUtils.allowClickToCloseGallery()) {

            mPhotoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    getActivity().onBackPressed();
                }
            });
        }

        LongClickListener longClickListener = ((BigPicContainerFragment) getParentFragment()).getLongClickListener();
        mPhotoView.setOnLongClickListener(longClickListener);

        final String path = getArguments().getString("path");
        boolean animateIn = getArguments().getBoolean("animationIn");
        final AnimationRect rect = getArguments().getParcelable("rect");

        if (!animateIn) {

            Glide.with(this).load(path).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    mPhotoView.setImageDrawable(resource);
                }
            });

            return view;
        }

        Glide.with(this).load(path).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                mPhotoView.setImageDrawable(resource);
            }
        });

        final Runnable endAction = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getArguments();
                bundle.putBoolean("animationIn", false);
            }
        };

        mPhotoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                if (rect == null) {
                    mPhotoView.getViewTreeObserver().removeOnPreDrawListener(this);
                    endAction.run();
                    return true;
                }

                final Rect startBounds = new Rect(rect.scaledBitmapRect);
                final Rect finalBounds = AnimationUtility.getBitmapRectFromImageView(mPhotoView);

                if (finalBounds == null) {
                    mPhotoView.getViewTreeObserver().removeOnPreDrawListener(this);
                    endAction.run();
                    return true;
                }

                float startScale = (float) finalBounds.width() / startBounds.width();

                if (startScale * startBounds.height() > finalBounds.height()) {
                    startScale = (float) finalBounds.height() / startBounds.height();
                }

                int deltaTop = startBounds.top - finalBounds.top;
                int deltaLeft = startBounds.left - finalBounds.left;

                mPhotoView.setPivotY((mPhotoView.getHeight() - finalBounds.height()) / 2);
                mPhotoView.setPivotX((mPhotoView.getWidth() - finalBounds.width()) / 2);

                mPhotoView.setScaleX(1 / startScale);
                mPhotoView.setScaleY(1 / startScale);

                mPhotoView.setTranslationX(deltaLeft);
                mPhotoView.setTranslationY(deltaTop);

                mPhotoView.animate().translationY(0).translationX(0).scaleY(1).scaleX(1).setDuration(ANIMATION_DURATION)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setListener(new MyAnimationListener(endAction));

                AnimatorSet animationSet = new AnimatorSet();
                animationSet.setDuration(ANIMATION_DURATION);
                animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

                animationSet.playTogether(ObjectAnimator.ofFloat(mPhotoView, "clipBottom",
                        AnimationRect.getClipBottom(rect, finalBounds), 0));
                animationSet.playTogether(ObjectAnimator.ofFloat(mPhotoView, "clipRight",
                        AnimationRect.getClipRight(rect, finalBounds), 0));
                animationSet.playTogether(ObjectAnimator.ofFloat(mPhotoView, "clipTop",
                        AnimationRect.getClipTop(rect, finalBounds), 0));
                animationSet.playTogether(ObjectAnimator.ofFloat(mPhotoView, "clipLeft",
                        AnimationRect.getClipLeft(rect, finalBounds), 0));

                animationSet.start();

                mPhotoView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        return view;
    }

    public void animationExit(ObjectAnimator backgroundAnimator) {

        if (Math.abs(mPhotoView.getScale() - 1.0f) > 0.1f) {
            mPhotoView.setScale(1, true);
            return;
        }

        getActivity().overridePendingTransition(0, 0);
        animateClose(backgroundAnimator);

    }

    private void animateClose(ObjectAnimator backgroundAnimator) {

        AnimationRect rect = getArguments().getParcelable("rect");

        if (rect == null) {
            mPhotoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        final Rect startBounds = rect.scaledBitmapRect;
        final Rect finalBounds = AnimationUtility.getBitmapRectFromImageView(mPhotoView);

        if (finalBounds == null) {
            mPhotoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        if (Utility.isDevicePort() != rect.isScreenPortrait) {
            mPhotoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();

        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
        }

        final float startScaleFinal = startScale;

        int deltaTop = startBounds.top - finalBounds.top;
        int deltaLeft = startBounds.left - finalBounds.left;

        mPhotoView.setPivotY((mPhotoView.getHeight() - finalBounds.height()) / 2);
        mPhotoView.setPivotX((mPhotoView.getWidth() - finalBounds.width()) / 2);

        mPhotoView.animate().translationX(deltaLeft).translationY(deltaTop).scaleY(startScaleFinal).scaleX(startScaleFinal)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new MyAnimationListener(new Runnable() {
                    @Override
                    public void run() {

                        mPhotoView.animate().alpha(0.0f).setDuration(200);

                    }
                }));

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animationSet.playTogether(backgroundAnimator);

        animationSet.playTogether(ObjectAnimator.ofFloat(mPhotoView, "clipBottom", 0,
                AnimationRect.getClipBottom(rect, finalBounds)));
        animationSet.playTogether(ObjectAnimator.ofFloat(mPhotoView, "clipRight", 0,
                AnimationRect.getClipRight(rect, finalBounds)));
        animationSet.playTogether(ObjectAnimator.ofFloat(mPhotoView, "clipTop", 0,
                AnimationRect.getClipTop(rect, finalBounds)));
        animationSet.playTogether(ObjectAnimator.ofFloat(mPhotoView, "clipLeft", 0,
                AnimationRect.getClipLeft(rect, finalBounds)));

        animationSet.start();

    }

}
