
package org.zarroboogs.weibo.widget;

import android.content.Context;
import android.util.AttributeSet;

import org.zarroboogs.weibo.support.utils.Utility;

public class ProfileTopAvatarImageView extends TimeLineAvatarImageView {

    public ProfileTopAvatarImageView(Context context) {
        super(context);
    }

    public ProfileTopAvatarImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileTopAvatarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void initLayout(Context context) {
        setPadding(Utility.dip2px(5), Utility.dip2px(5), Utility.dip2px(5), Utility.dip2px(5));

    }

}
