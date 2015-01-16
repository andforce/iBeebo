
package org.zarroboogs.weibo.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import lib.org.zarroboogs.weibo.login.httpclient.WaterMark;
import lib.org.zarroboogs.weibo.login.javabean.RequestResultBean;
import lib.org.zarroboogs.weibo.login.utils.LogTool;

import org.apache.http.Header;
import org.zarroboogs.util.net.LoginWeiboAsyncTask.LoginWeiboCallack;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.SendBitmapWorkerTask;
import org.zarroboogs.utils.Utility;
import org.zarroboogs.utils.WeiBaNetUtils;
import org.zarroboogs.utils.SendBitmapWorkerTask.OnCacheDoneListener;
import org.zarroboogs.weibo.ChangeWeibaAdapter;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.WebViewActivity;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.WeiboWeiba;
import org.zarroboogs.weibo.db.AppsrcDatabaseManager;
import org.zarroboogs.weibo.db.task.AccountDBTask;
import org.zarroboogs.weibo.selectphoto.ImgFileListActivity;
import org.zarroboogs.weibo.selectphoto.SendImgData;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.SmileyPickerUtility;
import org.zarroboogs.weibo.widget.SmileyPicker;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WriteWeiboWithAppSrcActivity extends BaseLoginActivity implements LoginWeiboCallack, OnClickListener,
        OnGlobalLayoutListener, OnItemClickListener, OnSharedPreferenceChangeListener {

    public static final int AT_USER = 0x1000;

    public static final String LOGIN_TAG = "START_SEND_WEIBO ";
    protected static final String TAG = "WeiboMainActivity  ";
    private SmileyPicker mSmileyPicker = null;

    private InputMethodManager imm = null;
    private MaterialEditText mEditText;
    private RelativeLayout mRootView;

    private RelativeLayout editTextLayout;
    private ImageButton mSelectPhoto;
    private ImageButton mSendBtn;
    private ImageButton smileButton;
    private ImageButton mTopicBtn;
    private ImageButton mAtButton;

    private Button appSrcBtn;
    TableLayout sendImgTL;

    AccountBean mAccountBean;

    private boolean isKeyBoardShowed = false;
    private ScrollView mEditPicScrollView;
    private RelativeLayout mRow001;
    private RelativeLayout mRow002;
    private RelativeLayout mRow003;

    private ImageView mImageView001;
    private ImageView mImageView002;
    private ImageView mImageView003;
    private ImageView mImageView004;
    private ImageView mImageView005;
    private ImageView mImageView006;
    private ImageView mImageView007;
    private ImageView mImageView008;
    private ImageView mImageView009;

    private TextView weiTextCountTV;

    ArrayList<ImageView> mSelectImageViews = new ArrayList<ImageView>();

    ArrayList<ImageView> mEmotionArrayList = new ArrayList<ImageView>();

    Toast mEmptyToast;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    PullToRefreshListView listView;
    ChangeWeibaAdapter listAdapter;
    List<WeiboWeiba> listdata = new ArrayList<WeiboWeiba>();
    AppsrcDatabaseManager mDBmanager = null;// new
                                            // AppsrcDatabaseManager(getApplicationContext());
    private String atContent = "";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;

    private SendImgData sendImgData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppSrcSharedPreference().registerOnSharedPreferenceChangeListener(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.write_weibo_with_appsrc_activity_layout);
        // drawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.writeWeiboDrawerL);
        mToolbar = (Toolbar) findViewById(R.id.writeWeiboToolBar);

        mDrawerToggle = new MyDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mAccountBean = getIntent().getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        atContent = getIntent().getStringExtra("content");

        mEmptyToast = Toast.makeText(getApplicationContext(), R.string.text_is_empty, Toast.LENGTH_SHORT);

        mEditPicScrollView = (ScrollView) findViewById(R.id.scrollView1);
        editTextLayout = (RelativeLayout) findViewById(R.id.editTextLayout);

        weiTextCountTV = (TextView) findViewById(R.id.weiTextCountTV);
        sendImgTL = (TableLayout) findViewById(R.id.sendImgTL_ref);
        sendImgTL.setVisibility(View.GONE);

        mRow001 = (RelativeLayout) findViewById(R.id.sendPicRow01);
        mRow002 = (RelativeLayout) findViewById(R.id.sendPicRow02);
        mRow003 = (RelativeLayout) findViewById(R.id.sendPicRow03);

        appSrcBtn = (Button) findViewById(R.id.appSrcBtn);
        appSrcBtn.setText(getWeiba().getText());
        Log.d("MAIN_", "" + sendImgTL.getChildCount());
        mImageView001 = (ImageView) findViewById(R.id.IVRow101);
        mImageView002 = (ImageView) findViewById(R.id.IVRow102);
        mImageView003 = (ImageView) findViewById(R.id.IVRow103);
        mImageView004 = (ImageView) findViewById(R.id.IVRow201);
        mImageView005 = (ImageView) findViewById(R.id.IVRow202);
        mImageView006 = (ImageView) findViewById(R.id.IVRow203);
        mImageView007 = (ImageView) findViewById(R.id.IVRow301);
        mImageView008 = (ImageView) findViewById(R.id.IVRow302);
        mImageView009 = (ImageView) findViewById(R.id.IVRow303);

        mSelectImageViews.add(mImageView001);
        mSelectImageViews.add(mImageView002);
        mSelectImageViews.add(mImageView003);
        mSelectImageViews.add(mImageView004);
        mSelectImageViews.add(mImageView005);
        mSelectImageViews.add(mImageView006);
        mSelectImageViews.add(mImageView007);
        mSelectImageViews.add(mImageView008);
        mSelectImageViews.add(mImageView009);

        mSelectPhoto = (ImageButton) findViewById(R.id.imageButton1);
        mRootView = (RelativeLayout) findViewById(R.id.container);
        mEditText = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.weiboContentET);
        mSmileyPicker = (SmileyPicker) findViewById(R.id.smileLayout_ref);
        mSmileyPicker.setEditText(this, mRootView, mEditText);
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSmileyPicker(true);
            }
        });

        mTopicBtn = (ImageButton) findViewById(R.id.menu_topic);
        mAtButton = (ImageButton) findViewById(R.id.menu_at);

        smileButton = (ImageButton) findViewById(R.id.smileImgButton);
        mSendBtn = (ImageButton) findViewById(R.id.sendWeiBoBtn);

        mTopicBtn.setOnClickListener(this);
        mAtButton.setOnClickListener(this);

        // findAllEmotionImageView((ViewGroup) findViewById(R.id.emotionTL));
        mSelectPhoto.setOnClickListener(this);
        smileButton.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        appSrcBtn.setOnClickListener(this);
        mEditPicScrollView.setOnClickListener(this);
        editTextLayout.setOnClickListener(this);
        mEditText.addTextChangedListener(watcher);

        if (!TextUtils.isEmpty(atContent)) {
            mEditText.setText(atContent + " ");
            mEditText.setSelection(mEditText.getEditableText().toString().length());
        }

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        mDBmanager = new AppsrcDatabaseManager(getApplicationContext());

        listAdapter = new ChangeWeibaAdapter(this);
        listView = (PullToRefreshListView) findViewById(R.id.left_menu_list_view);
        listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
                    listView.setRefreshing();
                    fetchAppSrc();
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

        setAutoSendWeiboListener(new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogTool.D(TAG + "onSuccess " + new String(responseBody));

                RequestResultBean sendResultBean = getRequestResultParser().parse(responseBody, RequestResultBean.class);
                
                LogTool.D(TAG + "onSuccess " + sendResultBean.getMsg());
                
                
                if (sendResultBean.getMsg().equals("未登录")) {
                    startAutoPreLogin(mAccountBean.getUname(), mAccountBean.getPwd());
                    
                }else if (sendResultBean.getMsg().equals("抱歉！登录失败，请稍候再试")) {
					startWebLogin();
				}
                if (sendResultBean.getCode().equals("100000")) {
                    onSendFinished(true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//            	startWebLogin();
                onSendFinished(false);
            }
        });

        setAutoLogInLoginListener(new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                sendWeibo(sendImgData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            	startWebLogin();
            }
        });
    }

    protected void fetchAppSrc() {
        fetchWeiBa(new OnFetchAppSrcListener() {

            @Override
            public void onSuccess(List<WeiboWeiba> appsrcs) {
                for (WeiboWeiba weiboWeiba : appsrcs) {
                    if (mDBmanager.searchAppsrcByCode(weiboWeiba.getCode()) == null) {
                        mDBmanager.insertCategoryTree(0, weiboWeiba.getCode(), weiboWeiba.getText());
                    }
                }
                listView.onRefreshComplete();
                listAdapter.setWeibas(mDBmanager.fetchAllAppsrc());
                hideDialogForWeiBo();
            }

            @Override
            public void onStart() {
                showDialogForWeiBo();
            }

            @Override
            public void onFailure() {
                hideDialogForWeiBo();
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
            if (isKeyBoardShowed) {
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
            }
            if (list.size() == 0) {
                if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
                    fetchAppSrc();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
                }
            } else {
                listAdapter.setWeibas(list);
            }
        }
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
        getAppSrcSharedPreference().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ChangeWeibaActivity.REQUEST) {
            appSrcBtn.setText(getWeiba().getText());
        } else if (resultCode == RESULT_OK && requestCode == ImgFileListActivity.REQUEST_CODE) {
            SendImgData sid = SendImgData.getInstance();
            ArrayList<String> imgs = sid.getSendImgs();
            if (imgs.size() > 0) {
                sendImgTL.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < imgs.size(); i++) {
                ImageView iv = mSelectImageViews.get(i);
                mImageLoader.displayImage("file://" + imgs.get(i), iv, options);
                iv.setVisibility(View.VISIBLE);
                // BitmapWorkerTask mWorkerTask = new BitmapWorkerTask(iv);
                // mWorkerTask.execute(imgs.get(i));
            }

            int offset = imgs.size() % 3;
            int row = offset == 0 ? imgs.size() / 3 : imgs.size() / 3 + 1;
            if (imgs.size() > 0) {
                switch (row) {
                    case 1:
                        mRow001.setVisibility(View.VISIBLE);
                        break;
                    case 2: {
                        mRow001.setVisibility(View.VISIBLE);
                        mRow002.setVisibility(View.VISIBLE);
                        break;
                    }

                    case 3: {
                        mRow001.setVisibility(View.VISIBLE);
                        mRow002.setVisibility(View.VISIBLE);
                        mRow003.setVisibility(View.VISIBLE);
                        break;
                    }

                    default:
                        break;
                }
            }

            for (String s : imgs) {
                Log.d("IMG_", "   " + s + "/////" + mSelectImageViews.size());
            }
        } else if (resultCode == RESULT_OK && requestCode == AT_USER && data != null) {
            String name = data.getStringExtra("name");
            String ori = mEditText.getText().toString();
            int index = mEditText.getSelectionStart();
            StringBuilder stringBuilder = new StringBuilder(ori);
            stringBuilder.insert(index, name);
            mEditText.setText(stringBuilder.toString());
            mEditText.setSelection(index + name.length());
        }
    }

    @Override
    public void onBackPressed() {
        if (mSmileyPicker.isShown()) {
            hideSmileyPicker(false);
        } else {
            super.onBackPressed();
        }
    }

    public void startWebLogin() {
        hideDialogForWeiBo();
        Intent intent = new Intent();
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
        intent.setClass(WriteWeiboWithAppSrcActivity.this, WebViewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLonginWeiboCallback(boolean isSuccess) {
        if (!isSuccess) {
            startWebLogin();
        } else {
            startPicCacheAndSendWeibo();
        }
    }

	private void startPicCacheAndSendWeibo() {
		sendImgData = SendImgData.getInstance();

		ArrayList<String> send = sendImgData.getSendImgs();
		final int count = send.size();

		if (count > 0) {
		    for (int i = 0; i < send.size(); i++) {
		        SendBitmapWorkerTask sendBitmapWorkerTask = new SendBitmapWorkerTask(getApplicationContext(),
		                new OnCacheDoneListener() {
		                    @Override
		                    public void onCacheDone(String newFile) {
		                        // TODO Auto-generated method stub
		                        Log.d(LOGIN_TAG, "has create new File : " + newFile);
		                        sendImgData.addReSizeImg(newFile);
		                        if (sendImgData.getReSizeImgs().size() == count) {
		                            sendWeibo(sendImgData);
		                        }
		                    }
		                });
		        sendBitmapWorkerTask.execute(send.get(i));
		    }
		} else {

		    sendWeibo(sendImgData);
		}
	}

    private void sendWeibo(SendImgData sendImgData) {
        String text = mEditText.getEditableText().toString();
        if (TextUtils.isEmpty(text)) {
            LogTool.D("sendWeibo    text is empty");
            text = getString(R.string.default_text_pic_weibo);
        }

        UserBean userBean = AccountDBTask.getUserBean(mAccountBean.getUid());
        String url = "";
        if (!TextUtils.isEmpty(userBean.getDomain())) {
            url = "weibo.com/" + userBean.getDomain();
        } else {
            url = "weibo.com/u/" + mAccountBean.getUid();
        }
        WaterMark mark = new WaterMark(mAccountBean.getUsernick(), url);

        executeSendWeibo(mAccountBean.getUname(), mAccountBean.getPwd(), mark, getWeiba().getCode(), text,
                sendImgData.getReSizeImgs());
    }

    private boolean checkDataEmpty() {
        if (TextUtils.isEmpty(mEditText.getText().toString()) && SendImgData.getInstance().getSendImgs().size() < 1) {
            return true;
        }
        return false;
    }

    public boolean isMoreThan140(){
        String charSequence = mEditText.getText().toString();
        int count = Utility.length(charSequence);
        return count > 140;
    }
    protected void insertTopic() {
        int currentCursor = mEditText.getSelectionStart();
        Editable editable = mEditText.getText();
        editable.insert(currentCursor, "##");
        mEditText.setSelection(currentCursor + 1);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.menu_topic: {
                insertTopic();
                break;
            }
            case R.id.menu_at: {
                Intent intent = new Intent(WriteWeiboWithAppSrcActivity.this, AtUserActivity.class);
                intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getAccountBean().getAccess_token());
                startActivityForResult(intent, AT_USER);
                break;
            }

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
                    /*
                     * if (isKeyBoardShowed) { imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                     * InputMethodManager.HIDE_NOT_ALWAYS); }
                     */
                    // menu.toggle();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.sendWeiBoBtn: {
            	if (isMoreThan140()) {
            		Toast.makeText(getApplicationContext(), R.string.weibo_text_large_error, Toast.LENGTH_SHORT).show();
            		return;
				}
                if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
                    if (checkDataEmpty()) {
                        mEmptyToast.show();
                    } else {
                        showDialogForWeiBo();
                        startPicCacheAndSendWeibo();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.smileImgButton: {
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (mSmileyPicker.isShown()) {
                            hideSmileyPicker(true);
                        } else {
                            showSmileyPicker(SmileyPickerUtility.isKeyBoardShow(WriteWeiboWithAppSrcActivity.this));
                        }
                        // if (mEmotionRelativeLayout.getVisibility() == View.GONE) {
                        // mEmotionRelativeLayout.setVisibility(View.VISIBLE);
                        // } else {
                        // mEmotionRelativeLayout.setVisibility(View.GONE);
                        // }
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

    private void showSmileyPicker(boolean showAnimation) {
        this.mSmileyPicker.show(this, showAnimation);
    }

    public void hideSmileyPicker(boolean showKeyBoard) {
        if (this.mSmileyPicker.isShown()) {
            if (showKeyBoard) {
                // this time softkeyboard is hidden
                RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) this.mEditText
                        .getLayoutParams();
                localLayoutParams.height = mSmileyPicker.getTop();
                this.mSmileyPicker.hide(this);

                SmileyPickerUtility.showKeyBoard(mEditText);
                mEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // unlockContainerHeightDelayed();
                    }
                }, 200L);
            } else {
                this.mSmileyPicker.hide(this);
                // unlockContainerHeightDelayed();
            }
        }

    }

    @Override
    public void onGlobalLayout() {
        // TODO Auto-generated method stub

        // Rect r = new Rect();
        // mEmotionRelativeLayout.getWindowVisibleDisplayFrame(r);
        //
        // int heightDiff = mEmotionRelativeLayout.getRootView().getHeight() - (r.bottom - r.top);
        // if (heightDiff > 100) {
        // // if more than 100 pixels, its probably a keyboard...
        // Log.d("WEIBO_INPUT", "++++++++");
        // mEmotionRelativeLayout.setVisibility(View.GONE);
        // isKeyBoardShowed = true;
        // } else {
        // Log.d("WEIBO_INPUT", "---------");
        // isKeyBoardShowed = false;
        // }

    }

    class WeiBaCacheFile implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {
            // TODO Auto-generated method stub
            return filename.startsWith("WEI-");
        }

    }

    public void onSendFinished(boolean isSuccess) {
        // TODO Auto-generated method stub
        hideDialogForWeiBo();
        if (isSuccess) {
            Toast.makeText(getApplicationContext(), R.string.send_wei_success, Toast.LENGTH_SHORT).show();
            mEditText.setText("");
            SendImgData sid = SendImgData.getInstance();
            sid.clearSendImgs();
            sid.clearReSizeImgs();
            sendImgTL.setVisibility(View.GONE);
            for (int i = 0; i < 9; i++) {
                mSelectImageViews.get(i).setVisibility(View.INVISIBLE);
            }

            File[] cacheFiles = getExternalCacheDir().listFiles(new WeiBaCacheFile());
            for (File file : cacheFiles) {
                Log.d("LIST_CAXCHE", " " + file.getName());
                file.delete();
            }
            this.finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.send_wei_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO Auto-generated method stub
        super.onSharedPreferenceChanged(sharedPreferences, key);
        appSrcBtn.setText(getWeiba().getText());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WeiboWeiba weiba = ((WeiboWeiba) parent.getItemAtPosition(position));
        Log.d("CLICK", "" + weiba);
        saveWeiba(weiba);
        // menu.toggle();
        mDrawerLayout.closeDrawer(findViewById(R.id.drawerLeft));
    }
}
