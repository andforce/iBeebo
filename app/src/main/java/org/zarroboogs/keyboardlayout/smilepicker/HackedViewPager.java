
package org.zarroboogs.keyboardlayout.smilepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HackedViewPager extends android.support.v4.view.ViewPager {

    public HackedViewPager(Context context) {
        super(context);
    }

    public HackedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
