package org.zarroboogs.weibo.widget.galleryview;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

public class WeiboGifView extends View {
    final static private String TAG = "GifView";
    final static private int FILE_MAX_SIZE = 2 * 1024 * 1024;
    final static private float M_SCALE_RATIO = 1.0f;
    final static private int M_GIF_WIDTH = 190;

    private int mGifw = 0;
    private int mGifh = 0;
    private int mVieww = 0;
    private int mViewh = 0;
    private Movie mMovie = null;
    private long mMovieStart = 0;
    private long mMovieDuration = 0;

    public WeiboGifView(Context context) {
        super(context);
    }

    public WeiboGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeiboGifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private byte[] streamToBytes(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        byte[] buffer = new byte[FILE_MAX_SIZE];
        int len;
        while ((len = is.read(buffer)) != -1) {
            bytestream.write(buffer, 0, len);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    public boolean setGifPath(String path) {
        if (null == path) {
            return false;
        }

        File gifFile = new File(path);

        if (!gifFile.exists()) {
            return false;
        }
        InputStream is = null;
        boolean bRet = false;
        try {
            // is = this.getContext().getContentResolver().openInputStream(uri);

            is = new FileInputStream(path);

            // byte[] array = streamToBytes(is);

            byte[] array = null;
            try {
                array = streamToBytes(is);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mMovie = Movie.decodeByteArray(array, 0, array.length);
            bRet = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (null != mMovie) {
            mMovieDuration = mMovie.duration();

            if (0 == mMovieDuration) {
                mMovieDuration = 1000;
            }
            mGifw = mMovie.width();
            mGifh = mMovie.height();
        }

        return bRet;
    }

    public boolean setUri(Uri uri) {
        if (null == uri) {
            return false;
        }

        InputStream is = null;
        boolean bRet = false;
        try {
            is = this.getContext().getContentResolver().openInputStream(uri);
            // byte[] array = streamToBytes(is);

            byte[] array = null;
            try {
                array = streamToBytes(is);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mMovie = Movie.decodeByteArray(array, 0, array.length);
            bRet = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (null != mMovie) {
            mMovieDuration = mMovie.duration();

            if (0 == mMovieDuration) {
                mMovieDuration = 1000;
            }
            mGifw = mMovie.width();
            mGifh = mMovie.height();
        }

        return bRet;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mVieww = w;
        mViewh = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == mMovie) {
            return;
        }

        long curTime = android.os.SystemClock.uptimeMillis();

        if (mMovieStart == 0) {
            mMovieStart = curTime;
        }

        int relTime = (int) ((curTime - mMovieStart) % mMovieDuration);
        mMovie.setTime(relTime);

        float scaleRate = M_SCALE_RATIO;
        float wRate = 0.0f;
        float hRate = 0.0f;

        if (mGifw > M_GIF_WIDTH) {
            scaleRate = M_SCALE_RATIO;
            wRate = mVieww * M_SCALE_RATIO / mGifw;
            hRate = mViewh * M_SCALE_RATIO / mGifh;
        } else {
            scaleRate = 0.8f;
            wRate = mVieww * scaleRate / mGifw;
            hRate = mViewh * scaleRate / mGifh;
        }

        if (wRate >= hRate) {
            scaleRate = hRate;
        } else {
            scaleRate = wRate;
        }

        float scalew = scaleRate * mGifw;
        float scaleh = scaleRate * mGifh;

        float leftPt = (mVieww * M_SCALE_RATIO - scalew) / 2.0f;
        float topPt = (mViewh * M_SCALE_RATIO - scaleh) / 2.0f;

        canvas.translate(leftPt, topPt);
        canvas.scale(scaleRate, scaleRate);
        mMovie.draw(canvas, 0, 0);
        invalidate();
    }
}
