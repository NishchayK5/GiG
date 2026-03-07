package com.g08.gig.persistence.real.Request;


import com.g08.gig.persistence.real.Client.ClientTable;
import com.g08.gig.persistence.real.TableSchema;

public class RequestTable implements TableSchema {
    public static final String TABLE_REQUEST = "requests";
    public static final String COL_REQUEST_ID = "id";
    public static final String COL_REQUEST_CLIENT_ID = "clientId";
    public static final String COL_REQUEST_TITLE = "title";
    public static final String COL_REQUEST_BODY = "body";
    public static final String COL_REQUEST_TIME_OF_REQUEST = "timeOfRequest";
    public static final String COL_REQUEST_STATUS = "status";

    @Override
    public String getTableName(){return TABLE_REQUEST;}

    @Override
    public String getCreateSql(){
        return "CREATE TABLE " + TABLE_REQUEST + "( " +
                COL_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REQUEST_CLIENT_ID + " INTEGER, " +
                COL_REQUEST_TITLE + " TEXT NOT NULL, " +
                COL_REQUEST_BODY + " TEXT NOT NULL, " +
                COL_REQUEST_TIME_OF_REQUEST + " TEXT NOT NULL, " +
                COL_REQUEST_STATUS + " INTEGER NOT NULL DEFAULT 1, " +
                "FOREIGN KEY("+ COL_REQUEST_CLIENT_ID +") REFERENCES " + ClientTable.TABLE_CLIENT + "(" + ClientTable.COL_CLIENT_ID + ") ON DELETE CASCADE);";
    }
}
