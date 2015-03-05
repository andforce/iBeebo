package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.HotModelDetailFragment;
import org.zarroboogs.weibo.fragment.HotModelFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

public class HotModelActivity extends TranslucentStatusBarActivity {

	private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	 setContentView(R.layout.hotweibo_activity_layout);
         
    	 mToolbar = (Toolbar) findViewById(R.id.hotWeiboToolbar);
    	 mToolbar.setTitle("车模热图");
    	 
         buildContent();
    }

    private void buildContent() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (getSupportFragmentManager().findFragmentByTag(HotModelFragment.class.getName()) == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.hotWeiboContent, new HotModelFragment(),HotModelFragment.class.getName())
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();
                }
            }
        });
    }
    
    
    public void switch2DetailFragemt(final String extparam){
    	new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
            	HotModelDetailFragment ft = (HotModelDetailFragment) getSupportFragmentManager().findFragmentByTag(HotModelDetailFragment.class.getName());
            	
            	if (ft == null) {
                	ft = new HotModelDetailFragment();
				}
            	
            	Bundle bundle = new Bundle();
            	bundle.putString("extparam", extparam);
            	
            	ft.setArguments(bundle);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.hotWeiboContent, ft).addToBackStack(null)
						.commitAllowingStateLoss();
	
            	getSupportFragmentManager().executePendingTransactions();
            }
        });
    }
}
