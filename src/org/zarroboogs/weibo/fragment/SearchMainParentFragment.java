
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.adapter.SearchSuggestionProvider;
import org.zarroboogs.weibo.adapter.SearchTimeLinePagerAdapter;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.support.lib.LongClickableLinkMovementMethod;
import org.zarroboogs.weibo.support.utils.SmileyPickerUtility;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import com.example.android.common.view.SlidingTabLayout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

public class SearchMainParentFragment extends BaseStateFragment implements MainTimeLineActivity.ScrollableListFragment {

    private ViewPager viewPager;
    private SlidingTabLayout mSlidingTabLayout;

    private SparseArray<Fragment> searchFragments = new SparseArray<Fragment>();

    private SparseArray<ActionBar.Tab> tabMap = new SparseArray<ActionBar.Tab>();

    private static final int SEARCH_WEIBO_CHILD_POSITION = 0;

    private static final int SEARCH_USER_CHILD_POSITION = 1;

    private SearchView searchView;

    private Toolbar mSearchToolbar;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static SearchMainParentFragment newInstance() {
        SearchMainParentFragment fragment = new SearchMainParentFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MyLogger("onCreate");
    }

    private static void MyLogger(String method) {
        Log.d("Seatch_event: ", method);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("q", q);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            this.q = savedInstanceState.getString("q");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.searchSTL);
        MyLogger("onCreateView");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(onPageChangeListener);
        SearchTimeLinePagerAdapter adapter = new SearchTimeLinePagerAdapter(this, viewPager, getChildFragmentManager(),
                (MainTimeLineActivity) getActivity(),
                searchFragments);
        viewPager.setAdapter(adapter);
        mSlidingTabLayout.setViewPager(viewPager);

        mSearchToolbar = (Toolbar) ((MainTimeLineActivity) getActivity()).findViewById(R.id.mainTimeLineToolBar);
        MyLogger("onViewCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        MyLogger("onStart");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MyLogger("onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        showSearchMenu();
    }

    public void showSearchMenu() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (SearchMainParentFragment.this.isVisible() && mSearchToolbar != null) {

                    mSearchToolbar.getMenu().clear();
                    mSearchToolbar.inflateMenu(R.menu.actionbar_menu_searchmainactivity);

                    createSearch(mSearchToolbar.getMenu());
                    ViewUtility.findViewById(getActivity(), R.id.scrollToTopBtn).setVisibility(View.GONE);
                }
            }
        }, 200);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (searchView != null) {
                SmileyPickerUtility.showKeyBoard(searchView);
            }
        } else {
            if (searchView != null) {
                SmileyPickerUtility.hideSoftInput(searchView);
            }
        }
    }

    private void createSearch(Menu menu) {
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setMaxWidth(Utility.dip2px(250));

        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(q);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchMainParentFragment.this.q = newText;
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(q);
            }
        });
        if (!TextUtils.isEmpty(this.q)) {
            searchView.setQuery(this.q, false);
        }
    }

    public String getSearchWord() {
        return this.q;
    }

    private String q;

    private void search(final String q) {
        if (!TextUtils.isEmpty(q)) {
            this.q = q;
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(this.q, null);
            switch (viewPager.getCurrentItem()) {
                case 0:
                    ((SearchStatusFragment) getSearchWeiboFragment()).search();
                    break;
                case 1:
                    ((SearchUserFragment) getSearchUserFragment()).search();
                    break;
            }
        }
    }

    public ActionBar.Tab getWeiboTab() {
        return tabMap.get(SEARCH_WEIBO_CHILD_POSITION);
    }

    public ActionBar.Tab getUserTab() {
        return tabMap.get(SEARCH_USER_CHILD_POSITION);
    }

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // ActionBar ab = getActivity().getActionBar();
            // if (getActivity().getActionBar().getNavigationMode() ==
            // ActionBar.NAVIGATION_MODE_TABS
            // && ab.getTabAt(position) == tabMap.get(position)) {
            // ab.setSelectedNavigationItem(position);
            // }

            ((LeftMenuFragment) ((MainTimeLineActivity) getActivity()).getLeftMenuFragment()).searchTabIndex = position;
            clearActionMode();
            MyLogger("onPageSelected");
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            switch (state) {
                case ViewPager.SCROLL_STATE_SETTLING:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LongClickableLinkMovementMethod.getInstance().setLongClickable(true);

                        }
                    }, ViewConfiguration.getLongPressTimeout());
                    break;
                default:
                    LongClickableLinkMovementMethod.getInstance().setLongClickable(false);
                    break;
            }
        }
    };

    public SearchUserFragment getSearchUserFragment() {
        SearchUserFragment fragment = ((SearchUserFragment) getChildFragmentManager().findFragmentByTag(
                SearchUserFragment.class.getName()));
        if (fragment == null) {
            fragment = new SearchUserFragment();
        }

        return fragment;
    }

    public SearchStatusFragment getSearchWeiboFragment() {
        SearchStatusFragment fragment = ((SearchStatusFragment) getChildFragmentManager().findFragmentByTag(
                SearchStatusFragment.class.getName()));
        if (fragment == null) {
            fragment = new SearchStatusFragment();
        }

        return fragment;
    }

    @Override
    public void scrollToTop() {
        Fragment fragment = searchFragments.get(viewPager.getCurrentItem());
        if (fragment instanceof AbsBaseTimeLineFragment) {
            Utility.stopListViewScrollingAndScrollToTop(((AbsBaseTimeLineFragment) fragment).getListView());
        } else if (fragment instanceof AbstractUserListFragment) {
            Utility.stopListViewScrollingAndScrollToTop(((AbstractUserListFragment) fragment).getListView());

        }
    }

    public void clearActionMode() {
        getSearchUserFragment().clearActionMode();
        getSearchWeiboFragment().clearActionMode();
    }
}
