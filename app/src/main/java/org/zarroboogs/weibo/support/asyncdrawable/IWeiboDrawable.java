
package org.zarroboogs.weibo.support.asyncdrawable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.zarroboogs.weibo.bean.UserBean;

public interface IWeiboDrawable {

    void setImageDrawable(Drawable drawable);

    void setImageBitmap(Bitmap bm);

    ImageView getImageView();

    void setProgress(int value, int max);

    ProgressBar getProgressBar();

    void setGifFlag(boolean value);

    void checkVerified(UserBean user);

    void setPressesStateVisibility(boolean value);

    void setVisibility(int visibility);

    int getVisibility();

    void setOnClickListener(View.OnClickListener onClickListener);

    void setOnLongClickListener(View.OnLongClickListener onLongClickListener);

    void setLayoutParams(ViewGroup.LayoutParams layoutParams);
}
