
package org.zarroboogs.weibo.support.lib;

import android.content.BroadcastReceiver;

public abstract class RecordOperationAppBroadcastReceiver extends BroadcastReceiver {

    private boolean hasRegistered = false;

    private boolean hasUnRegistered = false;

    public boolean hasRegistered() {
        return hasRegistered;
    }

    public boolean hasUnRegistered() {
        return hasUnRegistered;
    }

    public void setHasRegistered(boolean value) {
        this.hasRegistered = value;
    }

    public void setHasUnRegistered(boolean value) {
        this.hasUnRegistered = value;
    }
}
