
package org.zarroboogs.weibo.db.table;

public class CommentByMeTable {

    public static final String TABLE_NAME = "comment_by_me_table";
    // support multi user,so primary key can't be message id
    public static final String ID = "_id";
    // support mulit user
    public static final String ACCOUNTID = "accountid";

    public static final String TIMELINEDATA = "timelinedata";

    public static class CommentByMeDataTable {

        public static final String TABLE_NAME = "comment_by_me_data_table";
        // support multi user,so primary key can't be message id
        public static final String ID = "_id";
        // support mulit user
        public static final String ACCOUNTID = "accountid";
        // message id
        public static final String MBLOGID = "mblogid";

        public static final String JSONDATA = "json";

    }
}
