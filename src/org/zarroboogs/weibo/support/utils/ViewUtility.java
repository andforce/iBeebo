
package org.zarroboogs.weibo.support.utils;

import android.app.Activity;
import android.view.View;

public class ViewUtility {

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(View view, int id) {
        return (T) view.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }
}
