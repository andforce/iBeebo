package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.dialogfragment.RemoveWeiboMsgDialog;
import org.zarroboogs.weibo.fragment.HotWeiboFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;

public class HotWeiboActivity extends AbstractAppActivity implements RemoveWeiboMsgDialog.IRemove {

    private Toolbar mToolbar;
    
	@Override
	public void onCreate(Bundle arg0, PersistableBundle arg1) {
		// TODO Auto-generated method stub
		super.onCreate(arg0, arg1);
        setContentView(R.layout.browser_weibo_msg_activity_layout);
        mToolbar = (Toolbar) findViewById(R.id.accountToolBar);
        mToolbar.setTitle(R.string.weibo_detail);
        
        buildContent();
	}
	
	@Override
	public void removeMsg(String id) {
		// TODO Auto-generated method stub
		
	}

    private void buildContent() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (getSupportFragmentManager().findFragmentByTag(HotWeiboFragment.class.getName()) == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content, new HotWeiboFragment(),HotWeiboFragment.class.getName())
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();
                }
            }
        });
    }
}
