
package org.zarroboogs.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Utility {

    private Utility() {
        // Forbidden being instantiated.
    }

    public static String encodeUrl(Map<String, String> param) {
        if (param == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        Set<String> keys = param.keySet();
        boolean first = true;

        for (String key : keys) {
            String value = param.get(key);
            // pain...EditMyProfileDao params' values can be empty
            if (!TextUtils.isEmpty(value) || key.equals("description") || key.equals("url")) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }
                try {
                    sb.append(URLEncoder.encode(key, "UTF-8")).append("=")
                            .append(URLEncoder.encode(param.get(key), "UTF-8"));
                } catch (UnsupportedEncodingException e) {

                }
            }

        }

        return sb.toString();
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                try {
                    params.putString(URLDecoder.decode(v[0], "UTF-8"), URLDecoder.decode(v[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();

                }
            }
        }
        return params;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {

            }
        }
    }

    /**
     * Parse a URL query and fragment parameters into a key-value bundle.
     */
    public static Bundle parseUrl(String url) {
        // hack to prevent MalformedURLException
        url = url.replace("weiboconnect", "http");
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }

    public static int length(String paramString) {
        int i = 0;
        for (int j = 0; j < paramString.length(); j++) {
            if (paramString.substring(j, j + 1).matches("[Α-￥]")) {
                i += 2;
            } else {
                i++;
            }
        }

        if (i % 2 > 0) {
            i = 1 + i / 2;
        } else {
            i = i / 2;
        }

        return i;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    public static int getNetType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return networkInfo.getType();
        }
        return -1;
    }

    public static boolean isGprs(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSystemRinger(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    public static String getPicPathFromUri(Uri uri, Activity activity) {
        String value = uri.getPath();

        if (value.startsWith("/external")) {
            String[] proj = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return value;
        }
    }

    public static boolean isAllNotNull(Object... obs) {
        for (int i = 0; i < obs.length; i++) {
            if (obs[i] == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean isIntentSafe(Activity activity, Uri uri) {
        Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }

    public static boolean isIntentSafe(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return activities.size() > 0;
    }

    public static boolean isGooglePlaySafe(Activity activity) {
        Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms");
        Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
        mapCall.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        mapCall.setPackage("com.android.vending");
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }

    public static boolean isSinaWeiboSafe(Activity activity) {
        Intent mapCall = new Intent("com.sina.weibo.remotessoservice");
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> services = packageManager.queryIntentServices(mapCall, 0);
        return services.size() > 0;
    }

    public static String buildTabText(int number) {

        if (number == 0) {
            return null;
        }

        String num;
        if (number < 99) {
            num = "(" + number + ")";
        } else {
            num = "(99+)";
        }
        return num;

    }

    public static boolean isL() {
        return Build.VERSION.SDK_INT >= 20;
    }

    public static Uri getLatestCameraPicture(Activity activity) {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if (cursor.moveToFirst()) {
            String path = cursor.getString(1);
            return Uri.fromFile(new File(path));
        }
        return null;
    }

    public static void copyFile(InputStream in, File destFile) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        FileOutputStream outputStream = new FileOutputStream(destFile);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bufferedInputStream.read(buffer)) != -1) {
            bufferedOutputStream.write(buffer, 0, len);
        }
        closeSilently(bufferedInputStream);
        closeSilently(bufferedOutputStream);
    }

    public static Rect locateView(View v) {
        int[] location = new int[2];
        if (v == null) {
            return null;
        }
        try {
            v.getLocationOnScreen(location);
        } catch (NullPointerException npe) {
            // Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect locationRect = new Rect();
        locationRect.left = location[0];
        locationRect.top = location[1];
        locationRect.right = locationRect.left + v.getWidth();
        locationRect.bottom = locationRect.top + v.getHeight();
        return locationRect;
    }

    public static int countWord(String content, String word, int preCount) {
        int count = preCount;
        int index = content.indexOf(word);
        if (index == -1) {
            return count;
        } else {
            count++;
            return countWord(content.substring(index + word.length()), word, count);
        }
    }

    public static void setShareIntent(Activity activity, ShareActionProvider mShareActionProvider, String content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        if (Utility.isIntentSafe(activity, shareIntent) && mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }

    }

    public static void buildTabCount(ActionBar.Tab tab, String tabStrRes, int count) {
        if (tab == null) {
            return;
        }
        String content = tab.getText().toString();
        int value = 0;
        int start = content.indexOf("(");
        int end = content.lastIndexOf(")");
        if (start > 0) {
            String result = content.substring(start + 1, end);
            value = Integer.valueOf(result);
        }
        if (value <= count) {
            tab.setText(tabStrRes + "(" + count + ")");
        }
    }

    public static void buildTabCount(TextView tab, String tabStrRes, int count) {
        if (tab == null) {
            return;
        }
        // String content = tab.getText().toString();
        // int value = 0;
        // int start = content.indexOf("(");
        // int end = content.lastIndexOf(")");
        // if (start > 0) {
        // String result = content.substring(start + 1, end);
        // value = Integer.valueOf(result);
        // }
        // if (value <= count) {
        tab.setText(" " + count + " " + tabStrRes);
        // }
    }

    public static String getIdFromWeiboAccountLink(String url) {

        url = convertWeiboCnToWeiboCom(url);

        String id = url.substring("http://weibo.com/u/".length());
        id = id.replace("/", "");
        return id;
    }

    public static String getDomainFromWeiboAccountLink(String url) {
        url = convertWeiboCnToWeiboCom(url);

        final String NORMAL_DOMAIN_PREFIX = "http://weibo.com/";
        final String ENTERPRISE_DOMAIN_PREFIX = "http://e.weibo.com/";

        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("Url can't be empty");
        }

        if (!url.startsWith(NORMAL_DOMAIN_PREFIX) && !url.startsWith(ENTERPRISE_DOMAIN_PREFIX)) {
            throw new IllegalArgumentException("Url must start with " + NORMAL_DOMAIN_PREFIX + " or "
                    + ENTERPRISE_DOMAIN_PREFIX);
        }

        String domain = null;
        if (url.startsWith(ENTERPRISE_DOMAIN_PREFIX)) {
            domain = url.substring(ENTERPRISE_DOMAIN_PREFIX.length());

        } else if (url.startsWith(NORMAL_DOMAIN_PREFIX)) {
            domain = url.substring(NORMAL_DOMAIN_PREFIX.length());
        }
        domain = domain.replace("/", "");
        return domain;
    }

    public static boolean isWeiboAccountIdLink(String url) {
        url = convertWeiboCnToWeiboCom(url);

        return !TextUtils.isEmpty(url) && url.startsWith("http://weibo.com/u/");
    }

    // todo need refactor...
    public static boolean isWeiboAccountDomainLink(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        } else {
            url = convertWeiboCnToWeiboCom(url);
            boolean a = url.startsWith("http://weibo.com/") || url.startsWith("http://e.weibo.com/");
            boolean b = !url.contains("?");

            String tmp = url;
            if (tmp.endsWith("/")) {
                tmp = tmp.substring(0, tmp.lastIndexOf("/"));
            }

            int count = 0;
            char[] value = tmp.toCharArray();
            for (char c : value) {
                if ("/".equalsIgnoreCase(String.valueOf(c))) {
                    count++;
                }
            }
            return a && b && count == 3 && !"http://weibo.com/pub".equals(tmp);
        }
    }

    // http://www.weibo.com/2125954191/Aj3W9z25s
    public static boolean isWeiboMid(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        } else {
            url = convertWeiboCnToWeiboCom(url);
            boolean urlValide = url.startsWith("http://weibo.com/") || url.startsWith("http://e.weibo.com/");

            if (!urlValide) {
                return false;
            }

            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }

            if (url.contains("http://weibo.com/")) {
                url = url.substring("http://weibo.com/".length(), url.length());
            } else {
                url = url.substring("http://e.weibo.com/".length(), url.length());
            }

            String[] result = url.split("/");

            return result != null && result.length == 2;

        }
    }

    public static String getMidFromUrl(String url) {
        url = convertWeiboCnToWeiboCom(url);

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if (url.contains("http://weibo.com/")) {
            url = url.substring("http://weibo.com/".length(), url.length());
        } else {
            url = url.substring("http://e.weibo.com/".length(), url.length());
        }

        return url.split("/")[1];
    }

    private static String convertWeiboCnToWeiboCom(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("http://weibo.cn")) {
                url = url.replace("http://weibo.cn", "http://weibo.com");
            } else if (url.startsWith("http://www.weibo.com")) {
                url = url.replace("http://www.weibo.com", "http://weibo.com");
            } else if (url.startsWith("http://www.weibo.cn")) {
                url = url.replace("http://www.weibo.cn", "http://weibo.com");
            }
        }
        return url;
    }

    public static void vibrate(Context context, View view) {
        // Vibrator vibrator = (Vibrator)
        // context.getSystemService(Context.VIBRATOR_SERVICE);
        // vibrator.vibrate(30);
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    public static void playClickSound(View view) {
        view.playSoundEffect(SoundEffectConstants.CLICK);
    }

    public static View getListViewItemViewFromPosition(ListView listView, int position) {
        return listView.getChildAt(position - listView.getFirstVisiblePosition());
    }

    public static void setListViewSelectionFromTop(final ListView listView, final int positionAfterRefresh, final int top,
                                                   final Runnable runnable) {
        listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                listView.getViewTreeObserver().removeOnPreDrawListener(this);
                listView.setSelectionFromTop(positionAfterRefresh, top);
                if (runnable != null) {
                    runnable.run();
                }
                return false;
            }
        });
    }

    public static void setListViewSelectionFromTop(final ListView listView, final int positionAfterRefresh, final int top) {
        setListViewSelectionFromTop(listView, positionAfterRefresh, top, null);
    }

    public static String getMotionEventStringName(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return "MotionEvent.ACTION_DOWN";
            case MotionEvent.ACTION_UP:
                return "MotionEvent.ACTION_UP";
            case MotionEvent.ACTION_CANCEL:
                return "MotionEvent.ACTION_CANCEL";
            case MotionEvent.ACTION_MOVE:
                return "MotionEvent.ACTION_MOVE";
            default:
                return "Other";
        }
    }

    public static int getMaxLeftWidthOrHeightImageViewCanRead(int heightOrWidth) {
        // 1pixel==4bytes
        // http://stackoverflow.com/questions/13536042/android-bitmap-allocating-16-bytes-per-pixel
        // http://stackoverflow.com/questions/15313807/android-maximum-allowed-width-height-of-bitmap
        int[] maxSizeArray = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxSizeArray, 0);

        if (maxSizeArray[0] == 0) {
            GLES10.glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE, maxSizeArray, 0);
        }
        int maxHeight = maxSizeArray[0];
        int maxWidth = maxSizeArray[0];

        return (maxHeight * maxWidth) / heightOrWidth;
    }

    // sometime can get value, sometime can't, so I define it is 2048x2048
    public static int getBitmapMaxWidthAndMaxHeight() {
        int[] maxSizeArray = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxSizeArray, 0);

        if (maxSizeArray[0] == 0) {
            GLES10.glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE, maxSizeArray, 0);
        }
        // return maxSizeArray[0];
        return 2048;
    }

    public static boolean doThisDeviceOwnNavigationBar(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        return !hasMenuKey && !hasBackKey;
    }

    /**
     * https://svn.apache.org/repos/asf/cayenne/main/branches/cayenne-jdk1.5- generics
     * -unpublished/src/main/java/org/apache/cayenne/conf/Rot47PasswordEncoder .java
     */
    public static String rot47(String value) {
        int length = value.length();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);

            // Process letters, numbers, and symbols -- ignore spaces.
            if (c != ' ') {
                // Add 47 (it is ROT-47, after all).
                c += 47;

                // If character is now above printable range, make it printable.
                // Range of printable characters is ! (33) to ~ (126). A value
                // of 127 (just above ~) would therefore get rotated down to a
                // 33 (the !). The value 94 comes from 127 - 33 = 94, which is
                // therefore the value that needs to be subtracted from the
                // non-printable character to put it into the correct printable
                // range.
                if (c > '~') {
                    c -= 94;
                }
            }

            result.append(c);
        }

        return result.toString();
    }

    public static void runUIActionDelayed(Runnable runnable, long delayMillis) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }

    // long click link(schedule show dialog event), press home button(onPause
    // onSaveInstance), show dialog,then crash....
    // executePendingTransactions still occur crash
    public static void forceShowDialog(FragmentActivity activity, DialogFragment dialogFragment) {
        try {
            dialogFragment.show(activity.getSupportFragmentManager(), "");
            activity.getSupportFragmentManager().executePendingTransactions();
        } catch (Exception ignored) {

        }
    }

    public static Bitmap getBitmapFromView(View view) {

        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        return b;
    }
}
