package org.zarroboogs.weibo.db;

import java.util.ArrayList;
import java.util.List;

import org.zarroboogs.weibo.bean.WeiboWeiba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppsrcDatabaseManager {

	private AppsrcDatabaseHelper mHelper;
	private SQLiteDatabase mSqLiteDatabase;

	private static AppsrcDatabaseManager mDatabaseManager;

	public static AppsrcDatabaseManager getInstance(Context context) {
		if (mDatabaseManager == null) {
			mDatabaseManager = new AppsrcDatabaseManager(context);
		}
		return mDatabaseManager;
	}

	public AppsrcDatabaseManager(Context context) {
		super();
		// TODO Auto-generated constructor stub
		mHelper = AppsrcDatabaseHelper.getInstance(context);
		mSqLiteDatabase = mHelper.getWritableDatabase();
	}

	public long insertCategoryTree(int categoryID, String code, String text) {
		ContentValues cv = new ContentValues();

		cv.put(AppsrcDatabaseHelper.CODE, code);
		cv.put(AppsrcDatabaseHelper.TEXT, text);

		long row = mSqLiteDatabase.insert(AppsrcDatabaseHelper.TABLE_NAME_APPSRC, null, cv);

		return row;

	}

	public void updateDB(String tableName, int id, String key, String value) {
		String tmpValue = "\"" + value + "\"";
		mSqLiteDatabase.execSQL("UPDATE " + tableName + " SET " + key + "=" + tmpValue + " WHERE id=?", new String[] { "" + id });
	}

	public WeiboWeiba searchAppsrcByCode(String code) {
		Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + AppsrcDatabaseHelper.TABLE_NAME_APPSRC + " where " + AppsrcDatabaseHelper.CODE + " = ?",
				new String[] { code });
		
		while (cursor != null && cursor.moveToNext()) {
			WeiboWeiba lession = new WeiboWeiba();
			lession.setCode(cursor.getString(cursor.getColumnIndex(AppsrcDatabaseHelper.CODE)));
			lession.setText(cursor.getString(cursor.getColumnIndex(AppsrcDatabaseHelper.TEXT)));
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			return lession;
		}
		return null;
	}

	public List<WeiboWeiba> fetchAllAppsrc() {
		Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + AppsrcDatabaseHelper.TABLE_NAME_APPSRC, null);
		List<WeiboWeiba> lessions = new ArrayList<WeiboWeiba>();
		while (cursor != null && cursor.moveToNext()) {
			WeiboWeiba lession = new WeiboWeiba();
			lession.setCode(cursor.getString(cursor.getColumnIndex(AppsrcDatabaseHelper.CODE)));
			lession.setText(cursor.getString(cursor.getColumnIndex(AppsrcDatabaseHelper.TEXT)));

			lessions.add(lession);
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		};
		return lessions;
	}

}
