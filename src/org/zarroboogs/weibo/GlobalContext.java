
package org.zarroboogs.weibo;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.zarroboogs.utils.crashmanager.CrashManager;
import org.zarroboogs.utils.crashmanager.CrashManagerConstants;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.GroupListBean;
import org.zarroboogs.weibo.bean.MusicInfoBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.db.task.AccountDBTask;
import org.zarroboogs.weibo.db.task.GroupDBTask;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.SmileyMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.Display;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GlobalContext extends Application {
    public static GlobalContext instance;

    public final static String SLEEP_INTENT = "org.videolan.vlc.SleepIntent";

    // singleton
    public static GlobalContext globalContext = null;

    // image size
    public Activity activity = null;

    public Activity currentRunningActivity = null;

    public DisplayMetrics displayMetrics = null;

    // image memory cache
    public LruCache<String, Bitmap> appBitmapCache = null;

    // current account info
    public AccountBean accountBean = null;

    public LinkedHashMap<Integer, LinkedHashMap<String, Bitmap>> emotionsPic = null;

    public GroupListBean group = null;

    public MusicInfoBean musicInfo = null;

    public Handler handler;

    public boolean tokenExpiredDialogIsShowing = false;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = (GlobalContext) getApplicationContext();
        instance = (GlobalContext) getApplicationContext();

        handler = new Handler();
        emotionsPic = new LinkedHashMap<Integer, LinkedHashMap<String, Bitmap>>();

        initImageLoader(getApplicationContext());

        buildCache();
        CrashManagerConstants.loadFromContext(this);
        CrashManager.registerHandler();
        if (Utility.isCertificateFingerprintCorrect(this)) {
            // Crashlytics.start(this);
        }
    }

    public static GlobalContext getInstance() {
        return globalContext;
    }

    public Handler getUIHandler() {
        return handler;
    }

    public GroupListBean getGroup() {
        if (group == null) {
            group = GroupDBTask.get(GlobalContext.getInstance().getCurrentAccountId());
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
                for (MyProfileInfoChangeListener listener : profileListenerSet) {
                    listener.onChange(userBean);
                }
            }
        });
    }

    public void updateAccountBean(){
    	String id = SettingUtils.getDefaultAccountId();
        if (!TextUtils.isEmpty(id)) {
            accountBean = AccountDBTask.getAccount(id);
        } else {
            List<AccountBean> accountList = AccountDBTask.getAccountList();
            if (accountList != null && accountList.size() > 0) {
                accountBean = accountList.get(0);
            }
        }
    }
    public AccountBean getAccountBean() {
        if (accountBean == null) {
            String id = SettingUtils.getDefaultAccountId();
            if (!TextUtils.isEmpty(id)) {
                accountBean = AccountDBTask.getAccount(id);
            } else {
                List<AccountBean> accountList = AccountDBTask.getAccountList();
                if (accountList != null && accountList.size() > 0) {
                    accountBean = accountList.get(0);
                }
            }
        }

        return accountBean;
    }

    private Set<MyProfileInfoChangeListener> profileListenerSet = new HashSet<MyProfileInfoChangeListener>();

    public void registerForAccountChangeListener(MyProfileInfoChangeListener listener) {
        if (listener != null) {
            profileListenerSet.add(listener);
        }
    }

    public void unRegisterForAccountChangeListener(MyProfileInfoChangeListener listener) {
        profileListenerSet.remove(listener);
    }

    public static interface MyProfileInfoChangeListener {

        public void onChange(UserBean newUserBean);
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

    public synchronized Map<String, Bitmap> getEmotionsPics() {
        if (emotionsPic != null && emotionsPic.size() > 0) {
            return emotionsPic.get(SmileyMap.GENERAL_EMOTION_POSITION);
        } else {
            getEmotionsTask();
            return emotionsPic.get(SmileyMap.GENERAL_EMOTION_POSITION);
        }
    }

    public synchronized Map<String, Bitmap> getHuahuaPics() {
        if (emotionsPic != null && emotionsPic.size() > 0) {
            return emotionsPic.get(SmileyMap.HUAHUA_EMOTION_POSITION);
        } else {
            getEmotionsTask();
            return emotionsPic.get(SmileyMap.HUAHUA_EMOTION_POSITION);
        }
    }

    private void getEmotionsTask() {
        Map<String, String> general = SmileyMap.getInstance().getGeneral();
        emotionsPic.put(SmileyMap.GENERAL_EMOTION_POSITION, getEmotionsTask(general));
        Map<String, String> huahua = SmileyMap.getInstance().getHuahua();
        emotionsPic.put(SmileyMap.HUAHUA_EMOTION_POSITION, getEmotionsTask(huahua));
    }

    private LinkedHashMap<String, Bitmap> getEmotionsTask(Map<String, String> emotionMap) {
        List<String> index = new ArrayList<String>();
        index.addAll(emotionMap.keySet());
        LinkedHashMap<String, Bitmap> bitmapMap = new LinkedHashMap<String, Bitmap>();
        for (String str : index) {
            String name = emotionMap.get(str);
            AssetManager assetManager = GlobalContext.getInstance().getAssets();
            InputStream inputStream;
            try {
                inputStream = assetManager.open(name);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                            Utility.dip2px(getResources().getInteger(R.integer.emotion_size)),
                            Utility.dip2px(getResources().getInteger(R.integer.emotion_size)), true);
                    if (bitmap != scaledBitmap) {
                        bitmap.recycle();
                        bitmap = scaledBitmap;
                    }
                    bitmapMap.put(str, bitmap);
                }
            } catch (IOException ignored) {

            }
        }

        return bitmapMap;
    }

    public void updateMusicInfo(MusicInfoBean musicInfo) {
        if (musicInfo == null) {
            musicInfo = new MusicInfoBean();
        }
        this.musicInfo = musicInfo;
    }

    public MusicInfoBean getMusicInfo() {
        return musicInfo;
    }

    public boolean checkUserIsLogin() {
        return getInstance().getAccountBean() != null;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory().memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 * 1024))
                .diskCacheFileCount(Integer.MAX_VALUE)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                // .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
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
