package org.zarroboogs.weibo.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.zarroboogs.weibo.ChangeWeibaAdapter;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.WeibaGson;
import org.zarroboogs.weibo.bean.WeibaTree;
import org.zarroboogs.weibo.bean.WeiboWeiba;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChangeWeibaActivity extends SharedPreferenceActivity implements OnItemClickListener {

	public static final int REQUEST = 0x0002;
	ListView listView;
	ChangeWeibaAdapter listAdapter;
	List<WeiboWeiba> listdata = new ArrayList<WeiboWeiba>();

	ProgressDialog mDialog;

	   public static interface OnFetchDoneListener {
	        public void onFetchDone(String isSuccess);
	    }

	    public OnFetchDoneListener mInSuccessListener;
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_weiba_activity_layout);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage(getString(R.string.fetch_new_weiba));
		mDialog.setCancelable(false);

/*		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.show();*/

		listView = (ListView) findViewById(R.id.weibaListView);
		listAdapter = new ChangeWeibaAdapter(this);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

		fetchWeiBa();
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

	private void showDialogForWeiBo() {
		if (!mDialog.isShowing()) {
			mDialog.show();
		}

	}

	private void hideDialogForWeiBo() {
		mDialog.cancel();
		mDialog.hide();
	}

	private void fetchWeiBa() {
		showDialogForWeiBo();
	      String url = "http://appsrc.sinaapp.com/";
	        
//	        Header[] loginHeaders = {
//	                new BasicHeader("Cache-Control", "max-age=0"),
//	                new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
//	                new BasicHeader("User-Agent",
//	                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.122 Safari/537.36"),
//	                new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"), new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"), };
	        
		getAsyncHttpClient().get(url, new AsyncHttpResponseHandler() {
            
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp = new String(responseBody);
                String jsonString = resp.split("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")[1];
                Gson gson = new Gson();
                WeibaGson weibaGson = gson.fromJson(jsonString, WeibaGson.class);
                List<WeibaTree> weibaTrees = weibaGson.getData();
                for (WeibaTree weibaTree : weibaTrees) {
                    List<WeiboWeiba> weibas = weibaTree.getData();
                    listdata.addAll(weibas);
                    for (WeiboWeiba weiba : weibas) {
                        Log.d("FETCH_WEIBA", "Name:" + weiba.getText() + "  Code:" + weiba.getCode());
                    }
                }
                listAdapter.setWeibas(listdata);
                hideDialogForWeiBo();
                
            }
            
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_OK);
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		WeiboWeiba weiba = ((WeiboWeiba) arg0.getItemAtPosition(arg2));
		Log.d("CLICK", "" + weiba);
		saveWeiba(weiba);
		setResult(RESULT_OK);
		finish();
	}
}
