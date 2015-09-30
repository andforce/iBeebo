
package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.support.utils.ViewUtility;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

public class ToolBarAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void disPlayHomeAsUp(int toolbarId) {
        Toolbar toolbar = ViewUtility.findViewById(this, toolbarId);
        disPlayHomeAsUp(toolbar);
    }

    public void disPlayHomeAsUp(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

}
