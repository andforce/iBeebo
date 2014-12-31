package org.zarroboogs.weibo.service;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.WriteCommentActivity;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.CommentBean;
import org.zarroboogs.weibo.bean.CommentDraftBean;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.dao.CommentNewMsgDao;
import org.zarroboogs.weibo.db.DraftDBManager;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.NotificationUtility;
import org.zarroboogs.weibo.support.utils.Utility;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * User: qii Date: 13-1-20
 */
@SuppressLint("NewApi")
public class SendCommentService extends Service {

	private Map<WeiboSendTask, Boolean> tasksResult = new HashMap<WeiboSendTask, Boolean>();

	private Map<WeiboSendTask, Integer> tasksNotifications = new HashMap<WeiboSendTask, Integer>();

	private Handler handler = new Handler();

	public static Intent newIntent(AccountBean accountBean, MessageBean msg, String content, boolean comment_ori) {
		Intent intent = new Intent(GlobalContext.getInstance(), SendCommentService.class);
		intent.putExtra("oriMsg", msg);
		intent.putExtra("content", content);
		intent.putExtra("comment_ori", comment_ori);
		intent.putExtra(Constants.TOKEN, accountBean.getAccess_token());
		intent.putExtra(Constants.ACCOUNT, accountBean);
		return intent;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		int lastNotificationId = intent.getIntExtra("lastNotificationId", -1);
		if (lastNotificationId != -1) {
			NotificationUtility.cancel(lastNotificationId);
		}

		String token = intent.getStringExtra(Constants.TOKEN);
		AccountBean account = (AccountBean) intent.getParcelableExtra(Constants.ACCOUNT);
		String content = intent.getStringExtra("content");
		MessageBean oriMsg = (MessageBean) intent.getParcelableExtra("oriMsg");
		boolean comment_ori = intent.getBooleanExtra("comment_ori", false);
		CommentDraftBean commentDraftBean = (CommentDraftBean) intent.getParcelableExtra("draft");

		WeiboSendTask task = new WeiboSendTask(account, token, content, oriMsg, comment_ori, commentDraftBean);
		task.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);

		tasksResult.put(task, false);

		return START_REDELIVER_INTENT;

	}

	@SuppressLint("NewApi")
	private class WeiboSendTask extends MyAsyncTask<Void, Long, Void> {

		AccountBean account;

		String token;

		String content;

		MessageBean oriMsg;

		boolean comment_ori;

		CommentDraftBean commentDraftBean;

		Notification notification;

		WeiboException e;

		public WeiboSendTask(AccountBean account, String token, String content, MessageBean oriMsg, boolean comment_ori, CommentDraftBean commentDraftBean) {
			this.account = account;
			this.token = token;
			this.comment_ori = comment_ori;
			this.content = content;
			this.oriMsg = oriMsg;
			this.commentDraftBean = commentDraftBean;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Notification.Builder builder = new Notification.Builder(SendCommentService.this).setTicker(getString(R.string.sending_comment))
					.setContentTitle(getString(R.string.sending_comment)).setContentText(content).setOnlyAlertOnce(true).setOngoing(true)
					.setSmallIcon(R.drawable.upload_white);

			builder.setProgress(0, 100, true);

			int notificationId = new Random().nextInt(Integer.MAX_VALUE);

			notification = builder.getNotification();

			NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
			notificationManager.notify(notificationId, notification);

			tasksNotifications.put(WeiboSendTask.this, notificationId);

		}

		private CommentBean sendText() throws WeiboException {
			CommentNewMsgDao dao = new CommentNewMsgDao(token, oriMsg.getId(), content);
			dao.enableComment_ori(comment_ori);
			return dao.sendNewMsg();

		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				sendText();
			} catch (WeiboException e) {
				this.e = e;
				cancel(true);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (commentDraftBean != null) {
				DraftDBManager.getInstance().remove(commentDraftBean.getId());
			}
			showSuccessfulNotification(WeiboSendTask.this);

		}

		@Override
		protected void onCancelled(Void aVoid) {
			super.onCancelled(aVoid);
			showFailedNotification(WeiboSendTask.this);

		}

		private void showSuccessfulNotification(final WeiboSendTask task) {
			Notification.Builder builder = new Notification.Builder(SendCommentService.this).setTicker(getString(R.string.send_successfully))
					.setContentTitle(getString(R.string.send_successfully)).setOnlyAlertOnce(true).setAutoCancel(true)
					.setSmallIcon(R.drawable.send_successfully).setOngoing(false);
			Notification notification = builder.getNotification();
			final NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
			final int id = tasksNotifications.get(task);
			notificationManager.notify(id, notification);

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					notificationManager.cancel(id);
					stopServiceIfTasksAreEnd(task);
				}
			}, 3000);

			LocalBroadcastManager.getInstance(SendCommentService.this).sendBroadcast(
					new Intent(AppEventAction.buildSendCommentOrReplySuccessfullyAction(oriMsg)));
		}

		private void showFailedNotification(final WeiboSendTask task) {
			Notification.Builder builder = new Notification.Builder(SendCommentService.this).setTicker(getString(R.string.send_failed))
					.setContentTitle(getString(R.string.send_faile_click_to_open)).setContentText(content).setOnlyAlertOnce(true).setAutoCancel(true)
					.setSmallIcon(R.drawable.send_failed).setOngoing(false);

			Intent notifyIntent = WriteCommentActivity.startBecauseSendFailed(SendCommentService.this, account, content, oriMsg, commentDraftBean, comment_ori,
					e.getError());

			PendingIntent pendingIntent = PendingIntent.getActivity(SendCommentService.this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			builder.setContentIntent(pendingIntent);

			Notification notification;
			if (Utility.isJB()) {
				Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle(builder);
				bigTextStyle.setBigContentTitle(getString(R.string.send_faile_click_to_open));
				bigTextStyle.bigText(content);
				bigTextStyle.setSummaryText(account.getUsernick());
				builder.setStyle(bigTextStyle);

				Intent intent = new Intent(SendCommentService.this, SendCommentService.class);
				intent.putExtra("oriMsg", oriMsg);
				intent.putExtra("content", content);
				intent.putExtra("comment_ori", comment_ori);
				intent.putExtra(Constants.TOKEN, token);
				intent.putExtra(Constants.ACCOUNT, account);

				intent.putExtra("lastNotificationId", tasksNotifications.get(task));

				PendingIntent retrySendIntent = PendingIntent.getService(SendCommentService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.send_light, getString(R.string.retry_send), retrySendIntent);
				notification = builder.build();

			} else {
				notification = builder.getNotification();
			}

			final int id = tasksNotifications.get(task);
			NotificationUtility.show(notification, id);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					stopServiceIfTasksAreEnd(task);
				}
			}, 3000);
		}

	}

	private void stopServiceIfTasksAreEnd(WeiboSendTask currentTask) {

		tasksResult.put(currentTask, true);

		boolean isAllTaskEnd = true;
		Set<WeiboSendTask> taskSet = tasksResult.keySet();
		for (WeiboSendTask task : taskSet) {
			if (!tasksResult.get(task)) {
				isAllTaskEnd = false;
				break;
			}
		}
		if (isAllTaskEnd) {
			stopForeground(true);
			stopSelf();
		}
	}

}
