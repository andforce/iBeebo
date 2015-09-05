
package org.zarroboogs.weibo;

import org.zarroboogs.utils.crashmanager.CrashManager;
import org.zarroboogs.utils.crashmanager.CrashManagerConstants;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.GroupListBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.db.task.AccountDao;
import org.zarroboogs.weibo.db.task.GroupDBTask;
import org.zarroboogs.weibo.setting.SettingUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.Display;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BeeboApplication extends Application {


    public static BeeboApplication instance = null;

    // image size
    public Activity activity = null;

    public Activity currentRunningActivity = null;

    public DisplayMetrics displayMetrics = null;

    // image memory cache
    public LruCache<String, Bitmap> appBitmapCache = null;

    // current account info
    public AccountBean accountBean = null;


    public GroupListBean group = null;


    public Handler handler;

    public boolean tokenExpiredDialogIsShowing = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = (BeeboApplication) getApplicationContext();

        handler = new Handler();

        buildCache();
        CrashManagerConstants.loadFromContext(this);
        CrashManager.registerHandler();

    }

    public static BeeboApplication getInstance() {
        return instance;
    }

    public Handler getUIHandler() {
        return handler;
    }

    public GroupListBean getGroup() {
        if (group == null) {
            group = GroupDBTask.get(BeeboApplication.getInstance().getCurrentAccountId());
        }
        return group;
    }

    public void setGroup(GroupListBean group) {
        this.group = group;
    }

    public DisplayMetrics getDisplayMetrics() {
        if (displayMetrics != null) {
            return displayMetrics;
        } else {
            Activity a = getActivity();
            if (a != null) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                this.displayMetrics = metrics;
                return metrics;
            } else {
                // default screen is 800x480
                DisplayMetrics metrics = new DisplayMetrics();
                metrics.widthPixels = 480;
                metrics.heightPixels = 800;
                return metrics;
            }
        }
    }

    public void setAccountBean(final AccountBean accountBean) {
        this.accountBean = accountBean;
    }

    public void updateUserInfo(final UserBean userBean) {
        this.accountBean.setInfo(userBean);
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (AccountChangeListener listener : profileListenerSet) {
                    listener.onChange(userBean);
                }
            }
        });
    }

    public void updateAccountBean(){
    	String id = SettingUtils.getDefaultAccountId();
        if (!TextUtils.isEmpty(id)) {
            accountBean = AccountDao.getAccount(id);
        } else {
            List<AccountBean> accountList = AccountDao.getAccountList();
            if (accountList != null && accountList.size() > 0) {
                accountBean = accountList.get(0);
            }
        }
    }
    public AccountBean getAccountBean() {
        if (accountBean == null) {
            String id = SettingUtils.getDefaultAccountId();
            if (!TextUtils.isEmpty(id)) {
                accountBean = AccountDao.getAccount(id);
            } else {
                List<AccountBean> accountList = AccountDao.getAccountList();
                if (accountList != null && accountList.size() > 0) {
                    accountBean = accountList.get(0);
                }
            }
        }

        return accountBean;
    }

    private Set<AccountChangeListener> profileListenerSet = new HashSet<AccountChangeListener>();

    public void registerForAccountChangeListener(AccountChangeListener listener) {
        if (listener != null) {
            profileListenerSet.add(listener);
        }
    }

    public void unRegisterForAccountChangeListener(AccountChangeListener listener) {
        profileListenerSet.remove(listener);
    }

    public interface AccountChangeListener {

        void onChange(UserBean newUserBean);
    }

    public String getCurrentAccountId() {
        return getAccountBean().getUid();
    }

    public String getCurrentAccountName() {

        return getAccountBean().getUsernick();
    }

    public synchronized LruCache<String, Bitmap> getBitmapCache() {
        if (appBitmapCache == null) {
            buildCache();
        }
        return appBitmapCache;
    }

    public String getAccessToken() {
        if (getAccountBean() != null) {
            return getAccountBean().getAccess_token();
        } else {
            return "";
        }
    }
    
    public String getAccessTokenHack() {
        if (getAccountBean() != null) {
            return getAccountBean().getAccess_token_hack();
        } else {
            return "";
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getCurrentRunningActivity() {
        return currentRunningActivity;
    }

    public void setCurrentRunningActivity(Activity currentRunningActivity) {
        this.currentRunningActivity = currentRunningActivity;
    }

    private void buildCache() {
        int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

        int cacheSize = Math.max(1024 * 1024 * 8, 1024 * 1024 * memClass / 5);

        appBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getByteCount();
            }
        };
    }



    public boolean checkUserIsLogin() {
        return getInstance().getAccountBean() != null;
    }


    /**
     * @return the main context of the Application
     */
    public static Context getAppContext() {
        return instance;
    }

    /**
     * @return the main resources from the Application
     */
    public static Resources getAppResources() {
        if (instance == null)
            return null;
        return instance.getResources();
    }

}
