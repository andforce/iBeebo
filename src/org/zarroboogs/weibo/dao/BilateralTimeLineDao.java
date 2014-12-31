package org.zarroboogs.weibo.dao;

import org.zarroboogs.utils.WeiBoURLs;

/**
 * User: qii Date: 12-9-13
 */
public class BilateralTimeLineDao extends MainFriendsTimeLineDao {

	public BilateralTimeLineDao(String access_token) {
		super(access_token);
	}

	@Override
	protected String getUrl() {
		return WeiBoURLs.BILATERAL_TIMELINE;
	}
}
