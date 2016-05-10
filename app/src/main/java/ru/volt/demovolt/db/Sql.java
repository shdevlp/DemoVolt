package ru.volt.demovolt.db;

/**
 * Created by dave on 09.05.16.
 */
public class Sql {
    public final static String TBL_RECORD    = "record";
    public final static String TBL_FAVORITES = "favorites";
    public final static String FLD_SYSTEM_ID = "systemId";
    public final static String FLD_USER_ID   = "userId";
    public final static String FLD_RECORD_ID = "id";
    public final static String FLD_TITLE     = "title";
    public final static String FLD_BODY      = "body";
    public final static String FLD_FAVORITES = "favState";

    public final static String sqlTableRecord = "create table " + TBL_RECORD + "("
            + FLD_SYSTEM_ID + " integer not null primary key autoincrement,"
            + FLD_USER_ID   + " integer,"
            + FLD_RECORD_ID + " integer,"
            + FLD_TITLE     + " text,"
            + FLD_BODY      + " text)";

    public final static String sqlTableFavorites = "create table " + TBL_FAVORITES + "("
            + FLD_SYSTEM_ID + " integer not null primary key autoincrement,"
            + FLD_RECORD_ID + " integer not null,"
            + FLD_FAVORITES + " integer)";

    public final static String sqlDropTableRecord      = "drop table " + TBL_RECORD;
    public final static String sqlDropTableFavorites   = "drop table " + TBL_FAVORITES;
    public final static String sqlDeleteTableRecord    = "delete from " + TBL_RECORD;
    public final static String sqlDeleteTableFavorites = "delete from " + TBL_FAVORITES;
}
