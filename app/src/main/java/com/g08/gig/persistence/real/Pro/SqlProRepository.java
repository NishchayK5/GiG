package com.g08.gig.persistence.real.Pro;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.g08.gig.persistence.ProRepository;
import com.g08.gig.models.Pro;
import com.g08.gig.persistence.SkillRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Skills.SkillTable;
import com.g08.gig.persistence.real.user.UserTable;

import java.util.ArrayList;
import java.util.List;
public class SqlProRepository implements ProRepository{
    private final AppDbHelper dbHelper;
    private final SkillRepository proSkillRepo;
    public SqlProRepository(AppDbHelper dbHelper, SkillRepository proSkillRepo) {
        this.dbHelper = dbHelper;
        this.proSkillRepo = proSkillRepo;
    }

    @Override
    public List<Pro> getAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String SQL_statement = "SELECT users." + UserTable.COL_USER_ID + ", "+
                "users." + UserTable.COL_USER_EMAIL + ", "+
                "users." + UserTable.COL_USER_FIRST_NAME + ", "+
                "users." + UserTable.COL_USER_LAST_NAME + ", "+
                "users." + UserTable.COL_USER_PHONE + ", "+
                "pros." + ProTable.COL_PRO_CITY_LOCATION+", "+
                "users." + UserTable.COL_USER_PASSWORD +  " FROM "
                + UserTable.TABLE_USER + " JOIN "+ ProTable.TABLE_PRO +
                " ON pros." + ProTable.COL_PRO_ID + " = users." + UserTable.COL_USER_ID + ";";
        Cursor c = db.rawQuery(SQL_statement, null);
        List<Pro> result = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                result.add(cursorToPro(c));
            }
        } finally {
            c.close();
        }
        return result;
    }
    private Pro cursorToPro(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(UserTable.COL_USER_ID));
        String email = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_EMAIL));
        String firstName = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_FIRST_NAME));
        String lastName = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_LAST_NAME));
        String phoneNumber = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_PHONE));
        String password = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_PASSWORD));
        String cityOfOperation = c.getString(c.getColumnIndexOrThrow(ProTable.COL_PRO_CITY_LOCATION));
        Pro base = new Pro(id, email, firstName, lastName, phoneNumber, password, null, cityOfOperation);
        String[] skills = proSkillRepo.getAllSkillsByPro(base);
        return new Pro(id, email, firstName, lastName, phoneNumber, password, skills, cityOfOperation);
    }
    @Override
    public Pro getById(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String SQL_statement = "SELECT users." + UserTable.COL_USER_ID + ", "+
                "users." + UserTable.COL_USER_EMAIL + ", "+
                "users." + UserTable.COL_USER_FIRST_NAME + ", "+
                "users." + UserTable.COL_USER_LAST_NAME + ", "+
                "users." + UserTable.COL_USER_PHONE + ", "+
                "pros." + ProTable.COL_PRO_CITY_LOCATION+", "+
                "users." + UserTable.COL_USER_PASSWORD +  " FROM "
                + UserTable.TABLE_USER + " JOIN "+ ProTable.TABLE_PRO +
                " ON pros." + ProTable.COL_PRO_ID + " = users." + UserTable.COL_USER_ID +
                " WHERE users." + UserTable.COL_USER_ID + " =?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor c = db.rawQuery(SQL_statement, selectionArgs);
        try {
            if (!c.moveToFirst()) return null;
            return cursorToPro(c);
        } finally {
            c.close();
        }
    }

    @Override
    public Pro add(Pro pro){
        if(pro==null) return null;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues proValues = new ContentValues();
            proValues.put(ProTable.COL_PRO_ID, pro.getId());
            proValues.put(ProTable.COL_PRO_CITY_LOCATION, pro.getCityOfOperation());
            long result = db.insert(ProTable.TABLE_PRO,null, proValues);
            if(result==-1) return null;
            if (!insertSkillsForPro(db, pro.getId(), pro.getSkillSet())) return null;
            db.setTransactionSuccessful();
            return getById(pro.getId());
        }
        finally{
            db.endTransaction();
        }
    }

    @Override
    public boolean update(Pro pro){
        if(pro==null) return false;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues userValues = new ContentValues();
            userValues.put(UserTable.COL_USER_EMAIL, pro.getEmail());
            userValues.put(UserTable.COL_USER_FIRST_NAME, pro.getFirstName());
            userValues.put(UserTable.COL_USER_LAST_NAME, pro.getLastName());
            userValues.put(UserTable.COL_USER_PHONE, pro.getPhoneNumber());
            userValues.put(UserTable.COL_USER_PASSWORD, pro.getPassword());

            int rows = db.update(UserTable.TABLE_USER, userValues, UserTable.COL_USER_ID + "=?", new String[]{String.valueOf(pro.getId())});
            if(rows ==0) return false;
            ContentValues proValues = new ContentValues();
            proValues.put(ProTable.COL_PRO_ID, pro.getId());
            proValues.put(ProTable.COL_PRO_CITY_LOCATION, pro.getCityOfOperation());
            int pRows = db.update(ProTable.TABLE_PRO, proValues, ProTable.COL_PRO_ID + "=?", new String[]{String.valueOf(pro.getId())} );
            if(pRows==0) return false;

            proSkillRepo.deleteAllSkillsByProId(pro.getId());
            if (!insertSkillsForPro(db, pro.getId(), pro.getSkillSet())) return false;

            db.setTransactionSuccessful();
            return true;
        } finally{
            db.endTransaction();
        }
    }

    @Override
    public boolean delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(UserTable.TABLE_USER,
                UserTable.COL_USER_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        return rows>0;
    }

    private static boolean insertSkillsForPro(SQLiteDatabase db, int proId, String[] skills) {
        if (skills == null || skills.length == 0) return true;
        for (String s : skills) {
            ContentValues values = new ContentValues();
            values.put(SkillTable.COL_PRO_ID, String.valueOf(proId));
            values.put(SkillTable.COL_SKILL, s);
            if (db.insert(SkillTable.TABLE_SKILLS, null, values) == -1) return false;
        }
        return true;
    }
}
