package com.g08.gig.persistence.real.user;

import com.g08.gig.persistence.real.TableSchema;

public final class UserTable implements TableSchema {

    public static final String TABLE_USER = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_FIRST_NAME = "first_name";
    public static final String COL_USER_LAST_NAME = "last_name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PHONE = "phoneNumber";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ROLE = "role";

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
                COL_USER_PHONE + " TEXT NOT NULL, " +
                COL_USER_ROLE + " TEXT NOT NULL, " +
                COL_USER_PASSWORD + " TEXT" +
                ");";
    }
}
