package org.zarroboogs.weibo.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.zarroboogs.weibo.bean.CommentTimeLineData;
import org.zarroboogs.weibo.db.task.MentionCommentsTimeLineDBTask;

/**
 * User: qii Date: 13-4-10
 */
public class MentionsCommentDBLoader extends AsyncTaskLoader<CommentTimeLineData> {

	private String accountId;
	private CommentTimeLineData result;

	public MentionsCommentDBLoader(Context context, String accountId) {
		super(context);
		this.accountId = accountId;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if (result == null) {
			forceLoad();
		} else {
			deliverResult(result);
		}
	}

	public CommentTimeLineData loadInBackground() {
		result = MentionCommentsTimeLineDBTask.getCommentLineMsgList(accountId);
		return result;
	}

}