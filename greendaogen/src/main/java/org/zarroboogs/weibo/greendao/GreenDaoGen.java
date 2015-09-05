package org.zarroboogs.weibo.greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
import table.AccountTable;
import table.AtUsersTable;
import table.TimeLineStatusTable;

public class GreenDaoGen {

    public static final int DB_VERSION = 5;

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(DB_VERSION, "org.zarroboogs.weibo.greendao.bean");

        schema.setDefaultJavaPackageDao("org.zarroboogs.weibo.greendao.dao");

        addAccountTable(schema);

        addAtUsersTable(schema);

        addStatusTable(schema);

        new DaoGenerator().generateAll(schema, "app/src/main/java-greendao-gen");

    }

    // 001
    private static void addAccountTable(Schema schema) {

        Entity table = schema.addEntity("Green_AccountBean");
        table.setHasKeepSections(true);
        table.setTableName(AccountTable.ACCOUNT_TABLE);
        table.implementsSerializable();

        table.addLongProperty(AccountTable.UID).primaryKey();
        table.addStringProperty(AccountTable.USER_NAME);
        table.addStringProperty(AccountTable.USER_PWD);
        table.addStringProperty(AccountTable.COOKIE);
        table.addStringProperty(AccountTable.OAUTH_TOKEN);
        table.addLongProperty(AccountTable.OAUTH_TOKEN_EXPIRES_TIME);
        table.addStringProperty(AccountTable.ACCESS_TOKEN_HACK);
        table.addLongProperty(AccountTable.ACCESS_TOKEN_HACK_EXPIRES_TIME);
        table.addStringProperty(AccountTable.G_SID);
        table.addIntProperty(AccountTable.NAVIGATION_POSITION);
        table.addStringProperty(AccountTable.USER_INFO_JSON);
    }


    private static void addAtUsersTable(Schema schema) {

        Entity table = schema.addEntity("Green_AtUsersBean");
        table.setHasKeepSections(true);
        table.setTableName(AtUsersTable.TABLE_NAME);
        table.implementsSerializable();

        table.addStringProperty(AtUsersTable.ACCOUNTID);
        table.addStringProperty(AtUsersTable.AT_USERID);
        table.addStringProperty(AtUsersTable.AT_USER_INFO_JSON);
    }


    private static void addStatusTable(Schema schema) {

        Entity table = schema.addEntity("Green_TimeLineStatus");
        table.setHasKeepSections(true);
        table.setTableName(TimeLineStatusTable.TABLE_NAME);
        table.implementsSerializable();

        table.addIntProperty(TimeLineStatusTable.ID);
        table.addStringProperty(TimeLineStatusTable.ACCOUNTID);
        table.addStringProperty(TimeLineStatusTable.MBLOGID);
        table.addStringProperty(TimeLineStatusTable.JSONDATA);

    }


}
