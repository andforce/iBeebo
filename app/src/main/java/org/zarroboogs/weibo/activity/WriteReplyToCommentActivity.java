
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.CommentBean;
import org.zarroboogs.weibo.bean.ReplyDraftBean;
import org.zarroboogs.weibo.db.DraftDBManager;
import org.zarroboogs.weibo.service.SendReplyToCommentService;
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

public class WriteReplyToCommentActivity extends AbstractWriteActivity<CommentBean> {

    public static final String ACTION_DRAFT = "org.zarroboogs.weibo.DRAFT";

    private static final String ACTION_SEND_FAILED = "org.zarroboogs.weibo.SEND_FAILED";

    private static final String ACTION_NOTIFICATION_REPLY = "org.zarroboogs.weibo.NOTIFICATION_REPLY";

    private CommentBean bean;

    private ReplyDraftBean replyDraftBean;

    private CheckBox enableRepost;

    private boolean savedEnableRepost;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            Intent intent = getIntent();
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(WriteReplyToCommentActivity.ACTION_DRAFT)) {
                    handleDraftOperation(intent);
                } else if (action.equals(WriteReplyToCommentActivity.ACTION_SEND_FAILED)) {
                    handleFailedOperation(intent);
                } else if (action.equals(WriteReplyToCommentActivity.ACTION_NOTIFICATION_REPLY)) {
                    handleNotificationReplyOperation(intent);
                }
            } else {
                handleNormalOperation(intent);
            }
        }

        enableRepost = (CheckBox) findViewById(R.id.repostCheckBox);
        enableRepost.setChecked(savedEnableRepost);
        
        mRepostRoot.setVisibility(View.VISIBLE);
        mCommentRoot.setVisibility(View.GONE);
        
        disPlayHomeAsUp(getToolbar());
        
        getSupportActionBar().setTitle(R.string.reply_to_comment);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BEAN, bean);
        outState.putParcelable("replyDraftBean", replyDraftBean);
        outState.putBoolean("repost", enableRepost.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            savedEnableRepost = savedInstanceState.getBoolean("repost", false);
            bean = savedInstanceState.getParcelable(Constants.BEAN);
            replyDraftBean = savedInstanceState.getParcelable("replyDraftBean");
        }
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // // getMenuInflater().inflate(R.menu.actionbar_menu_commentnewactivity, menu);
    // menu.findItem(R.id.menu_enable_ori_comment).setVisible(false);
    // menu.findItem(R.id.menu_enable_repost).setVisible(true);
    // enableRepost = menu.findItem(R.id.menu_enable_repost);
    // enableRepost.setChecked(savedEnableRepost);
    // return true;
    // }

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

    public static Intent startBecauseSendFailed(Context context, AccountBean account, String content, CommentBean oriMsg,
            ReplyDraftBean replyDraftBean,
            String repostContent, String failedReason) {
        Intent intent = new Intent(context, WriteReplyToCommentActivity.class);
        intent.setAction(WriteReplyToCommentActivity.ACTION_SEND_FAILED);
        intent.putExtra(Constants.ACCOUNT, account);
        intent.putExtra("content", content);
        intent.putExtra("oriMsg", oriMsg);
        intent.putExtra("failedReason", failedReason);
        intent.putExtra("repostContent", repostContent);
        intent.putExtra("replyDraftBean", replyDraftBean);
        return intent;
    }

    public static Intent newIntentFromNotification(Context context, AccountBean account, CommentBean oriMsg) {
        Intent intent = new Intent(context, WriteReplyToCommentActivity.class);
        intent.setAction(WriteReplyToCommentActivity.ACTION_NOTIFICATION_REPLY);
        intent.putExtra(Constants.ACCOUNT, account);
        intent.putExtra("oriMsg", oriMsg);
        return intent;
    }

    private void handleFailedOperation(Intent intent) {
        token = ((AccountBean) intent.getParcelableExtra(Constants.ACCOUNT)).getAccess_token();
        bean = getIntent().getParcelableExtra("oriMsg");
        getEditTextView().setHint("@" + bean.getUser().getScreen_name() + ":" + bean.getText());
        getEditTextView().setError(intent.getStringExtra("failedReason"));
        getEditTextView().setText(intent.getStringExtra("content"));
        replyDraftBean = intent.getParcelableExtra("replyDraftBean");
        if (!TextUtils.isEmpty(intent.getStringExtra("repostContent"))) {
            savedEnableRepost = true;
        }
    }

    private void handleNotificationReplyOperation(Intent intent) {
        token = ((AccountBean) intent.getParcelableExtra(Constants.ACCOUNT)).getAccess_token();
        bean = getIntent().getParcelableExtra("oriMsg");
        getEditTextView().setHint("@" + bean.getUser().getScreen_name() + ":" + bean.getText());
        if (!TextUtils.isEmpty(intent.getStringExtra("repostContent"))) {
            savedEnableRepost = true;
        }
    }

    private void handleNormalOperation(Intent intent) {

        token = intent.getStringExtra(Constants.TOKEN);
        if (TextUtils.isEmpty(token)) {
            token = BeeboApplication.getInstance().getAccessToken();
        }

        bean = intent.getParcelableExtra("msg");
        getEditTextView().setHint("@" + bean.getUser().getScreen_name() + ":" + bean.getText());
    }

    private void handleDraftOperation(Intent intent) {
        token = intent.getStringExtra(Constants.TOKEN);
        if (TextUtils.isEmpty(token)) {
            token = BeeboApplication.getInstance().getAccessToken();
        }

        replyDraftBean = intent.getParcelableExtra("draft");
        getEditTextView().setText(replyDraftBean.getContent());
        bean = replyDraftBean.getCommentBean();
        getEditTextView().setHint("@" + bean.getUser().getScreen_name() + ":" + bean.getText());
    }

    @Override
    protected boolean canShowSaveDraftDialog() {
        if (replyDraftBean == null) {
            return true;
        } else if (!replyDraftBean.getContent().equals(getEditTextView().getText().toString())) {
            return true;
        }
        return false;
    }

    @Override
    public void saveToDraft() {
        if (!TextUtils.isEmpty(getEditTextView().getText().toString())) {
            DraftDBManager.getInstance().insertReply(getEditTextView().getText().toString(), bean,
                    BeeboApplication.getInstance().getCurrentAccountId());
        }
        finish();
    }

    @Override
    protected void removeDraft() {
        if (replyDraftBean != null) {
            DraftDBManager.getInstance().remove(replyDraftBean.getId());
        }
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        String contentStr = getEditTextView().getText().toString();
//        if (!TextUtils.isEmpty(contentStr)) {
//            menu.findItem(R.id.menu_clear).setVisible(true);
//        } else {
//            menu.findItem(R.id.menu_clear).setVisible(false);
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
			    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
		} else if (itemId == R.id.menu_at) {
			Intent intent = AtUserActivity.atUserIntent(this, BeeboApplication.getInstance().getAccessTokenHack());
			intent.putExtra(Constants.TOKEN, token);
			startActivityForResult(intent, AT_USER);
		} else if (itemId == R.id.menu_clear) {
			clearContentMenu();
		}
        return true;
    }

    @Override
    protected void send() {
        if (canSend()) {
            String content = ((EditText) findViewById(R.id.status_new_content)).getText().toString();

            Intent intent = SendReplyToCommentService.newIntent(getCurrentAccountBean(), bean, content,
                    enableRepost.isChecked() ? repost() : null);
            startService(intent);
            finish();
        }
    }

    private String repost() {

        String content = ((EditText) findViewById(R.id.status_new_content)).getText().toString();
        String msgContent = "//@" + bean.getUser().getScreen_name() + ": " + bean.getText();
        String total = content + msgContent;
        if (total.length() < 140) {
            content = total;
        }

        return content;
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
    protected AccountBean getCurrentAccountBean() {
        if (WriteReplyToCommentActivity.ACTION_NOTIFICATION_REPLY.equals(getIntent().getAction())
                || WriteReplyToCommentActivity.ACTION_SEND_FAILED.equals(getIntent().getAction())) {
            AccountBean accountBean = getIntent().getParcelableExtra(Constants.ACCOUNT);
            return accountBean;
        } else {
            return super.getCurrentAccountBean();
        }
    }
}
