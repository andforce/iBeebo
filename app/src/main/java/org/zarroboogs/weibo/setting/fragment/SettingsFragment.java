package org.zarroboogs.weibo.setting.fragment;

import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.utils.CommUtils;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.RootUtils;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.AccountActivity;
import org.zarroboogs.weibo.db.AccountDatabaseManager;
import org.zarroboogs.weibo.db.table.AccountTable;
import org.zarroboogs.weibo.setting.activity.SettingActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsFragment extends PreferenceFragment {

    private Preference mRootPreference;

    private ProgressDialog progressDialog;

    private static final String TAG = "SettingsFragment";

    private static final String SINA_PREF_PATH = "/data/data/com.sina.weibo/shared_prefs/sina_push_pref.xml";
    private static final String SINA_APP_SETTING = "/data/data/com.sina.weibo/shared_prefs/app_setting.xml";

    public static final String APP_UA = "app_ua";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Constants.isBeeboPlus) {
            addPreferencesFromResource(R.xml.beebo_plus_setting_activity_pref);
        } else {
            addPreferencesFromResource(R.xml.setting_activity_pref);
        }

        if (false) {
            mRootPreference = findPreference("root_request_key");

            mRootPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("高级授权");
                    builder.setMessage("如果有任何疑问请不要使用该功能，点击确定开始授权，点击取消不进行任何操作！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (CommUtils.isInstalled(getActivity(), "com.sina.weibo")) {
                                        boolean isRoot = RootUtils.haveRoot();
                                        if (isRoot) {
                                            showProgressDialog();
                                        } else {
                                            dissmissProgressDialog();

                                        }

                                    } else {
                                        showSinaWeiboNotInstalledDialog();
                                    }

                                }
                            }).setNegativeButton("取消", null).create().show();
                    return false;
                }
            });
        }


        Preference myPref = findPreference(SettingActivity.CHANGE_WEIBO_ACCOUNT);
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO Auto-generated method stub
                showAccountSwitchPage();
                return false;
            }

        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在获取高级授权...");
        }

        progressDialog.show();

        String app_ua = "";
        String cat_app_ua = "cat " + SINA_APP_SETTING;
        String cmdResult = RootUtils.execRootCmd(cat_app_ua);
        String APP_UA_P = "<string name=\"app_ua\">.*";
        Pattern uaPattern = Pattern.compile(APP_UA_P);
        Matcher uaMather = uaPattern.matcher(cmdResult);
        if (uaMather.find()) {
            Context context = BeeboApplication.getAppContext();

            app_ua = cmdResult.substring(uaMather.start(), uaMather.end()).replace("</string>", "").replace("<string name=\"app_ua\">", "");
            SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            preferences.edit().putString(APP_UA, app_ua).apply();

        }


        String gsid = "";
        String uid = "";

        String cmd = "cat " + SINA_PREF_PATH;
        DevLog.printLog(TAG + "_ROOT_CMD", cmd);
        String result = RootUtils.execRootCmd(cmd);

        String GSID_P = "<string name=\"key.gsid\">.*";
        Pattern p = Pattern.compile(GSID_P);
        Matcher m = p.matcher(result);
        if (m.find()) {
            gsid = result.substring(m.start(), m.end()).replace("</string>", "").replace("<string name=\"key.gsid\">", "");
            DevLog.printLog(TAG + "_ROOT_CMD", gsid);
        }

        String UID_P = "<long name=\"key.uid.new\" value=.*";
        Pattern uidPattern = Pattern.compile(UID_P);
        Matcher uidMatcher = uidPattern.matcher(result);
        if (uidMatcher.find()) {
            uid = result.substring(uidMatcher.start(), uidMatcher.end()).replace("\" />", "").replace("<long name=\"key.uid.new\" value=\"", "");
            DevLog.printLog(TAG + "_ROOT_CMD", uid);
        }

        String accountUID = BeeboApplication.getInstance().getAccountBean().getUid();
        if (!accountUID.equals(uid)) {
            showAuthFailed();

        } else {
            AccountDatabaseManager manager = new AccountDatabaseManager(getActivity().getApplicationContext());
            manager.updateAccount(AccountTable.ACCOUNT_TABLE, uid, AccountTable.GSID, gsid);
            showAuthSuccess();
        }


    }


    private void showAuthSuccess() {
        progressDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(), "授权成功!!", Toast.LENGTH_LONG).show();
    }

    private void showAuthFailed() {
        progressDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(), "当前登录帐号和官方客户端登录的帐号不一致!", Toast.LENGTH_LONG).show();
    }

    private void dissmissProgressDialog() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        Toast.makeText(getActivity().getApplicationContext(), "的手机没有Root或者你不信任iBeebo", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

    private void showSinaWeiboNotInstalledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("错误").setMessage("你没有安装官方客户端，但这不是广告，只是为了获取官方的高级授权，授权完成之后可以卸载官方客户端，请安装并登录")
                .setPositiveButton("确定", null).create().show();
    }


    private void showAccountSwitchPage() {
        Intent intent = AccountActivity.newIntent();
        startActivity(intent);
    }
}
