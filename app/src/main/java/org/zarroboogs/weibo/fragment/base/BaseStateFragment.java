
package org.zarroboogs.weibo.fragment.base;

import android.os.Bundle;

public class BaseStateFragment extends FixedOnActivityResultBugFragment {
    /**
     * when activity is recycled by system, isFirstTimeStartFlag will be reset to default true, when
     * activity is recreated because a configuration change for example screen rotate,
     * isFirstTimeStartFlag will stay false
     */
    private boolean isFirstTimeStartFlag = true;

    // when activity is first time start
    protected final int FIRST_TIME_START = 0;
    // when activity is destroyed and recreated because a configuration
    // change, see setRetainInstance(boolean retain)
    protected final int SCREEN_ROTATE = 1;
    // when activity is destroyed because memory is too low, recycled by android system
    protected final int ACTIVITY_DESTROY_AND_CREATE = 2;

    protected int getCurrentState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            isFirstTimeStartFlag = false;
            return ACTIVITY_DESTROY_AND_CREATE;
        }

        if (!isFirstTimeStartFlag) {
            return SCREEN_ROTATE;
        }

        isFirstTimeStartFlag = false;
        return FIRST_TIME_START;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }
}
