package com.g08.gig.persistence.real.Pro;

import com.g08.gig.persistence.real.TableSchema;

public final class ProTable implements TableSchema {
    public static final String TABLE_PRO = "pros";
    public static final String COL_PRO_ID = "id";
    public static final String COL_PRO_CITY_LOCATION = "cityOfOperation";

    @Override
    public String getTableName(){return TABLE_PRO;};

    @Override
    public String getCreateSql(){
        return "CREATE TABLE " + TABLE_PRO + "( " +
                COL_PRO_ID + " INTEGER PRIMARY KEY, " +
                COL_PRO_CITY_LOCATION + " TEXT NOT NULL, " +
                "FOREIGN KEY ("+ COL_PRO_ID + ") REFERENCES users(id) ON DELETE CASCADE);";
    }
}
