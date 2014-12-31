
package org.zarroboogs.weibo.support.asyncdrawable;

import org.zarroboogs.utils.ImageUtility;
import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.utils.file.FileManager;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

/**
 * User: qii Date: 12-8-5
 */
public class ProfileAvatarReadWorker extends MyAsyncTask<String, Integer, Bitmap> {

    private LruCache<String, Bitmap> lruCache;

    private String data = "";

    private ImageView view;

    private GlobalContext globalContext;

    public ProfileAvatarReadWorker(ImageView view, String url) {
        this.lruCache = GlobalContext.getInstance().getBitmapCache();
        this.view = view;
        this.globalContext = GlobalContext.getInstance();
        this.data = url;
    }

    @Override
    protected Bitmap doInBackground(String... url) {
        if (isCancelled()) {
            return null;
        }

        String path = FileManager.getFilePathFromUrl(data, FileLocationMethod.avatar_large);

        if (!(ImageUtility.isThisBitmapCanRead(path) && TaskCache.isThisUrlTaskFinished(data))) {
            boolean downloaded = TaskCache.waitForPictureDownload(data, null, path, FileLocationMethod.avatar_large);
            if (downloaded) {
                path = FileManager.getFilePathFromUrl(data, FileLocationMethod.avatar_large);
            }
        }

        int avatarWidth = globalContext.getResources().getDimensionPixelSize(R.dimen.profile_avatar_width);
        int avatarHeight = globalContext.getResources().getDimensionPixelSize(R.dimen.profile_avatar_height);

        return ImageUtility.getRoundedCornerPic(path, avatarWidth, avatarHeight);

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {
            view.setVisibility(View.VISIBLE);
            view.setImageBitmap(bitmap);
            lruCache.put(data, bitmap);

        } else {
            view.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

    }

}
