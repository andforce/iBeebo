package org.zarroboogs.weibo;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * Created by andforce on 15/9/4.
 */
public class GlideConfig implements GlideModule{
    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, 1024 * 1024 * 500));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
