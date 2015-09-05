
package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.bean.AccountBean;

public class WeiboDataProviderActivity extends TranslucentStatusBarActivity {

    public AccountBean getWeiboAccountBean() {
        return BeeboApplication.getInstance().getAccountBean();
    }
}
