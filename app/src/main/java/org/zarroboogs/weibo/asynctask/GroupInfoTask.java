
package org.zarroboogs.weibo.asynctask;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.bean.GroupListBean;
import org.zarroboogs.weibo.dao.FriendGroupDao;
import org.zarroboogs.weibo.db.task.GroupDBTask;

public class GroupInfoTask extends MyAsyncTask<Void, GroupListBean, GroupListBean> {

    private WeiboException e;

    private String token;
    private String accountId;

    public GroupInfoTask(String token, String accountId) {
        this.token = token;
        this.accountId = accountId;
    }

    @Override
    protected GroupListBean doInBackground(Void... params) {
        try {
            return new FriendGroupDao(token).getGroup();
        } catch (WeiboException e) {
            this.e = e;
            cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(GroupListBean groupListBean) {
        super.onPostExecute(groupListBean);

        GroupDBTask.update(groupListBean, accountId);
        if (accountId.equalsIgnoreCase(BeeboApplication.getInstance().getCurrentAccountId()))
            BeeboApplication.getInstance().setGroup(groupListBean);

    }

}
