package org.zarroboogs.weibo.widget.galleryview;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {
    private static final String TAG = "BitmapWorkerTask";
    private final ImageView imageViewReference;
    private Uri mUri = null;
    private Context mContext;

    private int scalW = -1;
    private int scalH = -1;
    private static final int DEF_W = 720;
    private static final int DEF_H = 1080;

    public BitmapWorkerTask(ImageView imageView, Context context) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = imageView;
        mContext = context.getApplicationContext();
    }

    private OnCreateBitmapListener mListener;

    public void setOnCreateBitmapListener(OnCreateBitmapListener listener) {
        this.mListener = listener;
    }

    public interface OnCreateBitmapListener {
        void onCreateBitmap(Bitmap newBitmap);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Uri... params) {
        mUri = params[0];
        if (scalW == -1 || scalH == -1) {
            return decodeSampledBitmapFromFile(mUri, DEF_W, DEF_H);
        } else {
            return decodeSampledBitmapFromFile(mUri, scalW, scalH);
        }

    }

    public void setBitmapScalWH(int scalW, int scalH) {
        this.scalW = scalW;
        this.scalH = scalH;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            if (imageViewReference != null) {
                imageViewReference.setImageBitmap(bitmap);
            }
        }

        if (mListener != null) {
            this.mListener.onCreateBitmap(bitmap);
        }
    }

    public Bitmap decodeSampledBitmapFromFile(Uri uri, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        ContentResolver cr = mContext.getContentResolver();
        Rect rect = new Rect();
        if (uri.toString().startsWith("content")) {
            InputStream is = null;
            try {
                is = cr.openInputStream(uri);
                BitmapFactory.decodeStream(is, rect, options);
                is.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (uri.toString().startsWith("file")) {
            String path = uri.toString().split("file://")[1];
            BitmapFactory.decodeFile(path, options);
        } else {
            BitmapFactory.decodeFile(uri.toString(), options);
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap result = null;
        if (uri.toString().startsWith("content")) {
            InputStream is = null;
            try {
                is = cr.openInputStream(uri);
                result = BitmapFactory.decodeStream(is, rect, options);
                is.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (uri.toString().startsWith("file")) {
            String path = uri.toString().split("file://")[1];
            result = BitmapFactory.decodeFile(path, options);
        } else {
            result = BitmapFactory.decodeFile(uri.toString(), options);
        }

        return result;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            float wRatio = width / reqWidth;
            float hRatio = height / reqHeight;

            if (hRatio > wRatio) {
                // scale by Height
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }
}
