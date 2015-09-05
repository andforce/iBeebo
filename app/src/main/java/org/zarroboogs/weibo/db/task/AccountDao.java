
package org.zarroboogs.weibo.db.task;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.weibo.activity.OAuthActivity;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.db.DatabaseHelper;
import org.zarroboogs.weibo.db.table.AccountTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class AccountDao {

    private AccountDao() {

    }

    private static SQLiteDatabase getWsd() {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getWritableDatabase();
    }

    private static SQLiteDatabase getRsd() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getReadableDatabase();
    }

    public static OAuthActivity.DBResult updateAccountHackToken(AccountBean account, String toaken_hack, long token_hack_exp) {

        ContentValues cv = new ContentValues();
        cv.put(AccountTable.ACCESS_TOKEN_HACK, toaken_hack);
        // toaken_time 
        cv.put(AccountTable.ACCESS_TOKEN_HACK_EXPIRES_TIME, token_hack_exp);
        
        String[] args = {
                account.getUid()
        };
        getWsd().update(AccountTable.ACCOUNT_TABLE, cv, AccountTable.UID + "=?", args);
        return OAuthActivity.DBResult.update_successfully;
    }
    
    
    public static OAuthActivity.DBResult updateGSID(AccountBean account, String gsid) {

        ContentValues cv = new ContentValues();
        cv.put(AccountTable.GSID, gsid);
        
        String[] args = {
                account.getUid()
        };
        getWsd().update(AccountTable.ACCOUNT_TABLE, cv, AccountTable.UID + "=?", args);
        return OAuthActivity.DBResult.update_successfully;
    }
    
    
    public static OAuthActivity.DBResult addOrUpdateAccount(AccountBean account, boolean blackMagic) {

        ContentValues cv = new ContentValues();
        cv.put(AccountTable.UID, account.getUid());
        cv.put(AccountTable.OAUTH_TOKEN, account.getAccess_token());
        cv.put(AccountTable.OAUTH_TOKEN_EXPIRES_TIME, String.valueOf(account.getExpires_time()));
        cv.put(AccountTable.BLACK_MAGIC, blackMagic);
        // uname
        cv.put(AccountTable.USER_NAME, account.getUname());
        // pwd
        cv.put(AccountTable.USER_PWD, account.getPwd());
        // token_hack
        cv.put(AccountTable.ACCESS_TOKEN_HACK, account.getAccess_token_hack());
        // toaken_time 
        cv.put(AccountTable.ACCESS_TOKEN_HACK_EXPIRES_TIME, account.getExpires_time_hack());
        // gsid
        cv.put(AccountTable.GSID, account.getGsid());

        String json = new Gson().toJson(account.getInfo());
        cv.put(AccountTable.INFOJSON, json);

        Cursor c = getWsd().query(AccountTable.ACCOUNT_TABLE, null, AccountTable.UID + "=?", new String[] {
                account.getUid()
        }, null, null, null);

        if (c != null && c.getCount() > 0) {
            String[] args = {
                    account.getUid()
            };
            getWsd().update(AccountTable.ACCOUNT_TABLE, cv, AccountTable.UID + "=?", args);
            return OAuthActivity.DBResult.update_successfully;
        } else {

            getWsd().insert(AccountTable.ACCOUNT_TABLE, AccountTable.UID, cv);
            return OAuthActivity.DBResult.add_successfuly;
        }

    }

    public static void asyncUpdateMyProfile(final AccountBean accountBean, final UserBean value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateMyProfile(accountBean, value);
            }
        }).start();
    }

    public static void updateMyProfile(AccountBean account, UserBean value) {
        String uid = account.getUid();
        String json = new Gson().toJson(value);

        ContentValues cv = new ContentValues();
        cv.put(AccountTable.UID, uid);
        cv.put(AccountTable.INFOJSON, json);

        int c = getWsd().update(AccountTable.ACCOUNT_TABLE, cv, AccountTable.UID + "=?", new String[] {
                uid
        });
    }

    public static void updateNavigationPosition(AccountBean account, int position) {
        String uid = account.getUid();

        ContentValues cv = new ContentValues();
        cv.put(AccountTable.UID, uid);
        cv.put(AccountTable.NAVIGATION_POSITION, position);

        int c = getWsd().update(AccountTable.ACCOUNT_TABLE, cv, AccountTable.UID + "=?", new String[] {
                uid
        });
    }

    public static List<AccountBean> getAccountList() {
        List<AccountBean> accountList = new ArrayList<AccountBean>();
        String sql = "select * from " + AccountTable.ACCOUNT_TABLE;
        Cursor c = getWsd().rawQuery(sql, null);
        while (c.moveToNext()) {
            AccountBean account = new AccountBean();
            // uname
            int uname = c.getColumnIndex(AccountTable.USER_NAME);
            account.setUname(c.getString(uname));
            // pwd
            int pwd = c.getColumnIndex(AccountTable.USER_PWD);
            account.setPwd(c.getString(pwd));
            // cookie
            int cookie = c.getColumnIndex(AccountTable.COOKIE);
            account.setCookie(c.getString(cookie));
            
            // access_token_hack
            int access_token_hack_index = c.getColumnIndex(AccountTable.ACCESS_TOKEN_HACK);
            account.setAccess_token_hack(c.getString(access_token_hack_index));

            // gsid
            int gsid = c.getColumnIndex(AccountTable.GSID);
            account.setGsid(c.getString(gsid));
            
            
            int colid = c.getColumnIndex(AccountTable.OAUTH_TOKEN);
            account.setAccess_token(c.getString(colid));

            colid = c.getColumnIndex(AccountTable.OAUTH_TOKEN_EXPIRES_TIME);
            account.setExpires_time(Long.valueOf(c.getString(colid)));

            colid = c.getColumnIndex(AccountTable.BLACK_MAGIC);
            account.setBlack_magic(c.getInt(colid) == 1);

            colid = c.getColumnIndex(AccountTable.NAVIGATION_POSITION);
            account.setNavigationPosition(c.getInt(colid));

            Gson gson = new Gson();
            String json = c.getString(c.getColumnIndex(AccountTable.INFOJSON));
            try {
                UserBean value = gson.fromJson(json, UserBean.class);
                account.setInfo(value);
            } catch (JsonSyntaxException e) {
                AppLoggerUtils.e(e.getMessage());
            }

            accountList.add(account);
        }
        c.close();
        return accountList;
    }

    public static UserBean getUserBean(String id) {

        String sql = "select * from " + AccountTable.ACCOUNT_TABLE + " where " + AccountTable.UID + " = " + id;
        Cursor c = getRsd().rawQuery(sql, null);
        UserBean value = null;
        if (c.moveToNext()) {
            Gson gson = new Gson();
            String json = c.getString(c.getColumnIndex(AccountTable.INFOJSON));
            try {
                value = gson.fromJson(json, UserBean.class);
            } catch (JsonSyntaxException e) {
                AppLoggerUtils.e(e.getMessage());
            }
            return value;
        }
        return null;

    }

    public static AccountBean getAccount(String id) {

        String sql = "select * from " + AccountTable.ACCOUNT_TABLE + " where " + AccountTable.UID + " = " + id;
        Cursor c = getRsd().rawQuery(sql, null);
        if (c.moveToNext()) {
            AccountBean account = new AccountBean();
            int colid = c.getColumnIndex(AccountTable.OAUTH_TOKEN);
            account.setAccess_token(c.getString(colid));

            colid = c.getColumnIndex(AccountTable.COOKIE);
            account.setCookie(c.getString(colid));

            colid = c.getColumnIndex(AccountTable.USER_NAME);
            account.setUname(c.getString(colid));

            // pwd
            colid = c.getColumnIndex(AccountTable.USER_PWD);
            account.setPwd(c.getString(colid));
            
            // hack token
            colid = c.getColumnIndex(AccountTable.ACCESS_TOKEN_HACK);
            account.setAccess_token_hack(c.getString(colid));

            colid = c.getColumnIndex(AccountTable.GSID);
            account.setGsid(c.getString(colid));
            
            colid = c.getColumnIndex(AccountTable.OAUTH_TOKEN_EXPIRES_TIME);
            account.setExpires_time(Long.valueOf(c.getString(colid)));

            colid = c.getColumnIndex(AccountTable.BLACK_MAGIC);
            account.setBlack_magic(c.getInt(colid) == 1);

            colid = c.getColumnIndex(AccountTable.NAVIGATION_POSITION);
            account.setNavigationPosition(c.getInt(colid));

            Gson gson = new Gson();
            String json = c.getString(c.getColumnIndex(AccountTable.INFOJSON));
            try {
                UserBean value = gson.fromJson(json, UserBean.class);
                account.setInfo(value);
            } catch (JsonSyntaxException e) {
                AppLoggerUtils.e(e.getMessage());
            }

            return account;
        }
        return null;

    }

    public static List<AccountBean> removeAndGetNewAccountList(Set<String> checkedItemPosition) {
        String[] args = checkedItemPosition.toArray(new String[0]);
        String asString = Arrays.toString(args);
        asString = asString.replace("[", "(");
        asString = asString.replace("]", ")");

        String sql = "delete from " + AccountTable.ACCOUNT_TABLE + " where " + AccountTable.UID + " in " + asString;

        getWsd().execSQL(sql);

        for (String id : args) {
            FriendsTimeLineDBTask.deleteAllHomes(id);
            MentionWeiboTimeLineDBTask.deleteAllReposts(id);
            MentionCommentsTimeLineDBTask.deleteAllComments(id);
            CommentToMeTimeLineDBTask.deleteAllComments(id);
            CommentByMeTimeLineDBTask.deleteAllComments(id);
            MyStatusDBTask.clear(id);
            AtUsersDBTask.clear(id);
            FavouriteDBTask.deleteAllFavourites(id);
        }

        return getAccountList();
    }
}
