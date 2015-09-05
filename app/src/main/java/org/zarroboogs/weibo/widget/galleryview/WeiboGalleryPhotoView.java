package org.zarroboogs.weibo.widget.galleryview;



import org.zarroboogs.weibo.widget.galleryview.PhotoViewAttacher.OnMatrixChangedListener;
import org.zarroboogs.weibo.widget.galleryview.PhotoViewAttacher.OnPhotoTapListener;
import org.zarroboogs.weibo.widget.galleryview.PhotoViewAttacher.OnViewTapListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

public class WeiboGalleryPhotoView extends ImageView implements IPhotoView {

    private final PhotoViewAttacher mAttacher;

    private ScaleType mPendingScaleType;

    private OnClickListener mClickListener;

    private Context mContext;

    public WeiboGalleryPhotoView(Context context) {
        this(context, null);
    }

    public WeiboGalleryPhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public WeiboGalleryPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        super.setScaleType(ScaleType.MATRIX);
        mAttacher = new PhotoViewAttacher(this);
        mContext = context;

        if (null != mPendingScaleType) {
            setScaleType(mPendingScaleType);
            mPendingScaleType = null;
        }
    }

    @Override
    public boolean canZoom() {
        return mAttacher.canZoom();
    }

    @Override
    public RectF getDisplayRect() {
        return mAttacher.getDisplayRect();
    }

    @Override
    public float getMinScale() {
        return mAttacher.getMinScale();
    }

    @Override
    public float getMidScale() {
        return mAttacher.getMidScale();
    }

    @Override
    public float getMaxScale() {
        return mAttacher.getMaxScale();
    }

    @Override
    public float getScale() {
        return mAttacher.getScale();
    }

    @Override
    public ScaleType getScaleType() {
        return mAttacher.getScaleType();
    }

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAttacher.setAllowParentInterceptOnEdge(allow);
    }

    @Override
    public void setMinScale(float minScale) {
        mAttacher.setMinScale(minScale);
    }

    @Override
    public void setMidScale(float midScale) {
        mAttacher.setMidScale(midScale);
    }

    @Override
    public void setMaxScale(float maxScale) {
        mAttacher.setMaxScale(maxScale);
    }

    @Override
    // setImageBitmap calls through to this method
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm);
        if (bm == null) {
            return;
        }
        float screenSize = getScreenSize().x;
        float scall = screenSize / bm.getWidth();
        if (scall >= 1.0f) {
            mAttacher.setMaxScallValue(scall + 1);
        } else {
            mAttacher.setMaxScallValue(2.5f);
        }
    }

    public Point getScreenSize() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point mPoint = new Point();
        display.getSize(mPoint);
        return mPoint;
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        mAttacher.setOnMatrixChangeListener(listener);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mAttacher.setOnLongClickListener(l);
    }

    @Override
    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mAttacher.setOnPhotoTapListener(listener);
    }

    @Override
    public void setOnViewTapListener(OnViewTapListener listener) {
        mAttacher.setOnViewTapListener(listener);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (null != mAttacher) {
            mAttacher.setScaleType(scaleType);
        } else {
            mPendingScaleType = scaleType;
        }
    }

    @Override
    public void setZoomable(boolean zoomable) {
        mAttacher.setZoomable(zoomable);
    }

    @Override
    public void zoomTo(float scale, float focalX, float focalY) {
        mAttacher.zoomTo(scale, focalX, focalY);
    }

    @Override
    protected void onDetachedFromWindow() {
        mAttacher.cleanup();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMaxScallValue(float maxScallValue) {
        // TODO Auto-generated method stub

    }

}
