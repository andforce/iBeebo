
package org.zarroboogs.weibo.support.gallery;

import org.zarroboogs.utils.ImageLoader;
import org.zarroboogs.weibo.MyAnimationListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.support.utils.AnimationUtility;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.ClipImageView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class GifPictureFragment extends Fragment {

    private static final int ANIMATION_DURATION = 300;

    private PhotoView gifImageView;

    public static GifPictureFragment newInstance(String path, AnimationRect rect, boolean animationIn) {
        GifPictureFragment fragment = new GifPictureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putParcelable("rect", rect);
        bundle.putBoolean("animationIn", animationIn);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_gif_layout, container, false);

        gifImageView = (PhotoView) view.findViewById(R.id.gif);

        if (SettingUtils.allowClickToCloseGallery()) {
            gifImageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    getActivity().onBackPressed();
                }
            });
        }

        LongClickListener longClickListener = ((BigPicContainerFragment) getParentFragment()).getLongClickListener();
        gifImageView.setOnLongClickListener(longClickListener);

        String path = getArguments().getString("path");
        boolean animateIn = getArguments().getBoolean("animationIn");
        final AnimationRect rect = getArguments().getParcelable("rect");

        File gifFile = new File(path);
        try {
            GifDrawable gifFromFile = new GifDrawable(gifFile);
            gifImageView.setImageDrawable(gifFromFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        final ClipImageView photoView = (ClipImageView) view.findViewById(R.id.cover);

        ImageLoader.load(this, path, photoView);

//        Bitmap bitmap = ImageUtility.decodeBitmapFromSDCard(path, IMAGEVIEW_SOFT_LAYER_MAX_WIDTH,
//                IMAGEVIEW_SOFT_LAYER_MAX_HEIGHT);
//
//        photoView.setImageBitmap(bitmap);

        if (!animateIn) {
            photoView.setVisibility(View.INVISIBLE);
            return view;
        }

        gifImageView.setVisibility(View.INVISIBLE);

        final Runnable endAction = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getArguments();
                bundle.putBoolean("animationIn", false);
                photoView.setVisibility(View.INVISIBLE);
                gifImageView.setVisibility(View.VISIBLE);
            }
        };

        photoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                if (rect == null) {
                    photoView.getViewTreeObserver().removeOnPreDrawListener(this);
                    endAction.run();
                    return true;
                }

                final Rect startBounds = new Rect(rect.scaledBitmapRect);
                final Rect finalBounds = AnimationUtility.getBitmapRectFromImageView(photoView);

                if (finalBounds == null) {
                    photoView.getViewTreeObserver().removeOnPreDrawListener(this);
                    endAction.run();
                    return true;
                }

                float startScale = (float) finalBounds.width() / startBounds.width();

                if (startScale * startBounds.height() > finalBounds.height()) {
                    startScale = (float) finalBounds.height() / startBounds.height();
                }

                int oriBitmapScaledWidth = (int) (finalBounds.width() / startScale);
                int oriBitmapScaledHeight = (int) (finalBounds.height() / startScale);

                int thumbnailAndOriDeltaRightSize = Math.abs(rect.scaledBitmapRect.width() - oriBitmapScaledWidth);
                int thumbnailAndOriDeltaBottomSize = Math.abs(rect.scaledBitmapRect.height() - oriBitmapScaledHeight);

                float thumbnailAndOriDeltaWidth = (float) thumbnailAndOriDeltaRightSize / (float) oriBitmapScaledWidth;
                float thumbnailAndOriDeltaHeight = (float) thumbnailAndOriDeltaBottomSize / (float) oriBitmapScaledHeight;

                int deltaTop = startBounds.top - finalBounds.top;
                int deltaLeft = startBounds.left - finalBounds.left;

                photoView.setPivotY((photoView.getHeight() - finalBounds.height()) / 2);
                photoView.setPivotX((photoView.getWidth() - finalBounds.width()) / 2);

                photoView.setScaleX(1 / startScale);
                photoView.setScaleY(1 / startScale);

                photoView.setTranslationX(deltaLeft);
                photoView.setTranslationY(deltaTop);

                photoView.animate().translationY(0).translationX(0).scaleY(1).scaleX(1).setDuration(ANIMATION_DURATION)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setListener(new MyAnimationListener(endAction));

                if (rect.type == AnimationRect.TYPE_EXTEND_V || rect.type == AnimationRect.TYPE_EXTEND_H) {

                    AnimatorSet animationSet = new AnimatorSet();
                    animationSet.setDuration(ANIMATION_DURATION);
                    animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

                    animationSet.playTogether(ObjectAnimator.ofFloat(photoView, "clipBottom", thumbnailAndOriDeltaHeight, 0));
                    animationSet.start();

                } else {

                    AnimatorSet animationSet = new AnimatorSet();
                    animationSet.setDuration(ANIMATION_DURATION);
                    animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

                    float clipRectH = ((oriBitmapScaledWidth - oriBitmapScaledWidth * thumbnailAndOriDeltaWidth - rect.widgetWidth) / 2)
                            / (float) oriBitmapScaledWidth;
                    float clipRectV = ((oriBitmapScaledHeight - oriBitmapScaledHeight * thumbnailAndOriDeltaHeight - rect.widgetHeight) / 2)
                            / (float) oriBitmapScaledHeight;

                    animationSet.playTogether(ObjectAnimator.ofFloat(photoView, "clipHorizontal", clipRectH, 0));
                    animationSet.playTogether(ObjectAnimator.ofFloat(photoView, "clipVertical", clipRectV, 0));

                    animationSet.playTogether(ObjectAnimator.ofFloat(photoView, "clipBottom", thumbnailAndOriDeltaHeight, 0));
                    animationSet.playTogether(ObjectAnimator.ofFloat(photoView, "clipRight", thumbnailAndOriDeltaWidth, 0));

                    animationSet.start();

                }

                photoView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        return view;
    }

    public void animationExit(ObjectAnimator backgroundAnimator) {

        if (Math.abs(gifImageView.getScale() - 1.0f) > 0.1f) {
            gifImageView.setScale(1, true);
            return;
        }

        getActivity().overridePendingTransition(0, 0);
        animateClose(backgroundAnimator);

    }

    private void animateClose(ObjectAnimator backgroundAnimator) {

        gifImageView.setVisibility(View.INVISIBLE);

        final ClipImageView photoView = (ClipImageView) getView().findViewById(R.id.cover);

        photoView.setVisibility(View.VISIBLE);

        AnimationRect rect = getArguments().getParcelable("rect");

        if (rect == null) {
            photoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        final Rect startBounds = rect.scaledBitmapRect;
        final Rect finalBounds = AnimationUtility.getBitmapRectFromImageView(photoView);

        if (finalBounds == null) {
            photoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        if (Utility.isDevicePort() != rect.isScreenPortrait) {
            photoView.animate().alpha(0);
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

        int oriBitmapScaledWidth = (int) (finalBounds.width() * startScale);
        int oriBitmapScaledHeight = (int) (finalBounds.height() * startScale);

        // sina server may cut thumbnail's right or bottom
        int thumbnailAndOriDeltaRightSize = Math.abs(rect.scaledBitmapRect.width() - oriBitmapScaledWidth);
        int thumbnailAndOriDeltaBottomSize = Math.abs(rect.scaledBitmapRect.height() - oriBitmapScaledHeight);

        float serverClipThumbnailRightSizePercent = (float) thumbnailAndOriDeltaRightSize / (float) oriBitmapScaledWidth;
        float serverClipThumbnailBottomSizePercent = (float) thumbnailAndOriDeltaBottomSize / (float) oriBitmapScaledHeight;

        int deltaTop = startBounds.top - finalBounds.top;
        int deltaLeft = startBounds.left - finalBounds.left;

        photoView.setPivotY((photoView.getHeight() - finalBounds.height()) / 2);
        photoView.setPivotX((photoView.getWidth() - finalBounds.width()) / 2);

        photoView.animate().translationX(deltaLeft).translationY(deltaTop).scaleY(startScaleFinal).scaleX(startScaleFinal)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new MyAnimationListener(new Runnable() {
                    @Override
                    public void run() {

                        photoView.animate().alpha(0.0f).setDuration(200);

                    }
                }));

        if (rect.type == AnimationRect.TYPE_EXTEND_V || rect.type == AnimationRect.TYPE_EXTEND_H) {
            AnimatorSet animationSet = new AnimatorSet();
            animationSet.setDuration(ANIMATION_DURATION);
            animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

            animationSet.playTogether(backgroundAnimator);
            animationSet.playTogether(ObjectAnimator.ofFloat(photoView, "clipBottom", 0,
                    serverClipThumbnailBottomSizePercent));
            animationSet.start();
        } else {

            AnimatorSet animationSet = new AnimatorSet();
            animationSet.setDuration(ANIMATION_DURATION);
            animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

            animationSet.playTogether(backgroundAnimator);

            float clipRectH = ((oriBitmapScaledWidth - oriBitmapScaledWidth * serverClipThumbnailRightSizePercent - rect.widgetWidth) / 2)
                    / (float) oriBitmapScaledWidth;
            float clipRectV = ((oriBitmapScaledHeight - oriBitmapScaledHeight * serverClipThumbnailBottomSizePercent - rect.widgetHeight) / 2)
                    / (float) oriBitmapScaledHeight;

            animationSet.playTogether(ObjectAnimator.ofFloat(photoView, "clipHorizontal", 0, clipRectH));
            animationSet.playTogether(ObjectAnimator.ofFloat(photoView, "clipVertical", 0, clipRectV));

            animationSet.playTogether(ObjectAnimator.ofFloat(photoView, "clipBottom", 0,
                    serverClipThumbnailBottomSizePercent));
            animationSet
                    .playTogether(ObjectAnimator.ofFloat(photoView, "clipRight", 0, serverClipThumbnailRightSizePercent));

            animationSet.start();

        }
    }

}
