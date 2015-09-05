
package org.zarroboogs.weibo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppsrcDatabaseHelper extends SQLiteOpenHelper {

    public static final String ID = "_id";

    // 这以下是Lession的数据库
    public static String TABLE_NAME_APPSRC = "APPSRC";
    public static String CODE = "code";
    public static String TEXT = "text";
    private static AppsrcDatabaseHelper mHelper;
    
    private static final int DB_VERSION = 4;

    public synchronized static AppsrcDatabaseHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new AppsrcDatabaseHelper(context);
        }
        return mHelper;
    }

    public AppsrcDatabaseHelper(Context context) {
        super(context, "appsrc.db", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String courseTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_APPSRC + "(" + ID + " integer primary key," + CODE
                + " varchar," + TEXT + " varchar"
                + ")";
        db.execSQL(courseTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    	if (oldVersion <= 3) {
            // 删除原来的数据表
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_APPSRC);

            // 重新创建
            onCreate(db);
		}

    }

}
