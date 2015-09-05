package table;

/**
 * Created by andforce on 15/8/30.
 */
public class TimeLineStatusTable {

    public static final String TABLE_NAME = "time_line_status_table";

    // support multi user,so primary key can't be message id
    public static final String ID = "_id";
    // support mulit user
    public static final String ACCOUNTID = "accountid";
    // message id
    public static final String MBLOGID = "mblogid";

    public static final String JSONDATA = "json";

}
