package com.g08.gig.persistence.real;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.g08.gig.persistence.real.Client.ClientTable;
import com.g08.gig.persistence.real.Pro.ProTable;
import com.g08.gig.persistence.real.Request.RequestTable;
import com.g08.gig.persistence.real.user.UserTable;

public class AppDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "gig.db";
    public static final int DB_VERSION = 5;

    private static final TableSchema[] SCHEMA_TABLES = {
        new UserTable(), new ClientTable(), new ProTable(), new RequestTable()
    };

    public AppDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (TableSchema table : SCHEMA_TABLES) {
            db.execSQL(table.getCreateSql());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (TableSchema table : SCHEMA_TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table.getTableName());
        }
        onCreate(db);
    }
}
