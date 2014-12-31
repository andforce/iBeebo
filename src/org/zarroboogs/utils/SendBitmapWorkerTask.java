
package org.zarroboogs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.zarroboogs.weibo.setting.SettingUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class SendBitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    public static interface OnCacheDoneListener {
        public void onCacheDone(String newFile);
    }

    private OnCacheDoneListener mCacheDoneListener;

    private String mFileName = null;
    private File cacheDir;
    private Context mContext;

    public SendBitmapWorkerTask(Context context, OnCacheDoneListener listener) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        mContext = context;
        this.mCacheDoneListener = listener;
        cacheDir = mContext.getExternalCacheDir();
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        mFileName = params[0];
        if (mFileName.contains(".gif")) {
            return null;
        }
        int uploadWidth = SettingUtils.isUploadBigPic() ? 2048 : 720;
        EditBitmapUtils editBitmapUtils = new EditBitmapUtils(mContext);
        return editBitmapUtils.getBitmapByMaxWidth(Uri.fromFile(new File(mFileName)), uploadWidth);
        // return decodeSampledBitmapFromFile(mFileName, uploadWidth);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        String newFile = null;
        if (mFileName.contains(".gif")) {
            // newFile = BitmapUtils.saveBitmapToFile(cacheDir, "WEI-" + makeMD5(mFileName), bitmap,
            // ".gif");
            String newFilePath = cacheDir.getAbsolutePath() + "/WEI-" + makeMD5(mFileName) + ".gif";
            copy(mFileName, newFilePath);
            newFile = newFilePath;
            Log.d("AAAAAAAAAA", "old: " + mFileName + "  new :" + newFile);
        } else {
            newFile = BitmapUtils.saveBitmapToFile(cacheDir, "WEI-" + makeMD5(mFileName), bitmap, ".jpg");
        }

        if (mCacheDoneListener != null) {
            mCacheDoneListener.onCacheDone(newFile);
        }
    }

    private boolean copy(String fileFrom, String fileTo) {
        try {
            FileInputStream in = new java.io.FileInputStream(fileFrom);
            FileOutputStream out = new FileOutputStream(fileTo);
            byte[] bt = new byte[1024];
            int count;
            while ((count = in.read(bt)) > 0) {
                out.write(bt, 0, count);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static String makeMD5(String password) {
        MessageDigest md;
        try {
            // 生成一个MD5加密计算摘要
            md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(password.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String pwd = new BigInteger(1, md.digest()).toString(16);
            return pwd;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果失败要返回null
        return null;
    }

    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        Log.d("UploadSize", "scale:" + options.inSampleSize);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > reqWidth) {
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }
        return inSampleSize;
    }
}
