
package org.zarroboogs.weibo.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;

public class DummyLoader<T> extends AsyncTaskLoader<AsyncTaskLoaderResult<T>> {
    public DummyLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public AsyncTaskLoaderResult<T> loadInBackground() {
        return null;
    }
}
