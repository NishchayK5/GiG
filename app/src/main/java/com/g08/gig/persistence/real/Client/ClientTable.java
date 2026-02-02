package com.g08.gig.persistence.real.Client;
import com.g08.gig.persistence.real.TableSchema;

public final class ClientTable implements TableSchema{
    public static final String TABLE_CLIENT = "clients";
    public static final String COL_CLIENT_ID = "id";
    public static final String COL_ADDRESS = "address";

    @Override
    public String getTableName() {
        return TABLE_CLIENT;
    }

    @Override
    public String getCreateSql() {
        return "CREATE TABLE " + TABLE_CLIENT + " (" +
                COL_CLIENT_ID + " INTEGER PRIMARY KEY, " +
                COL_ADDRESS + " TEXT NOT NULL, " +
                "FOREIGN KEY ("+ COL_CLIENT_ID + ") REFERENCES users(id) ON DELETE CASCADE);";
    }
}
