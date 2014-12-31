package org.zarroboogs.weibo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AccountDatabaseManager {

	private DatabaseHelper mHelper;
	private SQLiteDatabase mSqLiteDatabase;

	private static AccountDatabaseManager mDatabaseManager;

	public static AccountDatabaseManager getInstance(Context context) {
		if (mDatabaseManager == null) {
			mDatabaseManager = new AccountDatabaseManager(context);
		}
		return mDatabaseManager;
	}

	public AccountDatabaseManager(Context context) {
		super();
		// TODO Auto-generated constructor stub
		mHelper = new DatabaseHelper(context);
		mSqLiteDatabase = mHelper.getWritableDatabase();
	}

	public void updateDB(String tableName, int id, String key, String value) {
		String tmpValue = "\"" + value + "\"";
		mSqLiteDatabase.execSQL("UPDATE " + tableName + " SET " + key + "=" + tmpValue + " WHERE id=?", new String[] { "" + id });
	}

	public void updateAccount(String tableName, String uid, String key, String value) {
		String tmpValue = "\"" + value + "\"";
		mSqLiteDatabase.execSQL("UPDATE " + tableName + " SET " + key + "=" + tmpValue + " WHERE uid=?", new String[] { "" + uid });
	}
}
