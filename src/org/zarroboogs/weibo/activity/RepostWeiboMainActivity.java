
package org.zarroboogs.weibo.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import lib.org.zarroboogs.weibo.login.javabean.DoorImageAsyncTask;
import lib.org.zarroboogs.weibo.login.javabean.RequestResultBean;
import lib.org.zarroboogs.weibo.login.javabean.DoorImageAsyncTask.OnDoorOpenListener;
import lib.org.zarroboogs.weibo.login.utils.LogTool;

import org.apache.http.Header;
import org.zarroboogs.util.net.LoginWeiboAsyncTask.LoginCallBack;
import org.zarroboogs.utils.Utility;
import org.zarroboogs.utils.WeiBaNetUtils;
import org.zarroboogs.weibo.ChangeWeibaAdapter;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.WebViewActivity;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.WeibaGson;
import org.zarroboogs.weibo.bean.WeibaTree;
import org.zarroboogs.weibo.bean.WeiboWeiba;
import org.zarroboogs.weibo.db.AppsrcDatabaseManager;
import org.zarroboogs.weibo.selectphoto.ImgFileListActivity;
import org.zarroboogs.weibo.selectphoto.SendImgData;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.TimeLineUtility;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshListView;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class RepostWeiboMainActivity extends BaseLoginActivity implements LoginCallBack,
        OnClickListener, OnGlobalLayoutListener, OnItemClickListener {

    public static final String TAG = "RepostWeiboMainActivity ";

    private MessageBean msg;
    String pidC = "";
    RelativeLayout mEmotionRelativeLayout;

    InputMethodManager imm = null;
    MaterialEditText mEditText;
    RelativeLayout mRootView;

    RelativeLayout editTextLayout;
    ImageButton mSelectPhoto;
    ImageButton mSendBtn;
    ImageButton smileButton;

    Button appSrcBtn;
    AccountBean mAccountBean;
    private ScrollView mEditPicScrollView;

    private TextView weiTextCountTV;

    ArrayList<ImageView> mSelectImageViews = new ArrayList<ImageView>();

    ArrayList<ImageView> mEmotionArrayList = new ArrayList<ImageView>();
    Toast mEmptyToast;
    DisplayImageOptions options;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;

    AppsrcDatabaseManager mDBmanager = null;
    PullToRefreshListView listView;
    ChangeWeibaAdapter listAdapter;
    List<WeiboWeiba> listdata = new ArrayList<WeiboWeiba>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.repost_with_weibo_activity);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.writeWeiboDrawerL);
        mToolbar = (Toolbar) findViewById(R.id.writeWeiboToolBar);

        mDrawerToggle = new MyDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mAccountBean = getIntent().getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        // mAccountBean = getAccount();
        Log.d("RpostWeiBo_activity", "AccountBean == null ? : " + (mAccountBean == null));
        mEmptyToast = Toast.makeText(getApplicationContext(), R.string.text_is_empty, Toast.LENGTH_SHORT);

        mEditPicScrollView = (ScrollView) findViewById(R.id.scrollView1);
        editTextLayout = (RelativeLayout) findViewById(R.id.editTextLayout);

        weiTextCountTV = (TextView) findViewById(R.id.weiTextCountTV);

        appSrcBtn = (Button) findViewById(R.id.appSrcBtn);
        appSrcBtn.setText(getWeiba().getText());

        mSelectPhoto = (ImageButton) findViewById(R.id.imageButton1);
        mRootView = (RelativeLayout) findViewById(R.id.container);
        mEditText = (MaterialEditText) findViewById(R.id.weiboContentET);
        mEmotionRelativeLayout = (RelativeLayout) findViewById(R.id.smileLayout_ref);
        smileButton = (ImageButton) findViewById(R.id.smileImgButton);
        mSendBtn = (ImageButton) findViewById(R.id.sendWeiBoBtn);

        findAllEmotionImageView((ViewGroup) findViewById(R.id.emotionTL));
        mSelectPhoto.setOnClickListener(this);
        smileButton.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        appSrcBtn.setOnClickListener(this);
        mEditPicScrollView.setOnClickListener(this);
        editTextLayout.setOnClickListener(this);
        mEditText.addTextChangedListener(watcher);

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        Intent intent = getIntent();
        handleNormalOperation(intent);
        mDBmanager = new AppsrcDatabaseManager(getApplicationContext());
        listAdapter = new ChangeWeibaAdapter(this);
        listView = (PullToRefreshListView) findViewById(R.id.left_menu_list_view);
        listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
                    listView.setRefreshing();
                    fetchWeiBa();
                } else {
                    listView.post(new Runnable() {
                        public void run() {
                            listView.onRefreshComplete();
                        }
                    });
                    Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
                }

            }
        });
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        setOnRepostWeiboListener(new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                LogTool.D(TAG + "onSuccess " + new String(responseBody));

                RequestResultBean sendResultBean = getRequestResultParser().parse(responseBody, RequestResultBean.class);
                LogTool.D(TAG + "onSuccess " + sendResultBean.getMsg());
                if (sendResultBean.getMsg().equals("未登录")) {
                    doPreLogin(mAccountBean.getUname(), mAccountBean.getPwd());
                    hideDialogForWeiBo();
                }

                if (sendResultBean.getCode().equals("100000")) {
                    hideDialogForWeiBo();
                    mEditText.setText("");
                    Toast.makeText(getApplicationContext(), "转发成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideDialogForWeiBo();
                LogTool.D(TAG + "onFailure " + error.getLocalizedMessage());
            }
        });

        setOnLoginListener(new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogTool.D(TAG + " Login-  onSuccess " + new String(responseBody));
                repostWeibo();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    class MyDrawerToggle extends ActionBarDrawerToggle {

        public MyDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes,
                int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

            List<WeiboWeiba> list = mDBmanager.fetchAllAppsrc();
            /*
             * if (isKeyBoardShowed) { imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
             * InputMethodManager.HIDE_NOT_ALWAYS); }
             */
            if (list.size() == 0) {
                if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
                    fetchWeiBa();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
                }
            } else {
                listAdapter.setWeibas(list);
            }
        }
    }

    private void fetchWeiBa() {
        showDialogForWeiBo();
        String url = "http://appsrc.sinaapp.com/";
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
                    for (WeiboWeiba weiba : weibas) {
                        if (mDBmanager.searchAppsrcByCode(weiba.getCode()) == null) {
                            mDBmanager.insertCategoryTree(0, weiba.getCode(), weiba.getText());
                        }
                    }
                }

                listView.onRefreshComplete();
                listAdapter.setWeibas(mDBmanager.fetchAllAppsrc());
                hideDialogForWeiBo();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
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

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            String charSequence = mEditText.getText().toString();
            int count = Utility.length(charSequence);
            String text = count <= 0 ? getString(R.string.send_weibo) : count + "";
            weiTextCountTV.setText(text);
            if (count > 140) {
                weiTextCountTV.setTextColor(Color.RED);
            } else {
                weiTextCountTV.setTextColor(Color.BLACK);
            }
        }
    };

    public static int calculateWeiboLength(CharSequence c) {

        int len = 0;
        for (int i = 0; i < c.length(); i++) {
            int temp = (int) c.charAt(i);
            if (temp > 0 && temp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        ImageLoader.getInstance().stop();
    }

    private void findAllEmotionImageView(ViewGroup vg) {
        int count = vg.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = vg.getChildAt(i);
            if (v instanceof TableRow) {
                findAllEmotionImageView((TableRow) v);
            } else {
                ((ImageView) v).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        String text = ((ImageView) v).getContentDescription() + "";
                        int index = mEditText.getSelectionStart();// 获取光标所在位置
                        Editable edit = mEditText.getEditableText();// 获取EditText的文字
                        if (index < 0 || index >= edit.length()) {
                            edit.append(text);
                        } else {
                            edit.insert(index, text);// 光标所在位置插入文字
                        }
                        String content = mEditText.getText().toString();
                        TimeLineUtility.addEmotions(mEditText, content);
                        mEditText.setSelection(index + text.length());
                    }
                });
                mEmotionArrayList.add((ImageView) v);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ChangeWeibaActivity.REQUEST) {
            appSrcBtn.setText(getWeiba().getText());
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (!(mEmotionRelativeLayout.getVisibility() == View.GONE)) {
            mEmotionRelativeLayout.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    public void startLogIn() {
        hideDialogForWeiBo();
        Intent intent = new Intent();
        intent.setClass(RepostWeiboMainActivity.this, WebViewActivity.class);
        startActivity(intent);

    }

    @Override
    public void reSizeWeiboPictures(boolean isSuccess) {
        if (!isSuccess) {
            startLogIn();
        } else {
            final SendImgData sendImgData = SendImgData.getInstance();

            ArrayList<String> send = sendImgData.getSendImgs();
            final int count = send.size();

            String text = mEditText.getText().toString();
            if (TextUtils.isEmpty(text)) {
                text = "转发微博";
            }
            repostWeibo(getWeiba().getCode(), text, "", msg.getId());

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);
        appSrcBtn.setText(getWeiba().getText());
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.editTextLayout: {
                mEditText.performClick();
                break;
            }
            case R.id.scrollView1: {
                mEditText.performClick();
                break;
            }
            case R.id.appSrcBtn: {
                if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
                    mDrawerLayout.openDrawer(Gravity.START);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.sendWeiBoBtn: {
                if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
                    showDialogForWeiBo();
                    repostWeibo();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.smileImgButton: {
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (mEmotionRelativeLayout.getVisibility() == View.GONE) {
                            mEmotionRelativeLayout.setVisibility(View.VISIBLE);
                        } else {
                            mEmotionRelativeLayout.setVisibility(View.GONE);
                        }
                    }
                }, 100);

                break;
            }
            case R.id.imageButton1: {
                Intent mIntent = new Intent(getApplicationContext(), ImgFileListActivity.class);
                startActivityForResult(mIntent, ImgFileListActivity.REQUEST_CODE);
                break;
            }
            default:
                break;
        }

    }

    @Override
    public void onGlobalLayout() {
        // TODO Auto-generated method stub

        Rect r = new Rect();
        mEmotionRelativeLayout.getWindowVisibleDisplayFrame(r);

        int heightDiff = mEmotionRelativeLayout.getRootView().getHeight() - (r.bottom - r.top);
        if (heightDiff > 100) {
            // if more than 100 pixels, its probably a keyboard...
            Log.d("WEIBO_INPUT", "++++++++");
            mEmotionRelativeLayout.setVisibility(View.GONE);
        } else {
            Log.d("WEIBO_INPUT", "---------");
        }

    }

    class WeiBaCacheFile implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {
            // TODO Auto-generated method stub
            return filename.startsWith("WEI-");
        }

    }

    private void handleNormalOperation(Intent intent) {

        msg = (MessageBean) intent.getParcelableExtra("msg");

        if (msg.getRetweeted_status() != null) {
            mEditText.setText("//@" + msg.getUser().getScreen_name() + ": " + msg.getText());
            mEditText.setHint("//@" + msg.getRetweeted_status().getUser().getScreen_name() + "："
                    + msg.getRetweeted_status().getText());
        } else {
            mEditText.setHint("@" + msg.getUser().getScreen_name() + "：" + msg.getText());
        }
        mEditText.setSelection(0);
    }

    private void repostWeibo() {
        String text = mEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            text = "转发微博";
        }

        repostWeibo(getWeiba().getCode(), text, "", msg.getId());
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            if (msg.what == 1002) {
                DoorImageAsyncTask doorImageAsyncTask = new DoorImageAsyncTask();
                doorImageAsyncTask.setOnDoorOpenListener(new OnDoorOpenListener() {

                    @Override
                    public void onDoorOpen(android.graphics.Bitmap result) {
                        // TODO Auto-generated method stub
                        // mDoorImg.setImageBitmap(result);
                    }
                });
                doorImageAsyncTask.execute("");
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WeiboWeiba weiba = ((WeiboWeiba) parent.getItemAtPosition(position));
        Log.d("CLICK", "" + weiba);
        saveWeiba(weiba);
        // menu.toggle();
        mDrawerLayout.closeDrawer(findViewById(R.id.drawerLeft));
    }
}
