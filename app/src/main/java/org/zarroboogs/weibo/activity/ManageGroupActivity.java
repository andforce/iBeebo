
package org.zarroboogs.weibo.activity;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.GroupBean;
import org.zarroboogs.weibo.bean.GroupListBean;
import org.zarroboogs.weibo.dao.CreateGroupDao;
import org.zarroboogs.weibo.dao.DestroyGroupDao;
import org.zarroboogs.weibo.dao.FriendGroupDao;
import org.zarroboogs.weibo.dao.UpdateGroupNameDao;
import org.zarroboogs.weibo.db.task.GroupDBTask;
import org.zarroboogs.weibo.dialogfragment.AddGroupDialog;
import org.zarroboogs.weibo.dialogfragment.ModifyGroupDialog;
import org.zarroboogs.weibo.dialogfragment.RemoveGroupDialog;
import org.zarroboogs.weibo.setting.activity.SettingActivity;
import org.zarroboogs.weibo.support.utils.ThemeUtility;
import org.zarroboogs.weibo.support.utils.Utility;

import com.umeng.analytics.MobclickAgent;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ManageGroupActivity extends AbstractAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // getActionBar().setDisplayShowHomeEnabled(false);
        // getActionBar().setDisplayShowTitleEnabled(true);
        // getActionBar().setDisplayHomeAsUpEnabled(false);
        // getActionBar().setTitle(getString(R.string.friend_group));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new ManageGroupFragment()).commit();
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return false;
    }

    public static class ManageGroupFragment extends ListFragment {

        private GroupAdapter adapter;

        private GroupListBean group;

        private List<String> name;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            setRetainInstance(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            name = new ArrayList<String>();
            adapter = new GroupAdapter();
            getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            getListView().setMultiChoiceModeListener(new GroupMultiChoiceModeListener());
            setListAdapter(adapter);
            group = BeeboApplication.getInstance().getGroup();
            if (group != null) {
                final List<GroupBean> list = group.getLists();

                for (int i = 0; i < list.size(); i++) {
                    name.add(list.get(i).getName());
                }
                adapter.notifyDataSetChanged();
            }
        }

        private void refreshListData() {
            if (group != null) {
                name.clear();
                final List<GroupBean> list = group.getLists();

                for (int i = 0; i < list.size(); i++) {
                    name.add(list.get(i).getName());
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.actionbar_menu_managegroupfragment, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int itemId = item.getItemId();
			if (itemId == R.id.menu_add) {
				AddGroupDialog dialog = new AddGroupDialog();
				dialog.setTargetFragment(ManageGroupFragment.this, 0);
				dialog.show(getFragmentManager(), "");
			}

            return true;
        }

        public void addGroup(String groupName) {
            new CreateGroupTask(BeeboApplication.getInstance().getAccessToken(), groupName)
                    .executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }

        public void modifyGroupName(String idstr, String groupName) {
            new ModifyGroupNameTask(BeeboApplication.getInstance().getAccessToken(), idstr, groupName)
                    .executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }

        public void removeGroup(List<String> groupNames) {
            new RemoveGroupTask(BeeboApplication.getInstance().getAccessToken(), groupNames)
                    .executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }

        class GroupAdapter extends BaseAdapter {

            int checkedBG;

            int defaultBG;

            public GroupAdapter() {
                defaultBG = getResources().getColor(R.color.transparent);
                checkedBG = ThemeUtility.getColor(getActivity(), R.attr.listview_checked_color);

            }

            @Override
            public int getCount() {
                return name.size();
            }

            @Override
            public String getItem(int position) {
                return name.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = getActivity().getLayoutInflater().inflate(R.layout.managegroupactivity_list_item_layout, parent,
                        false);
                TextView tv = (TextView) view;
                tv.setBackgroundColor(defaultBG);
                if (getListView().getCheckedItemPositions().get(position)) {
                    tv.setBackgroundColor(checkedBG);
                }
                tv.setText(name.get(position));
                return view;
            }
        }

        class GroupMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {

            MenuItem modify;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.contextual_menu_managegroupfragment, menu);
                modify = menu.findItem(R.id.menu_modify_group_name);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                SparseBooleanArray positions = null;
                ArrayList<String> checkedIdstrs = null;
                int itemId = item.getItemId();
				if (itemId == R.id.menu_modify_group_name) {
					positions = getListView().getCheckedItemPositions();
					checkedIdstrs = new ArrayList<String>();
					String oriName = null;
					for (int i = 0; i < positions.size(); i++) {
					    if (positions.get(positions.keyAt(i))) {
					        oriName = group.getLists().get(positions.keyAt(i)).getName();
					        checkedIdstrs.add(group.getLists().get(positions.keyAt(i)).getIdstr());
					    }
					}
					ModifyGroupDialog modifyGroupDialog = new ModifyGroupDialog(oriName, checkedIdstrs.get(0));
					modifyGroupDialog.setTargetFragment(ManageGroupFragment.this, 0);
					modifyGroupDialog.show(getFragmentManager(), "");
					mode.finish();
					return true;
				} else if (itemId == R.id.menu_remove) {
					positions = getListView().getCheckedItemPositions();
					checkedIdstrs = new ArrayList<String>();
					for (int i = 0; i < positions.size(); i++) {
					    if (positions.get(positions.keyAt(i))) {
					        checkedIdstrs.add(group.getLists().get(positions.keyAt(i)).getIdstr());
					    }
					}
					RemoveGroupDialog removeGroupDialog = new RemoveGroupDialog(checkedIdstrs);
					removeGroupDialog.setTargetFragment(ManageGroupFragment.this, 0);
					removeGroupDialog.show(getFragmentManager(), "");
					mode.finish();
					return true;
				}
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (getListView().getCheckedItemCount() > 1) {
                    modify.setVisible(false);
                } else {
                    modify.setVisible(true);
                }
                mode.setTitle(String.format(getString(R.string.have_selected),
                        String.valueOf(getListView().getCheckedItemCount())));
                adapter.notifyDataSetChanged();
            }
        }

        class CreateGroupTask extends MyAsyncTask<Void, Void, GroupBean> {

            String token;

            String name;

            WeiboException e;

            public CreateGroupTask(String token, String name) {
                this.token = token;
                this.name = name;
            }

            @Override
            protected GroupBean doInBackground(Void... params) {
                try {
                    return new CreateGroupDao(token, name).create();
                } catch (WeiboException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                return null;
            }

            @Override
            protected void onPostExecute(GroupBean groupBean) {
                super.onPostExecute(groupBean);
                if (getActivity() == null) {
                    return;
                }
                if (Utility.isAllNotNull(groupBean)) {
                    new RefreshGroupTask(token).executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }

        class RefreshGroupTask extends MyAsyncTask<Void, GroupListBean, GroupListBean> {

            private WeiboException e;

            private String token;

            public RefreshGroupTask(String token) {
                this.token = token;
            }

            @Override
            protected GroupListBean doInBackground(Void... params) {
                try {
                    return new FriendGroupDao(token).getGroup();
                } catch (WeiboException e) {
                    cancel(true);
                }
                return null;
            }

            @Override
            protected void onPostExecute(GroupListBean groupListBean) {
                super.onPostExecute(groupListBean);
                if (getActivity() == null) {
                    return;
                }
                GroupDBTask.update(groupListBean, BeeboApplication.getInstance().getCurrentAccountId());
                BeeboApplication.getInstance().setGroup(groupListBean);
                group = groupListBean;
                refreshListData();
            }
        }

        class RemoveGroupTask extends MyAsyncTask<Void, Void, Boolean> {

            String token;

            List<String> groupNames;

            WeiboException e;

            public RemoveGroupTask(String token, List<String> groupNames) {
                this.token = token;
                this.groupNames = groupNames;
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    boolean result = true;
                    for (String groupName : groupNames) {
                        if (!new DestroyGroupDao(token, groupName).destroy()) {
                            result = false;
                        }
                    }
                    return result;
                } catch (WeiboException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean groupBean) {
                super.onPostExecute(groupBean);
                if (getActivity() == null) {
                    return;
                }
                if (Utility.isAllNotNull(groupBean)) {
                    new RefreshGroupTask(token).executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }

        class ModifyGroupNameTask extends MyAsyncTask<Void, Void, GroupBean> {

            String token;

            String groupIdstr;

            String name;

            WeiboException e;

            public ModifyGroupNameTask(String token, String groupIdstr, String name) {
                this.token = token;
                this.groupIdstr = groupIdstr;
                this.name = name;
            }

            @Override
            protected GroupBean doInBackground(Void... params) {
                try {

                    return new UpdateGroupNameDao(token, groupIdstr, name).update();

                } catch (WeiboException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                return null;
            }

            @Override
            protected void onPostExecute(GroupBean groupBean) {
                super.onPostExecute(groupBean);
                if (getActivity() == null) {
                    return;
                }
                if (Utility.isAllNotNull(groupBean)) {
                    new RefreshGroupTask(token).executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }
    }
}
