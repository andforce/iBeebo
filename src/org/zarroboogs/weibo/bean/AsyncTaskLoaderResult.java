package org.zarroboogs.weibo.bean;

import android.os.Bundle;

import org.zarroboogs.util.net.WeiboException;

/**
 * User: qii Date: 13-4-16
 */
public class AsyncTaskLoaderResult<E> {
	public E data;
	public WeiboException exception;
	public Bundle args;
}
