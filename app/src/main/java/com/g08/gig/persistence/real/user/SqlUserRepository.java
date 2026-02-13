package com.g08.gig.persistence.real.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.g08.gig.persistence.UserRepository;
import com.g08.gig.models.User;
import com.g08.gig.persistence.real.AppDbHelper;

import java.util.ArrayList;
import java.util.List;

public class SqlUserRepository implements UserRepository {

    private final AppDbHelper dbHelper;

    public SqlUserRepository(AppDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<User> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                UserTable.TABLE_USER,
                new String[]{UserTable.COL_USER_ID, UserTable.COL_USER_EMAIL, UserTable.COL_USER_FIRST_NAME, UserTable.COL_USER_LAST_NAME, UserTable.COL_USER_PHONE, UserTable.COL_USER_PASSWORD},
                null, null, null, null,
                UserTable.COL_USER_ID + " DESC"
        );

        List<User> result = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                result.add(cursorToUser(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    @Override
    public User getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                UserTable.TABLE_USER,
                new String[]{UserTable.COL_USER_ID, UserTable.COL_USER_EMAIL, UserTable.COL_USER_FIRST_NAME, UserTable.COL_USER_LAST_NAME, UserTable.COL_USER_PHONE, UserTable.COL_USER_PASSWORD},
                UserTable.COL_USER_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        try {
            if (!c.moveToFirst()) return null;
            return cursorToUser(c);
        } finally {
            c.close();
        }
    }

    @Override
    public User getByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                UserTable.TABLE_USER,
                new String[]{UserTable.COL_USER_ID, UserTable.COL_USER_EMAIL, UserTable.COL_USER_FIRST_NAME, UserTable.COL_USER_LAST_NAME, UserTable.COL_USER_PHONE, UserTable.COL_USER_PASSWORD},
                UserTable.COL_USER_EMAIL + "=?",
                new String[]{email.trim()},
                null, null, null
        );
        try {
            if (!c.moveToFirst()) return null;
            return cursorToUser(c);
        } finally {
            c.close();
        }
    }

    @Override
    public User add(User user) {
        if (user == null) return null;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(UserTable.COL_USER_EMAIL, user.getEmail());
            values.put(UserTable.COL_USER_FIRST_NAME, user.getFirstName());
            values.put(UserTable.COL_USER_LAST_NAME, user.getLastName());
            values.put(UserTable.COL_USER_PHONE, user.getPhoneNumber());
            values.put(UserTable.COL_USER_PASSWORD, user.getPassword());

            long newId = db.insert(UserTable.TABLE_USER, null, values);
            if (newId == -1) return null;

            int userId = (int) newId;

            db.setTransactionSuccessful();
            return getById(userId);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean update(User user) {
        if (user == null) return false;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(UserTable.COL_USER_EMAIL, user.getEmail());
            values.put(UserTable.COL_USER_FIRST_NAME, user.getFirstName());
            values.put(UserTable.COL_USER_LAST_NAME, user.getLastName());
            values.put(UserTable.COL_USER_PHONE, user.getPhoneNumber());
            values.put(UserTable.COL_USER_PASSWORD, user.getPassword());

            int rows = db.update(
                    UserTable.TABLE_USER,
                    values,
                    UserTable.COL_USER_ID + "=?",
                    new String[]{String.valueOf(user.getId())}
            );

            if (rows == 0) return false;

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                UserTable.TABLE_USER,
                UserTable.COL_USER_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        return rows > 0;
    }

    private User cursorToUser(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(UserTable.COL_USER_ID));
        String email = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_EMAIL));
        String firstName = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_FIRST_NAME));
        String lastName = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_LAST_NAME));
        String phoneNumber = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_PHONE));
        String password = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_PASSWORD));
        return new User(id, email, firstName, lastName, phoneNumber, password);
    }
}
