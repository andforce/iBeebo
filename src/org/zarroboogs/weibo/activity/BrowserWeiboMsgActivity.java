
package org.zarroboogs.weibo.activity;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.dao.DestroyStatusDao;
import org.zarroboogs.weibo.dao.ShowStatusDao;
import org.zarroboogs.weibo.dialogfragment.CommonErrorDialogFragment;
import org.zarroboogs.weibo.dialogfragment.CommonProgressDialogFragment;
import org.zarroboogs.weibo.dialogfragment.RemoveWeiboMsgDialog;
import org.zarroboogs.weibo.fragment.BrowserWeiboMsgFragment;
import org.zarroboogs.weibo.loader.AbstractAsyncNetRequestTaskLoader;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.ThemeUtility;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.ui.task.FavAsyncTask;
import org.zarroboogs.weibo.ui.task.UnFavAsyncTask;

import com.umeng.analytics.MobclickAgent;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class BrowserWeiboMsgActivity extends AbstractAppActivity implements RemoveWeiboMsgDialog.IRemove {

    private static final String ACTION_WITH_ID = "action_with_id";

    private static final String ACTION_WITH_DETAIL = "action_with_detail";

    private static final int REFRESH_LOADER_ID = 0;

    private MessageBean msg;

    private String msgId;

    private String token;

    private FavAsyncTask favTask = null;

    private UnFavAsyncTask unFavTask = null;

    private ShareActionProvider shareActionProvider;

    private GestureDetector gestureDetector;

    private RemoveTask removeTask;

    private AccountBean mAccountBean;

    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.browser_weibo_msg_activity_layout);
        mToolbar = (Toolbar) findViewById(R.id.accountToolBar);
        mToolbar.setTitle(R.string.weibo_detail);
        setSupportActionBar(mToolbar);

        mToolbar.inflateMenu(R.menu.actionbar_menu_browserweibomsgactivity);

        initLayout();
        if (savedInstanceState != null) {
            mAccountBean = savedInstanceState.getParcelable("mAccountBean");

            Log.d("RpostWeiBo_activity_oncreate01", "AccountBean == null ? : " + (mAccountBean == null));

            msg = savedInstanceState.getParcelable("msg");
            token = savedInstanceState.getString(Constants.TOKEN);
            if (msg != null) {
                buildContent();
            } else {
                msgId = getIntent().getStringExtra("weiboId");
                fetchUserInfoFromServer();
            }

        } else {
            mAccountBean = getIntent().getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);

            Log.d("RpostWeiBo_activity_oncreate02", "AccountBean == null ? : " + (mAccountBean == null));

            String action = getIntent().getAction();
            if (ACTION_WITH_ID.equalsIgnoreCase(action)) {
                token = getIntent().getStringExtra(Constants.TOKEN);
                msgId = getIntent().getStringExtra("weiboId");
                fetchUserInfoFromServer();
                findViewById(android.R.id.content).setBackgroundDrawable(
                        ThemeUtility.getDrawable(android.R.attr.windowBackground));
            } else if (ACTION_WITH_DETAIL.equalsIgnoreCase(action)) {
                Intent intent = getIntent();
                token = intent.getStringExtra(Constants.TOKEN);
                msg = intent.getParcelableExtra("msg");
                buildContent();
            } else {
                throw new IllegalArgumentException("activity intent action must be " + ACTION_WITH_DETAIL + " or "
                        + ACTION_WITH_ID);
            }

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
    protected void onDestroy() {
        super.onDestroy();
        Utility.cancelTasks(removeTask);
    }

    public static Intent newIntent(AccountBean accountBean, String weiboId, String token) {
        Intent intent = new Intent(GlobalContext.getInstance(), BrowserWeiboMsgActivity.class);
        intent.putExtra("weiboId", weiboId);
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, accountBean);
        intent.putExtra(Constants.TOKEN, token);
        intent.setAction(ACTION_WITH_ID);
        return intent;
    }

    public static Intent newIntent(AccountBean accountBean, MessageBean msg, String token) {
        Intent intent = new Intent(GlobalContext.getInstance(), BrowserWeiboMsgActivity.class);
        intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, accountBean);
        intent.putExtra("msg", msg);
        intent.putExtra(Constants.TOKEN, token);
        intent.setAction(ACTION_WITH_DETAIL);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("msg", msg);
        outState.putString(Constants.TOKEN, token);
        outState.putParcelable("mAccountBean", mAccountBean);
    }

    private void fetchUserInfoFromServer() {

        // getActionBar().setTitle(getString(R.string.fetching_weibo_info));

        CommonProgressDialogFragment dialog = CommonProgressDialogFragment
                .newInstance(getString(R.string.fetching_weibo_info));
        getSupportFragmentManager().beginTransaction().add(dialog, CommonProgressDialogFragment.class.getName()).commit();
        getSupportLoaderManager().initLoader(REFRESH_LOADER_ID, null, refreshCallback);
    }

    private void initLayout() {
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActionBar().setDisplayShowHomeEnabled(false);
    }

    private void buildContent() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (getSupportFragmentManager().findFragmentByTag(BrowserWeiboMsgFragment.class.getName()) == null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content, BrowserWeiboMsgFragment.newInstance(msg),
                                    BrowserWeiboMsgFragment.class.getName())
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();
                    findViewById(R.id.content).setBackgroundDrawable(null);
                }
            }
        });

        // getActionBar().setTitle(getString(R.string.detail));

        invalidateOptionsMenu();

    }

    private Fragment getBrowserWeiboMsgFragment() {
        return getSupportFragmentManager().findFragmentByTag(BrowserWeiboMsgFragment.class.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (msg == null) {
            return super.onCreateOptionsMenu(menu);
        }

        getMenuInflater().inflate(R.menu.actionbar_menu_browserweibomsgactivity, menu);

        if (msg.getUser() != null && msg.getUser().getId().equals(GlobalContext.getInstance().getCurrentAccountId())) {
            menu.findItem(R.id.menu_delete).setVisible(true);
        }

        // MenuItem item = menu.findItem(R.id.menu_share);
        //
        // shareActionProvider = (ShareActionProvider) item.getActionProvider();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = MainTimeLineActivity.newIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.menu_repost:
                intent = new Intent(this, RepostWeiboWithAppSrcActivity.class);
                Log.d("RpostWeiBo_activity_start", "AccountBean == null ? : " + (mAccountBean == null));
                intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
                intent.putExtra(Constants.TOKEN, getToken());
                intent.putExtra("id", getMsg().getId());
                intent.putExtra("msg", getMsg());
                startActivity(intent);
                return true;
            case R.id.menu_comment:

                intent = new Intent(this, WriteCommentActivity.class);
                intent.putExtra(Constants.TOKEN, getToken());
                intent.putExtra("id", getMsg().getId());
                intent.putExtra("msg", getMsg());
                startActivity(intent);

                return true;

            case R.id.menu_share:

                buildShareActionMenu();
                return true;
            case R.id.menu_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("sinaweibo", getMsg().getText()));
                Toast.makeText(this, getString(R.string.copy_successfully), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_fav:
                if (Utility.isTaskStopped(favTask) && Utility.isTaskStopped(unFavTask)) {
                    favTask = new FavAsyncTask(getToken(), msg.getId());
                    favTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
                return true;
            case R.id.menu_unfav:
                if (Utility.isTaskStopped(favTask) && Utility.isTaskStopped(unFavTask)) {
                    unFavTask = new UnFavAsyncTask(getToken(), msg.getId());
                    unFavTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
                return true;
            case R.id.menu_delete:
                RemoveWeiboMsgDialog dialog = new RemoveWeiboMsgDialog(msg.getId());
                dialog.show(getFragmentManager(), "");
                return true;
        }
        return false;
    }

    private void buildShareActionMenu() {
        Utility.setShareIntent(BrowserWeiboMsgActivity.this, shareActionProvider, msg);
    }

    @Override
    public void removeMsg(String id) {
        if (Utility.isTaskStopped(removeTask)) {
            removeTask = new RemoveTask(id);
            removeTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void updateCommentCount(int count) {
        msg.setComments_count(count);
        Intent intent = new Intent();
        intent.putExtra("msg", msg);
        setResult(0, intent);
    }

    public void updateRepostCount(int count) {
        msg.setReposts_count(count);
        Intent intent = new Intent();
        intent.putExtra("msg", msg);
        setResult(0, intent);
    }

    public String getToken() {
        return token;
    }

    public MessageBean getMsg() {
        return msg;
    }

    class RemoveTask extends MyAsyncTask<Void, Void, Boolean> {

        String id;

        WeiboException e;

        public RemoveTask(String id) {
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DestroyStatusDao dao = new DestroyStatusDao(token, id);
            try {
                return dao.destroy();
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
                return false;
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            if (this.e != null) {
                Toast.makeText(BrowserWeiboMsgActivity.this, e.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                finish();
            }
        }
    }

    private static class RefreshLoader extends AbstractAsyncNetRequestTaskLoader<MessageBean> {

        private String msgId;

        public RefreshLoader(Context context, String msgId) {
            super(context);
            this.msgId = msgId;
        }

        @Override
        protected MessageBean loadData() throws WeiboException {
            return new ShowStatusDao(GlobalContext.getInstance().getSpecialToken(), msgId).getMsg();

        }
    }

    private LoaderManager.LoaderCallbacks<AsyncTaskLoaderResult<MessageBean>> refreshCallback = new LoaderManager.LoaderCallbacks<AsyncTaskLoaderResult<MessageBean>>() {
        @Override
        public Loader<AsyncTaskLoaderResult<MessageBean>> onCreateLoader(int id, Bundle args) {
            return new RefreshLoader(BrowserWeiboMsgActivity.this, msgId);
        }

        @Override
        public void onLoadFinished(Loader<AsyncTaskLoaderResult<MessageBean>> loader,
                AsyncTaskLoaderResult<MessageBean> result) {
            MessageBean data = result != null ? result.data : null;
            final WeiboException exception = result != null ? result.exception : null;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    CommonProgressDialogFragment dialog = (CommonProgressDialogFragment) getSupportFragmentManager()
                            .findFragmentByTag(
                                    CommonProgressDialogFragment.class.getName());
                    if (dialog != null) {
                        dialog.dismiss();
                    }

                    if (exception != null) {
                        CommonErrorDialogFragment userInfoActivityErrorDialog = CommonErrorDialogFragment
                                .newInstance(exception.getError());
                        getSupportFragmentManager().beginTransaction()
                                .add(userInfoActivityErrorDialog, CommonErrorDialogFragment.class.getName()).commit();
                    }
                }
            });

            if (data != null) {
                BrowserWeiboMsgActivity.this.msg = data;
                buildContent();
            }
            getLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<AsyncTaskLoaderResult<MessageBean>> loader) {

        }
    };

}
