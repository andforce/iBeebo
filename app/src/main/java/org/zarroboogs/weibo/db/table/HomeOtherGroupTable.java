
package org.zarroboogs.weibo.db.table;

public class HomeOtherGroupTable {

    public static final String TABLE_NAME = "home_other_group_table";
    // support multi user,so primary key can't be message id
    public static final String ID = "_id";
    // support mulit user
    public static final String ACCOUNTID = "accountid";
    // group id
    public static final String GROUPID = "groupid";

    public static final String TIMELINEDATA = "timelinedata";

    public static class HomeOtherGroupDataTable {

        public static final String TABLE_NAME = "home_other_group_data_table";
        // support multi user,so primary key can't be message id
        public static final String ID = "_id";
        // support mulit user
        public static final String ACCOUNTID = "accountid";
        // group id
        public static final String GROUPID = "groupid";
        // message id
        public static final String MBLOGID = "mblogid";

        public static final String JSONDATA = "json";
    }
}
