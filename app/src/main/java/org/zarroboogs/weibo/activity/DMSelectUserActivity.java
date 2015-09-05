
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.dao.SearchDao;
import org.zarroboogs.weibo.fragment.AbstractFriendsFanListFragment;
import org.zarroboogs.weibo.loader.FriendUserLoader;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.widget.PerformanceImageView;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class DMSelectUserActivity extends AbstractAppActivity {

    private List<UserBean> data;

    private ProgressBar suggestProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dmselectuseractivity_layout);

        View title = getLayoutInflater().inflate(R.layout.dmselectuseractivity_title_layout, null);
        suggestProgressBar = (ProgressBar) title.findViewById(R.id.have_suggest_progressbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.list_content,
                            SelectFriendsListFragment.newInstance(BeeboApplication.getInstance().getAccountBean().getInfo()))
                    .commit();
        }

        MaterialAutoCompleteTextView search = (MaterialAutoCompleteTextView) findViewById(R.id.search);
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        search.setAdapter(adapter);
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("user", data.get(position));
                setResult(0, intent);
                finish();
            }
        });

        disPlayHomeAsUp(R.id.dmselectUserToolbar);
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

    public UserBean getUser() {
        return BeeboApplication.getInstance().getAccountBean().getInfo();
    }

    public ProgressBar getSuggestProgressBar() {
        return suggestProgressBar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = MainTimeLineActivity.newIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return false;
    }

    private class AutoCompleteAdapter extends ArrayAdapter<UserBean> implements Filterable {

        private DMSelectUserActivity activity;

        private ProgressBar suggestProgressBar;

        public AutoCompleteAdapter(DMSelectUserActivity context, int textViewResourceId) {
            super(context, textViewResourceId);
            data = new ArrayList<UserBean>();
            this.activity = context;
            this.suggestProgressBar = this.activity.getSuggestProgressBar();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public UserBean getItem(int index) {
            return data.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                suggestProgressBar.setVisibility(View.VISIBLE);
                            }
                        });

                        SearchDao dao = new SearchDao(BeeboApplication.getInstance().getAccessTokenHack(), constraint.toString());

                        try {
                            data = dao.getUserList().getUsers();
                        } catch (Exception e) {
                        }
                        // Now assign the values and count to the FilterResults
                        // object
                        filterResults.values = data;
                        filterResults.count = data.size();
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            suggestProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                    return filterResults;
                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {
                    return ((UserBean) resultValue).getScreen_name();
                }

                @Override
                protected void publishResults(CharSequence contraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return myFilter;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            convertView = activity.getLayoutInflater().inflate(R.layout.dm_search_user_dropdown_item_layout, parent, false);

            PerformanceImageView avatar = (PerformanceImageView) convertView.findViewById(R.id.avatar);
            TextView username = (TextView) convertView.findViewById(R.id.username);

            TimeLineBitmapDownloader.getInstance().downloadAvatar(avatar, getItem(position));
            username.setText(getItem(position).getScreen_name());

            return convertView;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }
    }

    public static class SelectFriendsListFragment extends AbstractFriendsFanListFragment {

        public static SelectFriendsListFragment newInstance(UserBean userBean) {
            SelectFriendsListFragment fragment = new SelectFriendsListFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.USERBEAN, userBean);
            fragment.setArguments(bundle);
            return fragment;
        }

        public SelectFriendsListFragment() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setHasOptionsMenu(false);
            setRetainInstance(false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }

        @Override
        protected UserBean getCurrentUser() {
            return getArguments().getParcelable(Constants.USERBEAN);
        }

        @Override
        protected void buildActionBarSubtitle() {
            // empty
        }

        protected void listViewItemClick(AdapterView parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.putExtra("user", getList().getUsers().get(position));
            getActivity().setResult(0, intent);
            getActivity().finish();
        }

        @Override
        protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateNewUserLoader(int id, Bundle args) {
            String token = BeeboApplication.getInstance().getAccessTokenHack();
            String cursor = String.valueOf(0);
            return new FriendUserLoader(getActivity(), token, getCurrentUser().getId(), cursor);
        }

        @Override
        protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateOldUserLoader(int id, Bundle args) {

            if (getList().getUsers().size() > 0 && Integer.valueOf(getList().getNext_cursor()) == 0) {
                return null;
            }

            String token = BeeboApplication.getInstance().getAccessTokenHack();
            String cursor = String.valueOf(bean.getNext_cursor());

            return new FriendUserLoader(getActivity(), token, getCurrentUser().getId(), cursor);
        }
    }
}
