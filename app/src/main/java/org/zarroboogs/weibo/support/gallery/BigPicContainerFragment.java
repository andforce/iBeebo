
package org.zarroboogs.weibo.support.gallery;

import java.io.File;

import org.zarroboogs.utils.ImageUtility;
import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.utils.file.FileManager;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.support.asyncdrawable.TaskCache;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.widget.CircleProgressView;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BigPicContainerFragment extends Fragment {

    private TextView wait;
    private TextView error;
    private CircleProgressView progressView;
    private TextView mProgressNumber;


    public static BigPicContainerFragment newInstance(String url, AnimationRect rect, boolean animationIn,
            boolean firstOpenPage) {
        BigPicContainerFragment fragment = new BigPicContainerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putParcelable("rect", rect);
        bundle.putBoolean("animationIn", animationIn);
        bundle.putBoolean("firstOpenPage", firstOpenPage);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.big_pic_gallery_container_layout, container, false);
        progressView = (CircleProgressView) view.findViewById(R.id.loading);
        wait = (TextView) view.findViewById(R.id.wait);
        error = (TextView) view.findViewById(R.id.error);
        mProgressNumber = (TextView) view.findViewById(R.id.progress_bumber);

        Bundle bundle = getArguments();
        String url = bundle.getString("url");
        boolean animateIn = bundle.getBoolean("animationIn");
        bundle.putBoolean("animationIn", false);

        String path = FileManager.getFilePathFromUrl(url, FileLocationMethod.picture_large);

        if (ImageUtility.isThisBitmapCanRead(path) && TaskCache.isThisUrlTaskFinished(url)) {
            displayPicture(path, animateIn);
        } else {
            // show ThumbNail
            // http://ww2.sinaimg.cn/large/61e9ece0gw1el7e6yocgkj20c838w17d.jpg
            String thumbUrl = url != null ? url.replace("/large/", "/thumbnail/") : null;
            String thumb = FileManager.getFilePathFromUrl(thumbUrl, FileLocationMethod.picture_thumbnail);
            if (!TextUtils.isEmpty(thumb) && new File(thumb).exists()) {
                Log.d("WeiBoThumb_Nail", ": " + thumb + " url:" + url);
                displayPicture(thumb, animateIn);
            }

            GalleryAnimationActivity activity = (GalleryAnimationActivity) getActivity();
            activity.showBackgroundImmediately();
            progressView.setVisibility(View.VISIBLE);
            wait.setVisibility(View.VISIBLE);

            mProgressNumber.setVisibility(View.VISIBLE);

            TimeLineBitmapDownloader.getInstance().download(this, url, FileLocationMethod.picture_large, downloadCallback);

        }

        return view;
    }

    private TimeLineBitmapDownloader.DownloadCallback downloadCallback = new TimeLineBitmapDownloader.DownloadCallback() {

        @Override
        public void onSubmitJobButNotBegin() {
            super.onSubmitJobButNotBegin();
            wait.setVisibility(View.VISIBLE);
        }

        @Override
        public void onUpdate(int progress, int max) {
            super.onUpdate(progress, max);
            wait.setVisibility(View.INVISIBLE);
            progressView.setMax(max);
            progressView.setProgress(progress);

            mProgressNumber.setText((100 * progress / max) + "%");
        }

        @Override
        public void onComplete(final String localPath) {
            super.onComplete(localPath);
            CircleProgressView circleProgressView = progressView;
            circleProgressView.executeRunnableAfterAnimationFinish(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() == null) {
                        return;
                    }
                    progressView.setVisibility(View.INVISIBLE);
                    wait.setVisibility(View.INVISIBLE);

                    if (TextUtils.isEmpty(localPath)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText(getString(R.string.picture_cant_download_or_sd_cant_read));
                    } else if (!ImageUtility.isThisBitmapCanRead(localPath)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText(getString(R.string.download_finished_but_cant_read_picture_file));
                    } else {
                        error.setVisibility(View.INVISIBLE);
                        displayPicture(localPath, false);
                    }

                }
            });
        }

    };

    private void displayPicture(String path, boolean animateIn) {

        mProgressNumber.setVisibility(View.GONE);

        GalleryAnimationActivity activity = (GalleryAnimationActivity) getActivity();

        AnimationRect rect = getArguments().getParcelable("rect");
        boolean firstOpenPage = getArguments().getBoolean("firstOpenPage");

        if (firstOpenPage) {
            if (animateIn) {
                ObjectAnimator animator = activity.showBackgroundAnimate();
                animator.start();
            } else {
                activity.showBackgroundImmediately();
            }
            getArguments().putBoolean("firstOpenPage", false);
        }

        if (!ImageUtility.isThisBitmapTooLargeToRead(path)) {
            Fragment fragment;
            if (ImageUtility.isThisPictureGif(path)) {
                fragment = GifPictureFragment.newInstance(path, rect, animateIn);
            } else {
                fragment = GeneralPictureFragment.newInstance(path, rect, animateIn);
            }
            getChildFragmentManager().beginTransaction().replace(R.id.child, fragment).commitAllowingStateLoss();

        } else {
            LargePictureFragment fragment = LargePictureFragment.newInstance(path, animateIn);
            getChildFragmentManager().beginTransaction().replace(R.id.child, fragment).commitAllowingStateLoss();
        }

    }

    public void animationExit(ObjectAnimator backgroundAnimator) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.child);
        if (fragment instanceof GeneralPictureFragment) {
            GeneralPictureFragment child = (GeneralPictureFragment) fragment;
            child.animationExit(backgroundAnimator);
        } else if (fragment instanceof GifPictureFragment) {
            GifPictureFragment child = (GifPictureFragment) fragment;
            child.animationExit(backgroundAnimator);
        }
    }

    public boolean canAnimateCloseActivity() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.child);
        return fragment instanceof GeneralPictureFragment || fragment instanceof GifPictureFragment;
    }

    public LongClickListener getLongClickListener() {
        String url = getArguments().getString("url");
        String path = FileManager.getFilePathFromUrl(url, FileLocationMethod.picture_large);
        return new LongClickListener(getActivity(), url, path);
    }

}
