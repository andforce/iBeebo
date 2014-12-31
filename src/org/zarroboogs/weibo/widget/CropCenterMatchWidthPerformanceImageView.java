
package org.zarroboogs.weibo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * User: qii Date: 12-11-15
 */
public class CropCenterMatchWidthPerformanceImageView extends ImageView {

    private boolean mMeasuredExactly = false;

    private boolean mBlockMeasurement = false;

    private Drawable mDrawable;

    int viewW = 0;
    int viewH = 0;

    public CropCenterMatchWidthPerformanceImageView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public CropCenterMatchWidthPerformanceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public CropCenterMatchWidthPerformanceImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {

        mBlockMeasurement = true;
        super.setImageDrawable(drawable);
        mBlockMeasurement = false;
    }

    @Override
    public void requestLayout() {
        if (mBlockMeasurement && mMeasuredExactly) {
            // Ignore request

        } else {
            super.requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (mDrawable != null) {

            final Rect rect = mDrawable.getBounds();
            if (false) {
                Log.d("CROP_VIEW", "" + viewW + " x " + viewH);
            }
            int drawRecW = rect.width();
            int drawRectH = rect.height();
            if (drawRecW > 0 && drawRectH > 0 && viewH > 0 && viewW > 0) {
                float viewScal = viewW / viewH;
                float picScal = drawRecW / drawRectH;

                if (picScal < viewScal) {
                    float scal = viewW / drawRecW;
                    float height = drawRectH * scal;
                    float startH = (height - viewH) / 2;
                    mDrawable.setBounds(0, -(int) startH, viewW, viewH + (int) startH);
                } else {
                    float scal = viewH / drawRectH;
                    float width = drawRecW * scal;
                    float startW = (width - viewW) / 2;
                    mDrawable.setBounds(-(int) startW, 0, viewW + (int) startW, viewH);
                }
            }

            mDrawable.draw(canvas);
        } else {
            super.onDraw(canvas);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasuredExactly = isMeasuredExactly(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewW = getWidth();
        viewH = getHeight();
    }

    private boolean isMeasuredExactly(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasureSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMeasureSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        return widthMeasureSpecMode == MeasureSpec.EXACTLY && heightMeasureSpecMode == MeasureSpec.EXACTLY;
    }
}
