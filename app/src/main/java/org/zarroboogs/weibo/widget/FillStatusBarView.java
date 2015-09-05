package org.zarroboogs.weibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by andforce on 15/8/29.
 */
public class FillStatusBarView extends View {
    public FillStatusBarView(Context context) {
        super(context);
    }

    public FillStatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FillStatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, getStatusBarHeight());
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
