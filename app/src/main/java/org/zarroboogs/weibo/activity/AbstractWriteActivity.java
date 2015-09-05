
package org.zarroboogs.weibo.activity;

import org.zarroboogs.keyboardlayout.KeyboardRelativeLayout;
import org.zarroboogs.keyboardlayout.OnKeyboardStateChangeListener;
import org.zarroboogs.keyboardlayout.smilepicker.SmileyPicker;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.TextNumLimitWatcher;
import org.zarroboogs.weibo.adapter.AutoCompleteAdapter;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.dialogfragment.ClearContentDialog;
import org.zarroboogs.weibo.dialogfragment.SaveDraftDialog;
import org.zarroboogs.weibo.support.lib.CheatSheet;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class AbstractWriteActivity<T> extends AbstractAppActivity implements OnClickListener,
        ClearContentDialog.IClear, SaveDraftDialog.IDraft {

    protected abstract boolean canSend();

    private AutoCompleteTextView et;

    private SmileyPicker smiley = null;

    private RelativeLayout container = null;

    public static final int AT_USER = 3;

    protected String token;

    private Toolbar abstractWriteToolbar;

    public RelativeLayout mCommentRoot;
    public RelativeLayout mRepostRoot;

    public KeyboardRelativeLayout mRootKeyboardLayout;

    private boolean isSmileClicked = false;

    protected EditText getEditTextView() {
        return et;
    }

    @Override
    public void clear() {
        getEditTextView().setText("");
    }

    protected abstract void send();

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public Toolbar getToolbar() {
        return abstractWriteToolbar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abstractwriteactivity_layout);

        mRootKeyboardLayout = ViewUtility.findViewById(this, R.id.root_layout);

        mCommentRoot = (RelativeLayout) findViewById(R.id.commentRoot);
        mRepostRoot = (RelativeLayout) findViewById(R.id.repostRoot);

        abstractWriteToolbar = (Toolbar) findViewById(R.id.abstractWriteToolbar);
        setSupportActionBar(abstractWriteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        abstractWriteToolbar.setNavigationOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        token = getIntent().getStringExtra(Constants.TOKEN);

        View title = getLayoutInflater().inflate(R.layout.writeweiboactivity_title_layout, null);
        // actionBar.setCustomView(title, new ActionBar.LayoutParams(Gravity.RIGHT));

        et = ((AutoCompleteTextView) findViewById(R.id.status_new_content));
        et.addTextChangedListener(new TextNumLimitWatcher((TextView) findViewById(R.id.menu_send), et, this));
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, et,
                (ProgressBar) title.findViewById(R.id.have_suggest_progressbar));
        et.setAdapter(adapter);

        findViewById(R.id.menu_topic).setOnClickListener(this);
        findViewById(R.id.menu_at).setOnClickListener(this);
        findViewById(R.id.menu_emoticon).setOnClickListener(this);
        findViewById(R.id.menu_send).setOnClickListener(this);

        CheatSheet.setup(AbstractWriteActivity.this, findViewById(R.id.menu_at), R.string.at_other);
        CheatSheet.setup(AbstractWriteActivity.this, findViewById(R.id.menu_emoticon), R.string.add_emoticon);
        CheatSheet.setup(AbstractWriteActivity.this, findViewById(R.id.menu_topic), R.string.add_topic);
        CheatSheet.setup(AbstractWriteActivity.this, findViewById(R.id.menu_send), R.string.send);

        smiley = (SmileyPicker) findViewById(R.id.smiley_picker);
        smiley.setEditText(et);
        container = (RelativeLayout) findViewById(R.id.container);


        mRootKeyboardLayout.setOnKeyboardStateListener(new OnKeyboardStateChangeListener() {
            @Override
            public void onKeyBoardShow(int height) {
                if (isSmileClicked) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) et.getLayoutParams();
                    params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                    container.requestLayout();
                }
            }

            @Override
            public void onKeyBoardHide() {
                if (isSmileClicked) {
                    showViewWithAnim(smiley);
                }
            }
        });
    }

    private void showViewWithAnim(View view) {
        smiley.setVisibility(View.VISIBLE);

        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(150);

        view.startAnimation(animation);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.menu_emoticon) {
            isSmileClicked = true;

            if (mRootKeyboardLayout.getKeyBoardHelper().isKeyboardShow()) {

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
                params.height = container.getHeight();
                container.requestLayout();

                mRootKeyboardLayout.getKeyBoardHelper().hideKeyboard();

            } else {
                mRootKeyboardLayout.getKeyBoardHelper().showKeyboard(et);
            }
        } else if (id == R.id.menu_send) {
            send();
        } else if (id == R.id.menu_topic) {
            insertTopic();
        } else if (id == R.id.menu_at) {
            Intent intent = AtUserActivity.atUserIntent(this, BeeboApplication.getInstance().getAccessTokenHack());
            startActivityForResult(intent, AT_USER);
        }
    }

    protected void insertTopic() {
        String ori = getEditTextView().getText().toString();
        String topicTag = "##";
        getEditTextView().setText(ori + topicTag);
        getEditTextView().setSelection(et.getText().toString().length() - 1);
    }

    protected void clearContentMenu() {
        ClearContentDialog dialog = new ClearContentDialog();
        dialog.show(getFragmentManager(), "");
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
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
                params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                container.setLayoutParams(params);
                smiley.setVisibility(View.GONE);
                smiley.requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);

    }

    @Override
    public void onBackPressed() {
        if (smiley.isShown()) {
            removeViewWithAnim(smiley);
        } else if (!TextUtils.isEmpty(et.getText().toString()) && canShowSaveDraftDialog()) {
            SaveDraftDialog dialog = new SaveDraftDialog();
            dialog.show(getFragmentManager(), "");
        } else {

            if (BeeboApplication.getInstance().getAccountBean().equals(getCurrentAccountBean())) {
                super.onBackPressed();
            } else {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                Intent intent = MainTimeLineActivity.newIntent(getCurrentAccountBean());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
    }

    protected AccountBean getCurrentAccountBean() {
        return BeeboApplication.getInstance().getAccountBean();
    }

    protected abstract boolean canShowSaveDraftDialog();

    public abstract void saveToDraft();

    protected abstract void removeDraft();

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AT_USER:
                    String name = intent.getStringExtra("name");
                    String ori = getEditTextView().getText().toString();
                    int index = getEditTextView().getSelectionStart();
                    StringBuilder stringBuilder = new StringBuilder(ori);
                    stringBuilder.insert(index, name);
                    getEditTextView().setText(stringBuilder.toString());
                    getEditTextView().setSelection(index + name.length());
                    break;
            }

        }
    }

}
