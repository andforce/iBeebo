
package org.zarroboogs.weibo.dao;

import org.zarroboogs.utils.WeiBoURLs;

public class BilateralTimeLineDao extends MainTimeLineDao {

    public BilateralTimeLineDao(String access_token) {
        super(access_token);
    }

    @Override
    protected String getUrl() {
        return WeiBoURLs.BILATERAL_TIMELINE;
    }
}
