
package org.zarroboogs.weibo.db;

import org.zarroboogs.weibo.db.table.AtUsersTable;

import android.database.sqlite.SQLiteDatabase;

public class Upgrade35to36 {

    public static void upgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + AtUsersTable.TABLE_NAME);

        db.execSQL(DatabaseHelper.CREATE_ATUSERS_TABLE_SQL);
    }

}
