package com.g08.gig.persistence.real.user;

import com.g08.gig.persistence.real.TableSchema;

/**
 * Schema definition for the users table.
 */
public final class UserTable implements TableSchema {

    public static final String TABLE_USER = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_FIRST_NAME = "first_name";
    public static final String COL_USER_LAST_NAME = "last_name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";

    @Override
    public String getTableName() {
        return TABLE_USER;
    }

    @Override
    public String getCreateSql() {
        return "CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_FIRST_NAME + " TEXT NOT NULL, " +
                COL_USER_LAST_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT NOT NULL, " +
                COL_USER_PASSWORD + " TEXT" +
                ");";
    }
}
