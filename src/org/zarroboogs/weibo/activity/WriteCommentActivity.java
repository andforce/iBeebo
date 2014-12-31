package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class WriteCommentActivity extends AbstractWriteActivity<DataItem> {

	public static final String ACTION_DRAFT = "org.zarroboogs.weibo.DRAFT";

	private static final String ACTION_SEND_FAILED = "org.zarroboogs.weibo.SEND_FAILED";

	private static final String ACTION_NOTIFICATION_COMMENT = "org.zarroboogs.weibo.NOTIFICATION_COMMENT";

	private String token;

	private MessageBean msg;

	private CommentDraftBean commentDraftBean;

	private MenuItem enableCommentOri;

	private MenuItem enableRepost;

	private boolean savedEnableCommentOri;

	private boolean savedEnableRepost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getActionBar().setTitle(R.string.comments);
//		getActionBar().setSubtitle(getCurrentAccountBean().getUsernick());

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

		getToolbar().inflateMenu(R.menu.actionbar_menu_commentnewactivity);
		getToolbar().setTitle(R.string.comments);
		
		enableCommentOri = getToolbar().getMenu().findItem(R.id.menu_enable_ori_comment);
		enableRepost = getToolbar().getMenu().findItem(R.id.menu_enable_repost);

		enableCommentOri.setChecked(savedEnableCommentOri);
		enableRepost.setChecked(savedEnableRepost);
		
		if (msg != null && msg.getRetweeted_status() != null) {
			enableCommentOri.setVisible(true);
		}
		String contentStr = getEditTextView().getText().toString();
		if (!TextUtils.isEmpty(contentStr)) {
			getToolbar().getMenu().findItem(R.id.menu_clear).setVisible(true);
		} else {
			getToolbar().getMenu().findItem(R.id.menu_clear).setVisible(false);
		}
		
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.actionbar_menu_commentnewactivity, menu);
//		enableCommentOri = menu.findItem(R.id.menu_enable_ori_comment);
//		enableRepost = menu.findItem(R.id.menu_enable_repost);
//
//		enableCommentOri.setChecked(savedEnableCommentOri);
//		enableRepost.setChecked(savedEnableRepost);
//		return true;
//	}

//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		if (msg != null && msg.getRetweeted_status() != null) {
//			enableCommentOri.setVisible(true);
//		}
//		String contentStr = getEditTextView().getText().toString();
//		if (!TextUtils.isEmpty(contentStr)) {
//			menu.findItem(R.id.menu_clear).setVisible(true);
//		} else {
//			menu.findItem(R.id.menu_clear).setVisible(false);
//		}
//		return super.onPrepareOptionsMenu(menu);
//	}
	
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
	
	public static Intent startBecauseSendFailed(Context context, AccountBean account, String content, MessageBean oriMsg, CommentDraftBean draft,
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
		getEditTextView().setHint("@" + msg.getUser().getScreen_name() + "：" + msg.getText());
	}

	private void handleFailedOperation(Intent intent) {
		token = ((AccountBean) intent.getParcelableExtra(Constants.ACCOUNT)).getAccess_token();
		msg = (MessageBean) getIntent().getParcelableExtra("oriMsg");

		getEditTextView().setError(intent.getStringExtra("failedReason"));
		getEditTextView().setText(intent.getStringExtra("content"));
		commentDraftBean = (CommentDraftBean) intent.getParcelableExtra("draft");
		getEditTextView().setHint("@" + msg.getUser().getScreen_name() + "：" + msg.getText());

		savedEnableRepost = intent.getBooleanExtra("comment_ori", false);

	}

	private void handleNormalOperation(Intent intent) {

		token = getIntent().getStringExtra(Constants.TOKEN);
		if (TextUtils.isEmpty(token)) {
			token = GlobalContext.getInstance().getSpecialToken();
		}

		msg = (MessageBean) getIntent().getParcelableExtra("msg");
		getEditTextView().setHint("@" + msg.getUser().getScreen_name() + "：" + msg.getText());
	}

	private void handleDraftOperation(Intent intent) {

		token = getIntent().getStringExtra(Constants.TOKEN);
		if (TextUtils.isEmpty(token)) {
			token = GlobalContext.getInstance().getSpecialToken();
		}

		commentDraftBean = (CommentDraftBean) getIntent().getParcelableExtra("draft");
		msg = commentDraftBean.getMessageBean();
		getEditTextView().setText(commentDraftBean.getContent());

		getEditTextView().setHint("@" + msg.getUser().getScreen_name() + "：" + msg.getText());
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
			msg = (MessageBean) savedInstanceState.getParcelable("msg");
			commentDraftBean = (CommentDraftBean) savedInstanceState.getParcelable("commentDraftBean");
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
			DraftDBManager.getInstance().insertComment(getEditTextView().getText().toString(), msg, GlobalContext.getInstance().getCurrentAccountId());
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
				Toast.makeText(this, getString(R.string.content_cant_be_empty_and_dont_have_account), Toast.LENGTH_SHORT).show();
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
		switch (item.getItemId()) {
		case android.R.id.home:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
			break;

		case R.id.menu_enable_ori_comment:
			if (enableCommentOri.isChecked()) {
				enableCommentOri.setChecked(false);
			} else {
				enableCommentOri.setChecked(true);
			}
			break;
		case R.id.menu_enable_repost:
			if (enableRepost.isChecked()) {
				enableRepost.setChecked(false);
			} else {
				enableRepost.setChecked(true);
			}
			break;
		case R.id.menu_at:
			Intent intent = new Intent(WriteCommentActivity.this, AtUserActivity.class);
			intent.putExtra(Constants.TOKEN, token);
			startActivityForResult(intent, AT_USER);
			break;
		case R.id.menu_clear:
			clearContentMenu();
			break;
		}
		return true;
	}

	@Override
	protected void send() {
		if (!enableRepost.isChecked()) {
			String content = ((EditText) findViewById(R.id.status_new_content)).getText().toString();
			Intent intent = SendCommentService.newIntent(getCurrentAccountBean(), msg, content, enableCommentOri.isChecked());
			startService(intent);
			finish();

		} else {
			repost();
		}

	}

	/**
	 * 1. this message has repost's message 2. this message is an original
	 * message
	 * <p/>
	 * if this message has repost's message,try to include its content, if total
	 * word number above 140,discard current msg content
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
		intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getSpecialToken());
		intent.putExtra("accountId", GlobalContext.getInstance().getCurrentAccountId());
		startService(intent);
		finish();

	}

	@Override
	protected AccountBean getCurrentAccountBean() {
		if (WriteCommentActivity.ACTION_NOTIFICATION_COMMENT.equals(getIntent().getAction())
				|| WriteCommentActivity.ACTION_SEND_FAILED.equals(getIntent().getAction())) {
			AccountBean accountBean = ((AccountBean) getIntent().getParcelableExtra(Constants.ACCOUNT));
			return accountBean;
		} else {
			return super.getCurrentAccountBean();
		}
	}
}
