package org.zarroboogs.weibo.ui.task;

import android.widget.Toast;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.FavBean;
import org.zarroboogs.weibo.dao.FavDao;

/**
 * User: qii Date: 12-9-12
 */
public class FavAsyncTask extends MyAsyncTask<Void, FavBean, FavBean> {

	private String token;
	private String id;
	private WeiboException e;

	public FavAsyncTask(String token, String id) {
		this.token = token;
		this.id = id;
	}

	@Override
	protected FavBean doInBackground(Void... params) {
		FavDao dao = new FavDao(token, id);
		try {
			return dao.favIt();
		} catch (WeiboException e) {
			this.e = e;
			cancel(true);
			return null;
		}
	}

	@Override
	protected void onCancelled(FavBean favBean) {
		super.onCancelled(favBean);
		if (favBean == null && this.e != null)
			Toast.makeText(GlobalContext.getInstance(), e.getError(), Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPostExecute(FavBean favBean) {
		super.onPostExecute(favBean);
		if (favBean != null)
			Toast.makeText(GlobalContext.getInstance(), GlobalContext.getInstance().getString(R.string.fav_successfully), Toast.LENGTH_SHORT).show();
	}
}
