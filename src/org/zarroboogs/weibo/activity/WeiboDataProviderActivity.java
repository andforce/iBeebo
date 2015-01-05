
package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.bean.AccountBean;

public class WeiboDataProviderActivity extends TranslucentStatusBarActivity {

    public AccountBean getWeiboAccountBean() {
        return GlobalContext.getInstance().getAccountBean();
    }
}
