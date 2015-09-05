package org.zarroboogs.msrl;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.zarroboogs.msrl.widget.MaterialSwipeRefreshLayout;

import java.util.ArrayList;


public class MSRLMainActivity extends ActionBarActivity {

    private MaterialSwipeRefreshLayout mRefreshLayout;
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<String> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msrlmain);

        mRefreshLayout = (MaterialSwipeRefreshLayout) findViewById(R.id.demo_msrl);
        mListView = (ListView) findViewById(R.id.demo_list_view);
        mRefreshLayout.setFooterView(R.layout.listview_footer);

        values = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            values.add("Item " + i);
        }
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        mListView.setAdapter(mArrayAdapter);

        mRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow);

        mRefreshLayout.setOnRefreshLoadMoreListener(new MaterialSwipeRefreshLayout.OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        values.add(0, "Swipe Down to Refresh " + values.size());
                        mArrayAdapter.notifyDataSetChanged();
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        values.add("Swipe Up to Load More " + values.size());
                        mArrayAdapter.notifyDataSetChanged();
                        mRefreshLayout.setLoadingMore(false);
                    }
                }, 2000);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_msrlmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
