package org.zarroboogs.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.zarroboogs.weibo.R;

/**
 * Created by andforce on 15/9/5.
 */
public class ImageLoader {

    private static RequestManager manager(Context context) {
        return Glide.with(context);
    }

    private static RequestManager manager(Activity activity) {
        return Glide.with(activity);
    }

    private static RequestManager manager(Fragment fragment) {
        return Glide.with(fragment);
    }

    public static void load(Context context, String string, ImageView imageView) {
        manager(context).load(string).placeholder(R.color.image_loader_place_holder).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
    }

    public static void load(Activity activity, String string, ImageView imageView) {
        manager(activity).load(string).placeholder(R.color.image_loader_place_holder).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
    }

    public static void load(Fragment fragment, String string, ImageView imageView) {
        manager(fragment).load(string).placeholder(R.color.image_loader_place_holder).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
    }
}
