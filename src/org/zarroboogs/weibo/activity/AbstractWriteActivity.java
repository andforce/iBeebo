
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.ImageUtility;
import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.TextNumLimitWatcher;
import org.zarroboogs.weibo.adapter.AutoCompleteAdapter;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.dialogfragment.ClearContentDialog;
import org.zarroboogs.weibo.dialogfragment.SaveDraftDialog;
import org.zarroboogs.weibo.support.lib.CheatSheet;
import org.zarroboogs.weibo.support.utils.SmileyPickerUtility;
import org.zarroboogs.weibo.widget.SmileyPicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * User: qii Date: 12-9-25
 */
public abstract class AbstractWriteActivity<T> extends AbstractAppActivity implements View.OnClickListener,
        ClearContentDialog.IClear, SaveDraftDialog.IDraft {

    protected abstract boolean canSend();

    private AutoCompleteTextView et;

    private SmileyPicker smiley = null;

    private RelativeLayout container = null;

    public static final int AT_USER = 3;

    protected String token;

    private Toolbar abstractWriteToolbar;

    protected EditText getEditTextView() {
        return et;
    }

    @Override
    public void clear() {
        getEditTextView().setText("");
    }

    protected abstract void send();

    public void insertEmotion(String emotionChar) {
        String ori = getEditTextView().getText().toString();
        int index = getEditTextView().getSelectionStart();
        StringBuilder stringBuilder = new StringBuilder(ori);
        stringBuilder.insert(index, emotionChar);
        getEditTextView().setText(stringBuilder.toString());
        getEditTextView().setSelection(index + emotionChar.length());
    }

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

        abstractWriteToolbar = (Toolbar) findViewById(R.id.abstractWriteToolbar);
        // ActionBar actionBar = getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(false);
        // actionBar.setDisplayShowHomeEnabled(true);
        // actionBar.setDisplayShowTitleEnabled(true);
        // actionBar.setDisplayShowCustomEnabled(true);

        int avatarWidth = getResources().getDimensionPixelSize(R.dimen.timeline_avatar_width);
        int avatarHeight = getResources().getDimensionPixelSize(R.dimen.timeline_avatar_height);

        Bitmap bitmap = ImageUtility.getWriteWeiboRoundedCornerPic(getCurrentAccountBean().getInfo().getAvatar_large(),
                avatarWidth, avatarHeight,
                FileLocationMethod.avatar_large);
        if (bitmap == null) {
            bitmap = ImageUtility.getWriteWeiboRoundedCornerPic(getCurrentAccountBean().getInfo().getProfile_image_url(),
                    avatarWidth, avatarHeight,
                    FileLocationMethod.avatar_small);
        }
        if (bitmap != null) {
            // actionBar.setIcon(new BitmapDrawable(getResources(), bitmap));
        }

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
        smiley.setEditText(AbstractWriteActivity.this, ((RelativeLayout) findViewById(R.id.root_layout)), et);
        container = (RelativeLayout) findViewById(R.id.container);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSmileyPicker(true);
            }
        });
    }

    private void showSmileyPicker(boolean showAnimation) {
        lockContainerHeight(SmileyPickerUtility.getAppContentHeight(AbstractWriteActivity.this));
        this.smiley.show(AbstractWriteActivity.this, showAnimation);
        

    }

    public void hideSmileyPicker(boolean showKeyBoard) {
        if (this.smiley.isShown()) {
            if (showKeyBoard) {
                // this time softkeyboard is hidden
                RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) this.container.getLayoutParams();
                localLayoutParams.height = smiley.getTop();
                
                this.smiley.hide(AbstractWriteActivity.this);

                SmileyPickerUtility.showKeyBoard(et);
                et.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        unlockContainerHeightDelayed();
                    }
                }, 200L);
            } else {
                this.smiley.hide(AbstractWriteActivity.this);
                unlockContainerHeightDelayed();
            }
        }

    }

    private void lockContainerHeight(int paramInt) {
        RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) this.container.getLayoutParams();
        localLayoutParams.height = paramInt;
//        localLayoutParams.weight = 0.0F;
    }

    public void unlockContainerHeightDelayed() {

        ((RelativeLayout.LayoutParams) AbstractWriteActivity.this.container.getLayoutParams()).height = RelativeLayout.LayoutParams.MATCH_PARENT;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_emoticon:
                new Handler().post(new Runnable() {
                    
                    @Override
                    public void run() {
                        if (smiley.isShown()) {
                            hideSmileyPicker(true);
                        } else {
                            showSmileyPicker(SmileyPickerUtility.isKeyBoardShow(AbstractWriteActivity.this));
                        }
                    }
                });

                break;

            case R.id.menu_send:
                send();
                break;
            case R.id.menu_topic:
                insertTopic();
                break;
            case R.id.menu_at:
                Intent intent = new Intent(AbstractWriteActivity.this, AtUserActivity.class);
                intent.putExtra(Constants.TOKEN, token);
                startActivityForResult(intent, AT_USER);
                break;
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

    @Override
    public void onBackPressed() {
        if (smiley.isShown()) {
            hideSmileyPicker(false);
        } else if (!TextUtils.isEmpty(et.getText().toString()) && canShowSaveDraftDialog()) {
            SaveDraftDialog dialog = new SaveDraftDialog();
            dialog.show(getFragmentManager(), "");
        } else {

            if (GlobalContext.getInstance().getAccountBean().equals(getCurrentAccountBean())) {
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
        return GlobalContext.getInstance().getAccountBean();
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
