package com.g08.gig.persistence.real.Review;

import static com.g08.gig.persistence.real.Client.ClientTable.TABLE_CLIENT;

import com.g08.gig.models.Pro;
import com.g08.gig.persistence.real.Client.ClientTable;
import com.g08.gig.persistence.real.Pro.ProTable;
import com.g08.gig.persistence.real.TableSchema;

public class ReviewTable implements TableSchema {

    public static final String TABLE_REVIEW = "reviews";
    public static final String COL_REVIEW_ID = "id";
    public static final String COL_REVIEW_CLIENT_ID = "ClientId";
    public static final String COL_REVIEW_PRO_ID = "proId";
    public static final String COL_REVIEW_TITLE = "title";
    public static final String COL_REVIEW_BODY = "body";
    public static final String COL_REVIEW_STAR_RATING = "starRating";

    @Override
    public String getTableName(){return TABLE_REVIEW;}

    @Override
    public String getCreateSql(){
        return "CREATE TABLE " + TABLE_REVIEW + " (" +
                COL_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REVIEW_CLIENT_ID + " INTEGER NOT NULL, " +
                COL_REVIEW_PRO_ID + " INTEGER NOT NULL, " +
                COL_REVIEW_TITLE + " TEXT NOT NULL, " +
                COL_REVIEW_BODY + " TEXT NOT NULL, " +
                COL_REVIEW_STAR_RATING + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_REVIEW_CLIENT_ID + ") REFERENCES " + ClientTable.TABLE_CLIENT + "(" + ClientTable.COL_CLIENT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COL_REVIEW_PRO_ID + ") REFERENCES " + ProTable.TABLE_PRO + "(" + ProTable.COL_PRO_ID + ") ON DELETE CASCADE " +
                ");";
    }
}
