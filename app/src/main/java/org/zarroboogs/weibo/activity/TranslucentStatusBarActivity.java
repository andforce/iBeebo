
package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.support.utils.ViewUtility;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class TranslucentStatusBarActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void disPlayHomeAsUp(int toobrID){
    	Toolbar toolbar = ViewUtility.findViewById(this, toobrID);
    	disPlayHomeAsUp(toolbar);
    }
	public void disPlayHomeAsUp(Toolbar toolbar) {
		if (toolbar != null) {
			setSupportActionBar(toolbar);
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        toolbar.setNavigationOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}
	}

}
