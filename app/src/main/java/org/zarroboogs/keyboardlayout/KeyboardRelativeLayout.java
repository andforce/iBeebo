
package org.zarroboogs.keyboardlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import org.zarroboogs.keyboardlayout.helper.KeyboardHelper;

public class KeyboardRelativeLayout extends RelativeLayout {


    private KeyboardHelper mHelper;

    public KeyboardRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHelper = new KeyboardHelper(this);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHelper = new KeyboardHelper(this);
    }

    public KeyboardRelativeLayout(Context context) {
        super(context);
        mHelper = new KeyboardHelper(this);
    }

    public void setOnKeyboardStateListener(OnKeyboardStateChangeListener listener) {
        mHelper.setOnKeyboardStateListener(listener);
    }


    public KeyboardHelper getKeyBoardHelper(){
        return mHelper;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mHelper.init();
    }

}
