
package org.zarroboogs.weibo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

public class MyAnimationListener extends AnimatorListenerAdapter {
    private Runnable mRunnable;

    public MyAnimationListener(Runnable r) {
        // TODO Auto-generated constructor stub
        this.mRunnable = r;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        // TODO Auto-generated method stub
        super.onAnimationEnd(animation);
        if (mRunnable != null) {
            mRunnable.run();
        }
    }
}
