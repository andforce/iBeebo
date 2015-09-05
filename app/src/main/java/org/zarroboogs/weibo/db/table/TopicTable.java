
package org.zarroboogs.weibo.db.table;

public class TopicTable {

    public static final String TABLE_NAME = "topics_table";
    // support multi user,so primary key can't be message id
    public static final String ID = "_id";
    // support mulit user
    public static final String ACCOUNTID = "accountid";

    public static final String TOPIC_NAME = "topic_name";

}
