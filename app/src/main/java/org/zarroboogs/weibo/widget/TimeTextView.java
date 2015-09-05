
package org.zarroboogs.weibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import org.zarroboogs.weibo.support.utils.TimeUtility;

public class TimeTextView extends TextView {

    public TimeTextView(Context context) {
        super(context);
    }

    public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTime(long mills) {

        String time = TimeUtility.getListTime(mills);
        if (!getText().toString().equals(time))
            setText(time);

    }
}
