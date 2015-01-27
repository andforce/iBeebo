
package org.zarroboogs.util.net;

import lib.org.zarroboogs.utils.net.BroserContent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class WeiboUploadAsyncTask{

    public static interface OnUploaded {
        public void onUploaded(String pid);
    }

    private OnUploaded onUploaded;
    private String pic = "";

    private WeiboUploadAsyncTask() {
        // TODO Auto-generated constructor stub
    }

    public WeiboUploadAsyncTask(OnUploaded up, String pic) {
        this.onUploaded = up;
        this.pic = pic;
    }

}
