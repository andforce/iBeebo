
package org.zarroboogs.weibo.activity;

import java.util.ArrayList;

import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.keyboardlayout.KeyboardRelativeLayout;
import org.zarroboogs.keyboardlayout.OnKeyboardStateChangeListener;
import org.zarroboogs.keyboardlayout.smilepicker.SmileyPicker;
import org.zarroboogs.util.net.LoginWeiboAsyncTask.LoginWeiboCallack;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.Utility;
import org.zarroboogs.utils.WeiBaNetUtils;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.GeoBean;
import org.zarroboogs.weibo.bean.StatusDraftBean;
import org.zarroboogs.weibo.bean.WeiboWeiba;
import org.zarroboogs.weibo.selectphoto.ImgFileListActivity;
import org.zarroboogs.weibo.selectphoto.SendImgData;
import org.zarroboogs.weibo.service.SendWeiboService;
import org.zarroboogs.weibo.service.SendWithAppSrcServices;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.ViewUtility;
import org.zarroboogs.weibo.widget.galleryview.ViewPagerActivity;

import com.rengwuxian.materialedittext.MaterialEditText;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WriteWeiboWithAppSrcActivity extends BaseLoginActivity implements LoginWeiboCallack, OnClickListener,
        OnItemClickListener, OnSharedPreferenceChangeListener {

    public static final int AT_USER = 0x1000;

    protected static final String TAG = "WeiboMainActivity  ";

    public static final String ACTION_SEND_FAILED = "action_send_failed";

    private SmileyPicker mSmileyPicker;

    private InputMethodManager imm = null;

    private MaterialEditText mEditText;

    private AccountBean mAccountBean;

    private boolean isSmileClicked = false;

    private TextView weiTextCountTV;

    private Toast mEmptyToast;

    private GridView mNinePicGridView;

    private NinePicGriViewAdapter mNinePicAdapter;

    private KeyboardRelativeLayout keyboardLayout;

    private RelativeLayout mContentRelativeLayout;

    public static Intent startBecauseSendFailed(Context context, AccountBean accountBean, String content, String picPath,
                                                GeoBean geoBean,
                                                StatusDraftBean statusDraftBean, String failedReason) {
        Intent intent = new Intent(context, WriteWeiboWithAppSrcActivity.class);
        intent.setAction(ACTION_SEND_FAILED);
        intent.putExtra(Constants.ACCOUNT, accountBean);
        intent.putExtra("content", content);
        intent.putExtra("failedReason", failedReason);
        intent.putExtra("picPath", picPath);
        intent.putExtra("geoBean", geoBean);
        intent.putExtra("statusDraftBean", statusDraftBean);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSPs().registerOnSharedPreferenceChangeListener(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.write_weibo_with_appsrc_activity_layout);

        keyboardLayout = ViewUtility.findViewById(this, R.id.keyboardLayout);
        keyboardLayout.setOnKeyboardStateListener(new OnKeyboardStateChangeListener() {
            @Override
            public void onKeyBoardShow(int height) {
                DevLog.printLog("keyboardLayout", "onKeyBoardShow: " + height);
                mNinePicGridView.setVisibility(View.GONE);
                if (isSmileClicked) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContentRelativeLayout.getLayoutParams();
                    params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                    mContentRelativeLayout.requestLayout();
                }

            }

            @Override
            public void onKeyBoardHide() {
                DevLog.printLog("keyboardLayout", "onKeyBoardHide");

                if (isSmileClicked) {
                    mNinePicGridView.setVisibility(View.GONE);
                    showViewWithAnim(mSmileyPicker);
                } else {
                    mNinePicGridView.setVisibility(View.VISIBLE);
                }


            }
        });


        // drawerLayout
        disPlayHomeAsUp((Toolbar) findViewById(R.id.writeWeiboToolBar));


        mAccountBean = getIntent().getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        String atContent = getIntent().getStringExtra("content");

        mEmptyToast = Toast.makeText(getApplicationContext(), R.string.text_is_empty, Toast.LENGTH_SHORT);

        mContentRelativeLayout = ViewUtility.findViewById(this, R.id.contentRelativeLayout);

        weiTextCountTV = (TextView) findViewById(R.id.weiTextCountTV);

        mNinePicAdapter = new NinePicGriViewAdapter(getApplicationContext());
        mNinePicGridView = (GridView) findViewById(R.id.ninePicGridView);
        mNinePicGridView.setAdapter(mNinePicAdapter);

        mNinePicGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(WriteWeiboWithAppSrcActivity.this, ViewPagerActivity.class);
                intent.putStringArrayListExtra(ViewPagerActivity.IMG_LIST, SendImgData.getInstance().getSendImgs());
                intent.putExtra(ViewPagerActivity.IMG_ID, position);
                startActivity(intent);

            }
        });


        ImageButton mSelectPhoto;
        ImageButton mSendBtn;
        ImageButton smileButton;
        ImageButton mTopicBtn;
        ImageButton mAtButton;

        mSelectPhoto = (ImageButton) findViewById(R.id.imageButton1);
        mEditText = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.weiboContentET);
        mSmileyPicker = (SmileyPicker) findViewById(R.id.smileLayout_ref);
        mSmileyPicker.setEditText(mEditText);


        mTopicBtn = (ImageButton) findViewById(R.id.menu_topic);
        mAtButton = (ImageButton) findViewById(R.id.menu_at);

        smileButton = (ImageButton) findViewById(R.id.smileImgButton);
        mSendBtn = (ImageButton) findViewById(R.id.sendWeiBoBtn);

        mTopicBtn.setOnClickListener(this);
        mAtButton.setOnClickListener(this);

        // findAllEmotionImageView((ViewGroup) findViewById(R.id.emotionTL));
        mSelectPhoto.setOnClickListener(this);
        smileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                isSmileClicked = true;

                if (keyboardLayout.getKeyBoardHelper().isKeyboardShow()) {

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContentRelativeLayout.getLayoutParams();
                    params.height = mContentRelativeLayout.getHeight();
                    mContentRelativeLayout.requestLayout();

                    keyboardLayout.getKeyBoardHelper().hideKeyboard();

                } else {
                    keyboardLayout.getKeyBoardHelper().showKeyboard(mEditText);
                }
            }
        });
        mSendBtn.setOnClickListener(this);
        mEditText.addTextChangedListener(watcher);

        if (!TextUtils.isEmpty(atContent)) {
            mEditText.setText(atContent + " ");
            mEditText.setSelection(mEditText.getEditableText().toString().length());
        }


        Intent intent = getIntent();
        if (ACTION_SEND_FAILED.equals(intent.getAction())) {
            mEditText.setText(intent.getStringExtra("content"));
            String picUrl = intent.getStringExtra("picPath");
            if (!TextUtils.isEmpty(picUrl)) {
                SendImgData.getInstance().clearSendImgs();

                SendImgData.getInstance().addSendImg(picUrl);
                refreshNineGridView();
            }

            mAccountBean = intent.getParcelableExtra(Constants.ACCOUNT);
        }

    }


    private void showViewWithAnim(View view) {
        mSmileyPicker.setVisibility(View.VISIBLE);

        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(150);
//        animation.setFillAfter(true);

        view.startAnimation(animation);

    }


    @Override
    protected void onResume() {
        super.onResume();

        refreshNineGridView();
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSPs().unregisterOnSharedPreferenceChangeListener(this);

        SendImgData.getInstance().clearSendImgs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ImgFileListActivity.REQUEST_CODE) {
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
//            keyboardLayout.getKeyBoardHelper().hideKeyboard();
            removeViewWithAnim(mSmileyPicker);
        } else {
            super.onBackPressed();
        }
    }

    private void removeViewWithAnim(View view) {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        animation.setDuration(200);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContentRelativeLayout.getLayoutParams();
                params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                mContentRelativeLayout.setLayoutParams(params);
                mSmileyPicker.setVisibility(View.GONE);
                mSmileyPicker.requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);

    }

    @Override
    public void onLonginWeiboCallback(boolean isSuccess) {
        if (!isSuccess) {
            startWebLogin();
        }
    }


    private String getWeiboTextContent() {
        String text = mEditText.getEditableText().toString();
        if (TextUtils.isEmpty(text)) {
            text = getString(R.string.default_text_pic_weibo);
        }
        return text;
    }

    private boolean checkDataEmpty() {
        return TextUtils.isEmpty(mEditText.getText().toString()) && SendImgData.getInstance().getSendImgs().size() < 1;
    }

    public boolean isMoreThan140() {
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
            Intent intent = AtUserActivity.atUserIntent(this, BeeboApplication.getInstance().getAccessTokenHack());
            startActivityForResult(intent, AT_USER);
        } else if (id == R.id.sendWeiBoBtn) {
            if (isMoreThan140()) {
                Toast.makeText(getApplicationContext(), R.string.weibo_text_large_error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
                if (checkDataEmpty()) {
                    mEmptyToast.show();
                } else {

                    ArrayList<String> send = SendImgData.getInstance().getSendImgs();

                    if (send.size() > 1) {
                        Intent intent = new Intent(getApplicationContext(), SendWithAppSrcServices.class);
                        intent.putExtra(SendWithAppSrcServices.APP_SRC, getWeiba());
                        intent.putExtra(SendWithAppSrcServices.TEXT_CONTENT, getWeiboTextContent());
                        startService(intent);
                        finish();
                    } else {
                        String charSequence = getWeiboTextContent();
                        if (send.size() > 0) {
                            executeTask(charSequence, send.get(0));
                        } else {
                            executeTask(charSequence, null);
                        }
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.smileImgButton) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        } else if (id == R.id.imageButton1) {
            Intent mIntent = new Intent(getApplicationContext(), ImgFileListActivity.class);
            startActivityForResult(mIntent, ImgFileListActivity.REQUEST_CODE);
        }

    }

    protected void executeTask(String contentString, String picPath) {
        Intent intent = new Intent(WriteWeiboWithAppSrcActivity.this, SendWeiboService.class);
        intent.putExtra(Constants.TOKEN, mAccountBean.getAccess_token());
        if (picPath != null) {
            intent.putExtra("picPath", picPath);
        }
        intent.putExtra(Constants.ACCOUNT, mAccountBean);
        intent.putExtra("content", contentString);
//        intent.putExtra("geo", null);
//        intent.putExtra("draft", statusDraftBean);
        startService(intent);
        finish();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WeiboWeiba weiba = ((WeiboWeiba) parent.getItemAtPosition(position));
        Log.d("CLICK", "" + weiba);
        saveWeiba(weiba);
        // menu.toggle();
    }
}
