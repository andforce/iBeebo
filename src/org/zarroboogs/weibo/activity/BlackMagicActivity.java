
package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.BlackMagicLoginTask;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.support.utils.Utility;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.util.Log;
import android.view.MenuItem;

/**
 * User: qii Date: 12-11-9
 */
public class BlackMagicActivity extends AbstractAppActivity {

    private MaterialEditText username;

    private MaterialEditText password;

    private String appkey;

    private String appSecret;

    private BlackMagicLoginTask loginTask;

    private Toolbar mToobar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_blackmagicactivity_layout);

        mToobar = (Toolbar) findViewById(R.id.loginToolBar);
        mToobar.inflateMenu(R.menu.actionbar_menu_blackmagicactivity);
        mToobar.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {

                int itemId = arg0.getItemId();
				if (itemId == R.id.menu_login) {
					if (username.getText().toString().length() == 0) {
					    username.setError(getString(R.string.email_cant_be_empty));
					    return true;
					}
					if (password.getText().toString().length() == 0) {
					    password.setError(getString(R.string.password_cant_be_empty));
					    return true;
					}
					if (Utility.isTaskStopped(loginTask)) {

					    String[] array = getResources().getStringArray(R.array.tail_value);
					    String value = array[0];
					    
					    
					    appkey = "2027761570";//value.substring(0, value.indexOf(","));
					    appSecret = "5042214816d14b2d9e8ae8255f96180d";//value.substring(value.indexOf(",") + 1);
					    
					    
					    
					    Log.d("APPKEY", "key:" + appkey + "  secret:" + appSecret);
					    loginTask = new BlackMagicLoginTask(BlackMagicActivity.this, username.getText().toString(),
					            password.getText().toString(), appkey, appSecret);
					    loginTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
					}
					return true;
				} else {
					return false;
				}
            }
        });

        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);

        username.setText("123" + GlobalContext.getInstance().getAccountBean().getUname());
        // SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tail,
        // android.R.layout.simple_spinner_dropdown_item);

        // spinner.setAdapter(mSpinnerAdapter);
        // spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //
        // @Override
        // public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // String[] array = getResources().getStringArray(R.array.tail_value);
        // String value = array[position];
        // appkey = value.substring(0, value.indexOf(","));
        // appSecret = value.substring(value.indexOf(",") + 1);
        // }
        //
        // @Override
        // public void onNothingSelected(AdapterView<?> parent) {
        //
        // }
        // });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.cancelTasks(loginTask);
    }

    public static class ProgressFragment extends DialogFragment {

        private MyAsyncTask<?, ?, ?> asyncTask = null;

        public static ProgressFragment newInstance() {
            ProgressFragment frag = new ProgressFragment();
            frag.setRetainInstance(true);
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.logining));
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);

            return dialog;
        }

        @Override
        public void onCancel(DialogInterface dialog) {

            if (asyncTask != null) {
                asyncTask.cancel(true);
            }

            super.onCancel(dialog);
        }

        public void setAsyncTask(MyAsyncTask<?, ?, ?> task) {
            asyncTask = task;
        }
    }
}
