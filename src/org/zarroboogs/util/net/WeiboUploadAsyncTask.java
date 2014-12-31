
package org.zarroboogs.util.net;

import lib.org.zarroboogs.utils.net.BroserContent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class WeiboUploadAsyncTask extends AsyncTask<Context, Integer, String> {

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

    @Override
    protected String doInBackground(Context... params) {
        // TODO Auto-generated method stub
        BroserContent mBroserContent = BroserContent.getInstance();
        HttpPostHelper mPostHelper = new HttpPostHelper();
        SharedPreferences mPreferences = params[0].getSharedPreferences("123", Context.MODE_PRIVATE);
        // mPostHelper...sendWeibo(mBroserContent,
        // "http://widget.weibo.com/public/aj_addMblog.php", "3o33sO",
        // "tttttttt", mPreferences.getString("cookie", ""));
        String a = mPostHelper.uploadPicToWeibo(mBroserContent,
                "http://picupload.service.weibo.com/interface/pic_upload.php?app="
                        + "miniblog&data=1&mime=image/png&ct=0.2805887470021844", mPreferences.getString("cookie", ""), pic);
        return a;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        onUploaded.onUploaded(result);
    }
}
