package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.adapter.SearchSuggestionProvider;
import org.zarroboogs.weibo.fragment.SearchStatusFragment;
import org.zarroboogs.weibo.fragment.SearchUserFragment;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import com.rengwuxian.materialedittext.MaterialEditText;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class SearchMainActivity extends SharedPreferenceActivity {
    private static final String KEY_SEARCH = "search_key";

    private String q;

    public enum SearchWhat {
        user, status
    }

    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main_activity_layout);
        mToolbar = ViewUtility.findViewById(this, R.id.searchToolbar);

        changeTitle();

        handleIntent(getIntent());

        buildContent();
        disPlayHomeAsUp(R.id.searchToolbar);

    }

    private void changeTitle() {
        SearchWhat sw = getSearchWhat();
        if (sw == SearchWhat.status) {
            mToolbar.setTitle("搜索微博");

        } else {
            mToolbar.setTitle("搜索用户");
        }
    }

    private SearchWhat getSearchWhat() {
        String title = getSPs().getString(KEY_SEARCH, SearchWhat.status.toString());
        SearchWhat sw = Enum.valueOf(SearchWhat.class, title);
        return sw;
    }

    private SearchWhat changeSearchWhat(SearchWhat sw) {
        getSPs().edit().putString(KEY_SEARCH, sw.toString()).commit();
        return sw;
    }

    private void buildContent() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                if (getSupportFragmentManager().findFragmentByTag(SearchUserFragment.class.getName()) == null) {
                    ft.add(R.id.searchContent, getSearchUserFragment(), SearchUserFragment.class.getName());
                    ft.hide(getSearchUserFragment());
                }

                if (getSupportFragmentManager().findFragmentByTag(SearchStatusFragment.class.getName()) == null) {
                    ft.add(R.id.searchContent, getSearchStatusFragment(), SearchStatusFragment.class.getName());
                    ft.hide(getSearchStatusFragment());
                }

                SearchWhat sw = getSearchWhat();
                if (sw == SearchWhat.status) {
                    ft.hide(getSearchStatusFragment());
                    ft.show(getSearchUserFragment());

                } else {
                    ft.hide(getSearchUserFragment());
                    ft.show(getSearchStatusFragment());
                }

                ft.commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();


            }
        });
    }

    public SearchUserFragment getSearchUserFragment() {
        SearchUserFragment fragment = ((SearchUserFragment) getSupportFragmentManager().findFragmentByTag(SearchUserFragment.class.getName()));
        if (fragment == null) {
            fragment = new SearchUserFragment();
        }
        return fragment;
    }

    private SearchStatusFragment getSearchStatusFragment() {
        SearchStatusFragment fragment = ((SearchStatusFragment) getSupportFragmentManager().findFragmentByTag(SearchStatusFragment.class.getName()));
        if (fragment == null) {
            fragment = new SearchStatusFragment();
        }

        return fragment;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            search(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_searchmainactivity, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search: {
                changeFragment();
                showSearchDialog();
                break;
            }
            case R.id.searchWeibo: {
                changeSearchWhat(SearchWhat.status);
                changeFragment();
                changeTitle();
                showSearchDialog();
                break;
            }

            case R.id.searchUser: {
                changeSearchWhat(SearchWhat.user);
                changeFragment();
                changeTitle();
                showSearchDialog();
                break;
            }

            default:
                break;
        }

        return true;
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater lif = LayoutInflater.from(this);
        View view = lif.inflate(R.layout.door_img_dialog_layout, null);
        TextView tvTextView = ViewUtility.findViewById(view, R.id.doorTitle);
        final MaterialEditText me = ViewUtility.findViewById(view, R.id.doorEditText);
        Button mButton = ViewUtility.findViewById(view, R.id.doorCheckBtn);
        SearchWhat sw = getSearchWhat();
        if (sw == SearchWhat.status) {
            tvTextView.setText("搜索微博");
        } else {
            tvTextView.setText("搜索用户");
        }

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        mButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                search(me.getText().toString().trim());
                alertDialog.cancel();
            }
        });
    }

    private void changeFragment() {
        SearchWhat sw = getSearchWhat();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (sw == SearchWhat.status) {
            ft.show(getSearchStatusFragment());
            ft.hide(getSearchUserFragment());
        } else {
            ft.show(getSearchUserFragment());
            ft.hide(getSearchStatusFragment());
        }
        ft.commit();
    }

    public String getSearchWord() {
        return this.q;
    }

    private void search(final String q) {
        if (!TextUtils.isEmpty(q)) {
            this.q = q;

            SearchWhat sw = getSearchWhat();
            if (sw == SearchWhat.status) {
                getSearchStatusFragment().search(getSearchWord());
            } else {
                getSearchUserFragment().search(getSearchWord());
            }
        }
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // getActionBar().setSelectedNavigationItem(position);
        }
    };

}
