
package org.zarroboogs.weibo.widget;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import android.content.Context;
import android.util.AttributeSet;

//com.rengwuxian.materialedittext.MaterialAutoCompleteTextView
public class KeyboardControlEditText extends MaterialAutoCompleteTextView {
    private boolean mShowKeyboard = true;

    public void setShowKeyboard(boolean value) {
        mShowKeyboard = value;
    }

    public KeyboardControlEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return mShowKeyboard;
    }
}
