
package org.zarroboogs.weibo.support.asyncdrawable;

import android.graphics.drawable.ColorDrawable;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.support.utils.ThemeUtility;

import java.lang.ref.WeakReference;

/**
 * User: qii Date: 12-9-5
 */
public class PictureBitmapDrawable extends ColorDrawable {
    private final WeakReference<IPictureWorker> bitmapDownloaderTaskReference;

    public PictureBitmapDrawable(IPictureWorker bitmapDownloaderTask) {
        super(ThemeUtility.getColor(R.attr.listview_pic_bg));
        bitmapDownloaderTaskReference = new WeakReference<IPictureWorker>(bitmapDownloaderTask);
    }

    public IPictureWorker getBitmapDownloaderTask() {
        return bitmapDownloaderTaskReference.get();
    }
}
