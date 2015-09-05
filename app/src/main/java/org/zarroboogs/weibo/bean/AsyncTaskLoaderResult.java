
package org.zarroboogs.weibo.bean;

import android.os.Bundle;

import org.zarroboogs.util.net.WeiboException;

public class AsyncTaskLoaderResult<E> {
    public E data;
    public WeiboException exception;
    public Bundle args;
}
