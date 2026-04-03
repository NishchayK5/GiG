package com.g08.gig.persistence.real.Bid;

import com.g08.gig.persistence.real.Pro.ProTable;
import com.g08.gig.persistence.real.Request.RequestTable;
import com.g08.gig.persistence.real.TableSchema;

public class BidTable implements TableSchema {
    public static final String TABLE_BID = "bid";
    public static final String COL_BID_ID = "id";
    public static final String COL_BID_REQUEST_ID = "requestId";
    public static final String COL_BID_PRO_ID = "proId";
    public static final String COL_BID_COST = "costInCents";
    public static final String COL_BID_TIME_OF_BID = "timeOfBid";
    public static final String COL_BID_STATUS = "status";

    @Override
    public String getTableName(){return TABLE_BID;}

    @Override
    public String getCreateSql() {
        return "CREATE TABLE " + TABLE_BID + " (" +
                COL_BID_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BID_REQUEST_ID + " INTEGER NOT NULL, " +
                COL_BID_PRO_ID + " INTEGER NOT NULL, " +
                COL_BID_COST + " INTEGER NOT NULL, " +
                COL_BID_TIME_OF_BID  + " TEXT NOT NULL, " +
                COL_BID_STATUS + " INTEGER NOT NULL DEFAULT 0, " +
                " FOREIGN KEY (" + COL_BID_REQUEST_ID + ") REFERENCES " + RequestTable.TABLE_REQUEST + "(" + RequestTable.COL_REQUEST_ID + ") ON DELETE CASCADE," +
                " FOREIGN KEY (" + COL_BID_PRO_ID + ") REFERENCES " + ProTable.TABLE_PRO + "(" + ProTable.COL_PRO_ID + ") ON DELETE CASCADE)";
    }


}
