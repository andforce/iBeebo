
package org.zarroboogs.weibo.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.zarroboogs.utils.WeiBoURLs;
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
import org.zarroboogs.weibo.hot.hean.HotCardBean;
import org.zarroboogs.weibo.hot.hean.HotMblogBean;
import org.zarroboogs.weibo.hot.hean.HotWeiboBean;
import org.zarroboogs.weibo.selectphoto.ImgFileListActivity;
import org.zarroboogs.weibo.selectphoto.SendImgData;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.SmileyPickerUtility;
import org.zarroboogs.weibo.widget.SmileyPicker;
import org.zarroboogs.weibo.widget.galleryview.ViewPagerActivity;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshListView;

import com.crashlytics.android.internal.s;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

    AccountBean mAccountBean;

    private boolean isKeyBoardShowed = false;
    private ScrollView mEditPicScrollView;

    private TextView weiTextCountTV;

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

    private GridView mNinePicGridView;
    private NinePicGriViewAdapter mNinePicAdapter;
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
        
        mNinePicAdapter = new NinePicGriViewAdapter(getApplicationContext());
        mNinePicGridView = (GridView) findViewById(R.id.ninePicGridView);
        mNinePicGridView.setAdapter(mNinePicAdapter);

        mNinePicGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
					Intent intent = new Intent(WriteWeiboWithAppSrcActivity.this, ViewPagerActivity.class);
					intent.putStringArrayListExtra(ViewPagerActivity.IMG_LIST, SendImgData.getInstance().getSendImgs());
					intent.putExtra(ViewPagerActivity.IMG_ID, position);
					startActivity(intent);
					
			}
		});

        appSrcBtn = (Button) findViewById(R.id.appSrcBtn);
        appSrcBtn.setText(getWeiba().getText());

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
    
    String test = "";

    protected void fetchAppSrc() {
    	if (true) {
        	String json = readStringFromAssert().replaceAll("\"geo\": \"\"", "\"geo\": {}");
        	org.zarroboogs.weibo.support.utils.Utility.printLongLog("READ_JSON_DONE", json);
        	Gson gson = new Gson();
//	    	HotWeiboBean result = gson.fromJson(json, new TypeToken<HotWeiboBean>() {}.getType());
	    	HotWeiboBean result = gson.fromJson(json, HotWeiboBean.class);
	    	Log.d("===========after_READ_JSON_DONE:", "-----------"+ result.getCardlistInfo().getDesc());
		}

    	
		if (false) {
	    	getAsyncHttpClient().get(WeiBoURLs.hotWeiboUrl("4upDb8fe3jr9RGyZmP1OG7SC21d", 2), new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					// TODO Auto-generated method stub
					
					String json = new String(responseBody).replace("", "");
					org.zarroboogs.weibo.support.utils.Utility.printLongLog("READ_JSON_DONE", json);
					
					Gson gson = new Gson();
			    	HotWeiboBean result = gson.fromJson(json, new TypeToken<HotWeiboBean>() {}.getType());
			    	Log.d("===========after_READ_JSON_DONE:", "-----------"+ result.getCardlistInfo().getDesc());
					List<HotCardBean> cardBeans = result.getCards();
					Log.d("===========after_READ_JSON_DONE:", "-----------" + "Cards Size: " + cardBeans.size());
					
					List<HotMblogBean> hotMblogBeans = new ArrayList<HotMblogBean>();
					for (HotCardBean i : cardBeans) {
						HotMblogBean blog = i.getMblog();
						if (blog != null) {
							hotMblogBeans.add(blog);
						}
					}
			            
					for (HotMblogBean i : hotMblogBeans) {
						Log.d("===========after_READ_JSON_DONE:", i.getUser().getId());
					}
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					// TODO Auto-generated method stub
					
				}
			});
		}

		fetchWeiBa(new OnFetchAppSrcListener() {

			@Override
			public void onSuccess(List<WeiboWeiba> appsrcs) {
				for (WeiboWeiba weiboWeiba : appsrcs) {
					if (mDBmanager.searchAppsrcByCode(weiboWeiba.getCode()) == null) {
						mDBmanager.insertCategoryTree(0, weiboWeiba.getCode(),
								weiboWeiba.getText());
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

	private String readStringFromAssert() {
		InputStream json = null;
		try {
			json = getAssets().open("test_error_2.json");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		InputStreamReader read = new InputStreamReader(json);// 考虑到编码格式
		BufferedReader bufferedReader = new BufferedReader(read);
		StringBuffer sb = new StringBuffer();
		String lineTxt = "";
		try {
			while ((lineTxt = bufferedReader.readLine()) != null) {
				sb.append(lineTxt);
			}
			read.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
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
        
        refreshNineGridView();
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
        
        SendImgData.getInstance().clearSendImgs();
        SendImgData.getInstance().clearReSizeImgs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ChangeWeibaActivity.REQUEST) {
            appSrcBtn.setText(getWeiba().getText());
        } else if (resultCode == RESULT_OK && requestCode == ImgFileListActivity.REQUEST_CODE) {
            refreshNineGridView();

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

	private void refreshNineGridView() { 
		mNinePicAdapter.notifyDataSetChanged();
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
		sendImgData.clearReSizeImgs();

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
		                        	Log.d(LOGIN_TAG, "Create File Finished start send Weibo ");
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
        int id = v.getId();
		if (id == R.id.menu_topic) {
			insertTopic();
		} else if (id == R.id.menu_at) {
			Intent intent = new Intent(WriteWeiboWithAppSrcActivity.this, AtUserActivity.class);
			intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getAccountBean().getAccess_token());
			startActivityForResult(intent, AT_USER);
		} else if (id == R.id.editTextLayout) {
			mEditText.performClick();
		} else if (id == R.id.scrollView1) {
			mEditText.performClick();
		} else if (id == R.id.appSrcBtn) {
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
		} else if (id == R.id.sendWeiBoBtn) {
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
		} else if (id == R.id.smileImgButton) {
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
		} else if (id == R.id.imageButton1) {
			Intent mIntent = new Intent(getApplicationContext(), ImgFileListActivity.class);
			startActivityForResult(mIntent, ImgFileListActivity.REQUEST_CODE);
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
