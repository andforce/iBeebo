
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.CommentDraftBean;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.data.DataItem;
import org.zarroboogs.weibo.dao.RepostNewMsgDao;
import org.zarroboogs.weibo.db.DraftDBManager;
import org.zarroboogs.weibo.service.SendCommentService;
import org.zarroboogs.weibo.service.SendRepostService;
import org.zarroboogs.weibo.support.utils.Utility;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class WriteCommentActivity extends AbstractWriteActivity<DataItem> {

    public static final String ACTION_DRAFT = "org.zarroboogs.weibo.DRAFT";

    private static final String ACTION_SEND_FAILED = "org.zarroboogs.weibo.SEND_FAILED";

    private static final String ACTION_NOTIFICATION_COMMENT = "org.zarroboogs.weibo.NOTIFICATION_COMMENT";

    private String token;

    private MessageBean msg;

    private CommentDraftBean commentDraftBean;

    private CheckBox enableCommentOri;

    private CheckBox enableRepost;

    private boolean savedEnableCommentOri;

    private boolean savedEnableRepost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            Intent intent = getIntent();
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(WriteCommentActivity.ACTION_DRAFT)) {
                    handleDraftOperation(intent);
                } else if (action.equals(WriteCommentActivity.ACTION_SEND_FAILED)) {
                    handleFailedOperation(intent);
                } else if (action.equals(WriteCommentActivity.ACTION_NOTIFICATION_COMMENT)) {
                    handleNotificationCommentOperation(intent);
                }
            } else {
                handleNormalOperation(intent);
            }
        }

        getSupportActionBar().setTitle(R.string.comments_weibo);

        enableCommentOri = (CheckBox) findViewById(R.id.commentCheckBox);
        enableRepost = (CheckBox) findViewById(R.id.repostCheckBox);

        enableCommentOri.setChecked(savedEnableCommentOri);
        enableRepost.setChecked(savedEnableRepost);

        if (msg != null && msg.getRetweeted_status() != null) {
//            enableCommentOri.setVisible(true);
        }

        mCommentRoot.setVisibility(View.GONE);
        mRepostRoot.setVisibility(View.VISIBLE);
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

    public static Intent startBecauseSendFailed(Context context, AccountBean account, String content, MessageBean oriMsg,
            CommentDraftBean draft,
            boolean comment_ori, String failedReason) {
        Intent intent = new Intent(context, WriteCommentActivity.class);
        intent.setAction(WriteCommentActivity.ACTION_SEND_FAILED);
        intent.putExtra(Constants.ACCOUNT, account);
        intent.putExtra("content", content);
        intent.putExtra("oriMsg", oriMsg);
        intent.putExtra("comment_ori", comment_ori);
        intent.putExtra("failedReason", failedReason);
        intent.putExtra("draft", draft);
        return intent;
    }

    public static Intent newIntentFromNotification(Context context, AccountBean account, MessageBean msg) {
        Intent intent = new Intent(context, WriteCommentActivity.class);
        intent.setAction(WriteCommentActivity.ACTION_NOTIFICATION_COMMENT);
        intent.putExtra(Constants.ACCOUNT, account);
        intent.putExtra("msg", msg);
        return intent;
    }

    private void handleNotificationCommentOperation(Intent intent) {
        AccountBean accountBean = intent.getParcelableExtra(Constants.ACCOUNT);
        token = accountBean.getAccess_token();
        msg = intent.getParcelableExtra("msg");
        getEditTextView().setHint("@" + msg.getUser().getScreen_name() + ":" + msg.getText());
    }

    private void handleFailedOperation(Intent intent) {
        token = ((AccountBean) intent.getParcelableExtra(Constants.ACCOUNT)).getAccess_token();
        msg = getIntent().getParcelableExtra("oriMsg");

        getEditTextView().setError(intent.getStringExtra("failedReason"));
        getEditTextView().setText(intent.getStringExtra("content"));
        commentDraftBean = intent.getParcelableExtra("draft");
        getEditTextView().setHint("@" + msg.getUser().getScreen_name() + ":" + msg.getText());

        savedEnableRepost = intent.getBooleanExtra("comment_ori", false);

    }

    private void handleNormalOperation(Intent intent) {

        token = getIntent().getStringExtra(Constants.TOKEN);
        if (TextUtils.isEmpty(token)) {
            token = BeeboApplication.getInstance().getAccessToken();
        }

        msg = getIntent().getParcelableExtra("msg");
        getEditTextView().setHint("@" + msg.getUser().getScreen_name() + ":" + msg.getText());
    }

    private void handleDraftOperation(Intent intent) {

        token = getIntent().getStringExtra(Constants.TOKEN);
        if (TextUtils.isEmpty(token)) {
            token = BeeboApplication.getInstance().getAccessToken();
        }

        commentDraftBean = getIntent().getParcelableExtra("draft");
        msg = commentDraftBean.getMessageBean();
        getEditTextView().setText(commentDraftBean.getContent());

        getEditTextView().setHint("@" + msg.getUser().getScreen_name() + ":" + msg.getText());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("commentOri", enableCommentOri.isChecked());
        outState.putBoolean("repost", enableRepost.isChecked());
        outState.putString(Constants.TOKEN, token);
        outState.putParcelable("msg", msg);
        outState.putParcelable("commentDraftBean", commentDraftBean);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            savedEnableCommentOri = savedInstanceState.getBoolean("commentOri");
            savedEnableRepost = savedInstanceState.getBoolean("repost");
            token = savedInstanceState.getString(Constants.TOKEN);
            msg = savedInstanceState.getParcelable("msg");
            commentDraftBean = savedInstanceState.getParcelable("commentDraftBean");
        }
    }

    @Override
    protected boolean canShowSaveDraftDialog() {
        if (commentDraftBean == null) {
            return true;
        } else if (!commentDraftBean.getContent().equals(getEditTextView().getText().toString())) {
            return true;
        }
        return false;
    }

    @Override
    public void saveToDraft() {
        if (!TextUtils.isEmpty(getEditTextView().getText().toString())) {
            DraftDBManager.getInstance().insertComment(getEditTextView().getText().toString(), msg,
                    BeeboApplication.getInstance().getCurrentAccountId());
        }
        finish();
    }

    @Override
    protected void removeDraft() {
        if (commentDraftBean != null) {
            DraftDBManager.getInstance().remove(commentDraftBean.getId());
        }
    }

    @Override
    protected boolean canSend() {

        boolean haveContent = !TextUtils.isEmpty(getEditTextView().getText().toString());
        boolean haveToken = !TextUtils.isEmpty(token);
        int sum = Utility.length(getEditTextView().getText().toString());
        int num = 140 - sum;

        boolean contentNumBelow140 = (num >= 0);

        if (haveContent && haveToken && contentNumBelow140) {
            return true;
        } else {
            if (!haveContent && !haveToken) {
                Toast.makeText(this, getString(R.string.content_cant_be_empty_and_dont_have_account), Toast.LENGTH_SHORT)
                        .show();
            } else if (!haveContent) {
                getEditTextView().setError(getString(R.string.content_cant_be_empty));
            } else if (!haveToken) {
                Toast.makeText(this, getString(R.string.dont_have_account), Toast.LENGTH_SHORT).show();
            }

            if (!contentNumBelow140) {
                getEditTextView().setError(getString(R.string.content_words_number_too_many));
            }

        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
			    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		} else if (itemId == R.id.menu_enable_ori_comment) {
			if (enableCommentOri.isChecked()) {
			    enableCommentOri.setChecked(false);
			} else {
			    enableCommentOri.setChecked(true);
			}
		} else if (itemId == R.id.menu_at) {
			Intent intent = AtUserActivity.atUserIntent(this, BeeboApplication.getInstance().getAccessTokenHack());
			startActivityForResult(intent, AT_USER);
		} else if (itemId == R.id.menu_clear) {
			clearContentMenu();
		}
        return true;
    }

    @Override
    protected void send() {
        if (!enableRepost.isChecked()) {
            String content = ((EditText) findViewById(R.id.status_new_content)).getText().toString();
            Intent intent = SendCommentService
                    .newIntent(getCurrentAccountBean(), msg, content, enableCommentOri.isChecked());
            startService(intent);
            finish();

        } else {
            repost();
        }

    }

    /**
     * 1. this message has repost's message 2. this message is an original message
     * <p/>
     * if this message has repost's message,try to include its content, if total word number above
     * 140,discard current msg content
     */

    private void repost() {

        String content = ((EditText) findViewById(R.id.status_new_content)).getText().toString();

        if (msg.getRetweeted_status() != null) {
            String msgContent = "//@" + msg.getUser().getScreen_name() + ": " + msg.getText();
            String total = content + msgContent;
            if (total.length() < 140) {
                content = total;
            }
        }

        boolean comment = true;
        boolean oriComment = enableCommentOri.isChecked();
        String is_comment = "";
        if (comment && oriComment) {
            is_comment = RepostNewMsgDao.ENABLE_COMMENT_ALL;
        } else if (comment) {
            is_comment = RepostNewMsgDao.ENABLE_COMMENT;
        } else if (oriComment) {
            is_comment = RepostNewMsgDao.ENABLE_ORI_COMMENT;
        }

        Intent intent = new Intent(WriteCommentActivity.this, SendRepostService.class);
        intent.putExtra("oriMsg", msg);
        intent.putExtra("content", content);
        intent.putExtra("is_comment", is_comment);
        intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
        intent.putExtra("accountId", BeeboApplication.getInstance().getCurrentAccountId());
        startService(intent);
        finish();

    }

    @Override
    protected AccountBean getCurrentAccountBean() {
        if (WriteCommentActivity.ACTION_NOTIFICATION_COMMENT.equals(getIntent().getAction())
                || WriteCommentActivity.ACTION_SEND_FAILED.equals(getIntent().getAction())) {
            AccountBean accountBean = getIntent().getParcelableExtra(Constants.ACCOUNT);
            return accountBean;
        } else {
            return super.getCurrentAccountBean();
        }
    }
}
